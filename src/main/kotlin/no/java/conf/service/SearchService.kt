package no.java.conf.service

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.jillesvangurp.jsondsl.json
import com.jillesvangurp.ktsearch.Aggregations
import com.jillesvangurp.ktsearch.BulkItemCallBack
import com.jillesvangurp.ktsearch.BulkResponse
import com.jillesvangurp.ktsearch.OperationType
import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.bulk
import com.jillesvangurp.ktsearch.count
import com.jillesvangurp.ktsearch.createIndex
import com.jillesvangurp.ktsearch.deleteIndex
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.parsedBuckets
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.ktsearch.termsResult
import com.jillesvangurp.searchdsls.querydsl.SearchDSL
import com.jillesvangurp.searchdsls.querydsl.TermsAgg
import com.jillesvangurp.searchdsls.querydsl.agg
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.exists
import com.jillesvangurp.searchdsls.querydsl.nested
import com.jillesvangurp.searchdsls.querydsl.simpleQueryString
import com.jillesvangurp.searchdsls.querydsl.terms
import com.jillesvangurp.serializationext.DEFAULT_JSON
import io.github.oshai.kotlinlogging.KotlinLogging
import no.java.conf.model.AggregationsNotFound
import no.java.conf.model.ApiError
import no.java.conf.model.IndexNotReady
import no.java.conf.model.SearchMissing
import no.java.conf.model.search.AggregateResponse
import no.java.conf.model.search.FormatAggregate
import no.java.conf.model.search.LanguageAggregate
import no.java.conf.model.search.SearchResponse
import no.java.conf.model.search.SessionResponse
import no.java.conf.model.search.TextSearchRequest
import no.java.conf.model.search.VideoSearchResponse
import no.java.conf.model.search.YearAggregate
import no.java.conf.model.search.hasFilter
import no.java.conf.model.search.hasQuery
import no.java.conf.model.sessions.Session
import no.java.conf.model.sessions.Speaker
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue

private const val INDEX_NAME = "javazone"

private const val REPLICAS = 0
private const val SHARDS = 3
private const val REFRESH = 10

private val logger = KotlinLogging.logger {}

enum class State {
    NEW,
    MAPPED,
    INDEXED,
}

