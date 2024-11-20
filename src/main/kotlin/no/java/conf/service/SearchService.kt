package no.java.conf.service

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.jillesvangurp.jsondsl.json
import com.jillesvangurp.ktsearch.Aggregations
import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.count
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
import no.java.conf.service.search.ElasticIndexer
import no.java.conf.service.search.ElasticIngester
import kotlin.time.measureTimedValue

private const val INDEX_NAME = "javazone"

private val logger = KotlinLogging.logger {}

enum class State {
    NEW,
    MAPPED,
    INDEXED,
}

class SearchService(
    private val client: SearchClient,
    private val indexer: ElasticIndexer,
    private val ingester: ElasticIngester,
    private val skipIndex: Boolean,
) {
    private var readyState = State.NEW

    suspend fun setup() {
        logger.debug { "Setting up SearchService" }

        if (readyState != State.NEW) {
            logger.debug { "Incorrect search state - was $readyState" }

            return
        }

        if (!skipIndex) {
            logger.debug { "Creating index" }

            indexer.recreateIndex(INDEX_NAME)
        }

        logger.debug { "State -> Mapped" }

        readyState = State.MAPPED
    }

    suspend fun ingest(sessions: List<Session>) {
        logger.debug { "Ingesting" }

        if (readyState != State.MAPPED) {
            logger.debug { "Incorrect search state - was $readyState" }

            return
        }

        if (!skipIndex) {
            logger.debug { "Bulk" }

            val timeTaken = measureTimedValue { ingester.ingest(INDEX_NAME, sessions) }

            logger.info { "Time taken to index - $timeTaken" }
        } else {
            logger.info { "Indexing skipped" }
        }

        logger.debug { "State -> Indexed" }

        readyState = State.INDEXED
    }

    context(Raise<ApiError>)
    suspend fun allVideos(): List<VideoSearchResponse> {
        ensureReady(readyState)

        val docCount = client.totalDocs()

        val sessions =
            client.search(INDEX_NAME) {
                resultSize = docCount.toInt()
                query = exists("video")
            }

        return sessions.parseHits<VideoSearchResponse>().sortedBy { -it.year }
    }

    fun state(): State = readyState

    context(Raise<ApiError>)
    suspend fun textSearch(searchRequest: TextSearchRequest?): SearchResponse {
        ensureNotNull(searchRequest) {
            raise(SearchMissing)
        }

        ensureReady(readyState)

        val docCount = client.totalDocs()

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
}

private fun Raise<ApiError>.ensureReady(readyState: State) {
    ensure(readyState == State.INDEXED) {
        raise(IndexNotReady)
    }
}

private fun Aggregations.buildResponse() =
    AggregateResponse(
        languages =
            this
                .termsResult("by-language")
                .parsedBuckets
                .map { LanguageAggregate(it.parsed.key, it.parsed.docCount) },
        formats =
            this
                .termsResult("by-format")
                .parsedBuckets
                .map { FormatAggregate(it.parsed.key, it.parsed.docCount) },
        years =
            this
                .termsResult("by-year")
                .parsedBuckets
                .map { YearAggregate(it.parsed.key.toInt(), it.parsed.docCount) }
                .sortedBy { -it.year },
    )

private fun SearchDSL.addLanguageAggregate(docCount: Long) {
    this.agg(
        "by-language",
        TermsAgg(Session::language.name) {
            aggSize = docCount
            minDocCount = 1
        }
    )
}

private fun SearchDSL.addFormatAggregate(docCount: Long) {
    this.agg(
        "by-format",
        TermsAgg(Session::format.name) {
            aggSize = docCount
            minDocCount = 1
        }
    )
}

private fun SearchDSL.addYearAggregate(docCount: Long) {
    this.agg(
        "by-year",
        TermsAgg(Session::year) {
            aggSize = docCount
            minDocCount = 1
        }
    )
}

private fun SearchDSL.addQuery(
    docCount: Long,
    searchRequest: TextSearchRequest
) {
    logger.debug { "Building query for $searchRequest" }

    resultSize =
        when (!searchRequest.hasQuery() && !searchRequest.hasFilter()) {
            true -> 0
            false -> docCount.toInt()
        }

    val queryString =
        when {
            searchRequest.hasQuery() -> searchRequest.query
            searchRequest.hasFilter() -> "*"
            else -> null
        }

    val textQuery =
        queryString?.let { q ->
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

    val yearQuery =
        searchRequest.years.ifEmpty { null }?.let { years ->
            terms(
                field = "year",
                values = years.map { it.toString() }.toTypedArray()
            )
        }

    val languageQuery =
        searchRequest.languages.ifEmpty { null }?.let { languages ->
            terms(
                field = "language",
                values = languages.toTypedArray()
            )
        }

    val formatQuery =
        searchRequest.formats.ifEmpty { null }?.let { formats ->
            terms(
                field = "format",
                values = formats.toTypedArray()
            )
        }

    this.query =
        bool {
            must(
                listOfNotNull(textQuery, yearQuery, languageQuery, formatQuery)
            )
        }
}

private suspend fun SearchClient.totalDocs() = this.count(INDEX_NAME).count
