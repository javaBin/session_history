package no.java.conf.service.search

import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.createIndex
import com.jillesvangurp.ktsearch.deleteIndex
import no.java.conf.model.sessions.Session
import no.java.conf.model.sessions.Speaker
import kotlin.time.Duration.Companion.seconds

private const val REPLICAS = 0
private const val SHARDS = 3
private const val REFRESH = 10

class ElasticIndexer(
    private val client: SearchClient,
) {
    suspend fun recreateIndex(indexName: String) {
        client.deleteIndex(indexName, ignoreUnavailable = true)

        client.createIndex(indexName) {
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
}
