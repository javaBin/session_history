package no.java.conf.service

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.jillesvangurp.ktsearch.BulkItemCallBack
import com.jillesvangurp.ktsearch.BulkResponse
import com.jillesvangurp.ktsearch.OperationType
import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.bulk
import com.jillesvangurp.ktsearch.createIndex
import com.jillesvangurp.ktsearch.deleteIndex
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import no.java.conf.model.sessions.Session
import no.java.conf.model.sessions.Speaker
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue

private const val INDEX_NAME = "javazone"

private val logger = KotlinLogging.logger {}

class SearchService(private val client: SearchClient) {

    init {
        runBlocking(Dispatchers.IO) {
            client.deleteIndex(INDEX_NAME)

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
        }
    }

    private val mapper = jsonMapper()

    suspend fun ingest(sessions: List<Session>) {

        val itemCallBack = object : BulkItemCallBack {
            override fun bulkRequestFailed(e: Exception, ops: List<Pair<String, String?>>) {
                logger.error(e) { "Bulk failed" }
            }

            override fun itemFailed(operationType: OperationType, item: BulkResponse.ItemDetails) {
                logger.warn { "${operationType.name} failed  ${item.id} with ${item.status}" }
            }

            override fun itemOk(operationType: OperationType, item: BulkResponse.ItemDetails) {
                logger.info { "operation $operationType completed - id: ${item.id} - sq_no: ${item.seqNo} - primary_term ${item.primaryTerm}" }
            }
        }

        val timeTaken = measureTimedValue {
            client.bulk(callBack = itemCallBack) {
                sessions.forEach { session ->
                    index(
                        source = mapper.writeValueAsString(session),
                        index = INDEX_NAME,
                        id = session.sessionId
                    )
                }
            }
        }

        logger.info { "Time taken to index - $timeTaken" }
    }
}