class SearchService(
    private val client: SearchClient,
    private val skipIndex: Boolean,
) {
    private var readyState = State.NEW

    suspend fun setup() {
        if (readyState != State.NEW) {
            return
        }

        if (!skipIndex) {
            client.deleteIndex(INDEX_NAME, ignoreUnavailable = true)

            client.createIndex(INDEX_NAME) {
                settings {
                    replicas = REPLICAS
                    shards = SHARDS
                    refreshInterval = REFRESH.seconds
                }
                mappings(dynamicEnabled = false) {
                    text(Session::title)
                    text(Session::abstract)
                    text(Session::intendedAudience)
                    keyword(Session::year)
                    keyword(Session::video)
                    keyword(Session::sessionId)
                    keyword(Session::format)
                    keyword(Session::language)
                    nestedField("speakers") {
                        text(Speaker::name)
                        keyword(Speaker::twitter)
                        text(Speaker::bio)
                    }
                }
            }
        }

        readyState = State.MAPPED
    }

    suspend fun ingest(sessions: List<Session>) {
        if (readyState != State.MAPPED) {
            return
        }

        if (!skipIndex) {
            val itemCallBack =
                object : BulkItemCallBack {
                    override fun bulkRequestFailed(
                        e: Exception,
                        ops: List<Pair<String, String?>>,
                    ) {
                        logger.error(e) { "Bulk failed" }
                    }

                    override fun itemFailed(
                        operationType: OperationType,
                        item: BulkResponse.ItemDetails,
                    ) {
                        logger.warn { "${operationType.name} failed ${item.id} with ${item.status}" }
                    }

                    override fun itemOk(
                        operationType: OperationType,
                        item: BulkResponse.ItemDetails,
                    ) {
                        logger.trace {
                            "${operationType.name} completed ${item.id} seq ${item.seqNo} p_term ${item.primaryTerm}"
                        }
                    }
                }

            val timeTaken =
                measureTimedValue {
                    client.bulk(callBack = itemCallBack) {
                        sessions.forEach { session ->
                            index(
                                source = DEFAULT_JSON.encodeToString(Session.serializer(), session),
                                index = INDEX_NAME,
                                id = session.sessionId,
                            )
                        }
                    }
                }

            logger.info { "Time taken to index - $timeTaken" }
        } else {
            logger.info { "Indexing skipped" }
        }

        readyState = State.INDEXED
    }

    context(Raise<ApiError>)
    suspend fun allVideos(): List<VideoSearchResponse> {
        ensureReady()

        val docCount = totalDocs()

        val sessions =
            client.search(INDEX_NAME) {
                resultSize = docCount.toInt()
                query = exists("video")
            }

        return sessions.parseHits<VideoSearchResponse>().sortedBy { -it.year }
    }

    private fun Raise<ApiError>.ensureReady() {
        ensure(readyState == State.INDEXED) {
            raise(IndexNotReady)
        }
    }

    fun state(): State = readyState

    private fun Aggregations.buildResponse() = AggregateResponse(
        languages = this.termsResult("by-language")
            .parsedBuckets.map { LanguageAggregate(it.parsed.key, it.parsed.docCount) },
        formats = this.termsResult("by-format")
            .parsedBuckets.map { FormatAggregate(it.parsed.key, it.parsed.docCount) },
        years = this.termsResult("by-year")
            .parsedBuckets.map { YearAggregate(it.parsed.key.toInt(), it.parsed.docCount) }
            .sortedBy { -it.year },
    )

    context(Raise<ApiError>)
    suspend fun textSearch(searchRequest: TextSearchRequest?): SearchResponse {
        ensureNotNull(searchRequest) {
            raise(SearchMissing)
        }

        ensureReady()

        val docCount = totalDocs()

        val searchResult =
            client.search(INDEX_NAME) {
                addQuery(docCount, searchRequest)

                addLanguageAggregate(docCount)
                addFormatAggregate(docCount)
                addYearAggregate(docCount)

                logger.debug { this.json() }
            }

        ensure(searchResult.aggregations != null) {
            raise(AggregationsNotFound)
        }

        return SearchResponse(
            searchResult.parseHits<SessionResponse>().sortedBy { -it.year },
            searchResult.aggregations!!.buildResponse()
        )
    }

    private fun SearchDSL.addLanguageAggregate(docCount: Long) {
        this.agg("by-language", TermsAgg(Session::language.name) {
            aggSize = docCount
            minDocCount = 1
        })
    }

    private fun SearchDSL.addFormatAggregate(docCount: Long) {
        this.agg("by-format", TermsAgg(Session::format.name) {
            aggSize = docCount
            minDocCount = 1
        })
    }

    private fun SearchDSL.addYearAggregate(docCount: Long) {
        this.agg("by-year", TermsAgg(Session::year) {
            aggSize = docCount
            minDocCount = 1
        })
    }

    private fun SearchDSL.addQuery(docCount: Long, searchRequest: TextSearchRequest) {
        logger.debug { "Building query for $searchRequest" }

        resultSize = when (!searchRequest.hasQuery() && !searchRequest.hasFilter()) {
            true -> 0
            false -> docCount.toInt()
        }

        val queryString = when {
            searchRequest.hasQuery() -> searchRequest.query
            searchRequest.hasFilter() -> "*"
            else -> null
        }

        val textQuery = queryString?.let { q ->
            bool {
                should(
                    simpleQueryString(q, "title", "abstract", "intendedAudience"),
                    nested {
                        path = "speakers"
                        query = simpleQueryString(q, "speakers.name", "speakers.bio")
                    }
                )
            }
        }

        val yearQuery = searchRequest.years.ifEmpty { null }?.let { years ->
            terms(
                field = "year",
                values = years.map { it.toString() }.toTypedArray()
            )
        }

        val languageQuery = searchRequest.languages.ifEmpty { null }?.let { languages ->
            terms(
                field = "language",
                values = languages.toTypedArray()
            )
        }

        val formatQuery = searchRequest.formats.ifEmpty { null }?.let { formats ->
            terms(
                field = "format",
                values = formats.toTypedArray()
            )
        }

        this.query = bool {
            must(
                listOfNotNull(textQuery, yearQuery, languageQuery, formatQuery)
            )
        }
    }

    private suspend fun totalDocs() = client.count(INDEX_NAME).count
}

/*

 */