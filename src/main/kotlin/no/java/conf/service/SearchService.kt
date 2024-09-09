package no.java.conf.service

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.jillesvangurp.jsondsl.json
import com.jillesvangurp.ktsearch.BulkItemCallBack
import com.jillesvangurp.ktsearch.BulkResponse
import com.jillesvangurp.ktsearch.OperationType
import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.bulk
import com.jillesvangurp.ktsearch.count
import com.jillesvangurp.ktsearch.createIndex
import com.jillesvangurp.ktsearch.deleteIndex
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.SearchDSL
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.exists
import com.jillesvangurp.searchdsls.querydsl.nested
import com.jillesvangurp.searchdsls.querydsl.simpleQueryString
import com.jillesvangurp.serializationext.DEFAULT_JSON
import io.github.oshai.kotlinlogging.KotlinLogging
import no.java.conf.model.ApiError
import no.java.conf.model.IndexNotReady
import no.java.conf.model.SearchMissing
import no.java.conf.model.search.SessionResponse
import no.java.conf.model.search.TextSearchRequest
import no.java.conf.model.search.VideoSearchResponse
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
                resultSize = docCount
                query = exists("video")
            }

        return sessions.parseHits<VideoSearchResponse>()
    }

    private fun Raise<ApiError>.ensureReady() {
        ensure(readyState == State.INDEXED) {
            raise(IndexNotReady)
        }
    }

    fun state(): State = readyState

    context(Raise<ApiError>)
    suspend fun textSearch(searchRequest: TextSearchRequest?): List<SessionResponse> {
        ensure(searchRequest != null && searchRequest.query.isNotBlank()) {
            raise(SearchMissing)
        }

        ensureReady()

        val docCount = totalDocs()

        val sessions =
            client.search(INDEX_NAME) {
                resultSize = docCount
                query = bool {
                    should(
                        simpleQueryString(searchRequest.query, "title", "abstract"),
                        nested {
                            path = "speakers"
                            query = simpleQueryString(searchRequest.query, "speakers.name", "speakers.bio")
                        }
                    )
                }
                logger.debug { this.json() }
            }

        return sessions.parseHits<SessionResponse>()
    }



    private suspend fun totalDocs() = client.count(INDEX_NAME).count.toInt()
}
