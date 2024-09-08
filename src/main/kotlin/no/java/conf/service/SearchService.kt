package no.java.conf.service

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.jillesvangurp.ktsearch.BulkItemCallBack
import com.jillesvangurp.ktsearch.BulkResponse
import com.jillesvangurp.ktsearch.OperationType
import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.SearchResponse
import com.jillesvangurp.ktsearch.bulk
import com.jillesvangurp.ktsearch.count
import com.jillesvangurp.ktsearch.createIndex
import com.jillesvangurp.ktsearch.deleteIndex
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.exists
import com.jillesvangurp.serializationext.DEFAULT_JSON
import io.github.oshai.kotlinlogging.KotlinLogging
import no.java.conf.model.ApiError
import no.java.conf.model.IndexNotReady
import no.java.conf.model.search.VideoSearchResult
import no.java.conf.model.sessions.Session
import no.java.conf.model.sessions.Speaker
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue

private const val INDEX_NAME = "javazone"

private val logger = KotlinLogging.logger {}

enum class State {
    NEW,
    MAPPED,
    INDEXED
}

class SearchService(private val client: SearchClient) {
    private var readyState = State.NEW

    suspend fun setup() {
        if (readyState != State.NEW) {
            return
        }

        client.deleteIndex(INDEX_NAME, ignoreUnavailable = true)

        client.createIndex(INDEX_NAME) {
            settings {
                replicas = 0
                shards = 3
                refreshInterval = 10.seconds
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

        readyState = State.MAPPED
    }

    suspend fun ingest(sessions: List<Session>) {
        if (readyState != State.MAPPED) {
            return
        }

        val itemCallBack = object : BulkItemCallBack {
            override fun bulkRequestFailed(e: Exception, ops: List<Pair<String, String?>>) {
                logger.error(e) { "Bulk failed" }
            }

            override fun itemFailed(operationType: OperationType, item: BulkResponse.ItemDetails) {
                logger.warn { "${operationType.name} failed  ${item.id} with ${item.status}" }
            }

            override fun itemOk(operationType: OperationType, item: BulkResponse.ItemDetails) {
                logger.trace { "operation $operationType completed - id: ${item.id} - sq_no: ${item.seqNo} - primary_term ${item.primaryTerm}" }
            }
        }

        val timeTaken = measureTimedValue {
            client.bulk(callBack = itemCallBack) {
                sessions.forEach { session ->
                    index(
                        source = DEFAULT_JSON.encodeToString(Session.serializer(), session),
                        index = INDEX_NAME,
                        id = session.sessionId
                    )
                }
            }
        }

        logger.info { "Time taken to index - $timeTaken" }

        readyState = State.INDEXED
    }

    context(Raise<ApiError>)
    suspend fun allVideos(): List<VideoSearchResult> {
        ensure(readyState == State.INDEXED) {
            raise(IndexNotReady)
        }

        val docs = client.count(INDEX_NAME).count

        val sessions: SearchResponse = client.search(INDEX_NAME) {
            resultSize = docs.toInt()
            query = exists("video")
        }

        return sessions.parseHits<VideoSearchResult>()
    }

    fun state(): State {
        return readyState
    }

}
