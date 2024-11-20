package no.java.conf.service.search

import com.jillesvangurp.ktsearch.BulkItemCallBack
import com.jillesvangurp.ktsearch.BulkResponse
import com.jillesvangurp.ktsearch.OperationType
import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.bulk
import com.jillesvangurp.serializationext.DEFAULT_JSON
import io.github.oshai.kotlinlogging.KotlinLogging
import no.java.conf.model.sessions.Session

private val logger = KotlinLogging.logger {}

class ElasticIngester(
    private val client: SearchClient,
) {
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

    suspend fun ingest(
        indexName: String,
        sessions: List<Session>
    ) {
        client.bulk(callBack = itemCallBack) {
            sessions.forEach { session ->
                index(
                    source = DEFAULT_JSON.encodeToString(Session.serializer(), session),
                    index = indexName,
                    id = session.sessionId,
                )
            }
        }
    }
}
