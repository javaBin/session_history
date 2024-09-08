package no.java.conf.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import no.java.conf.model.EndpointConfig
import no.java.conf.model.sessions.Session
import no.java.conf.model.sleepingpill.SPSession
import no.java.conf.model.sleepingpill.SPSessions
import no.java.conf.model.sleepingpill.toSession

data class EndpointSessions(
    val year: Int, val endpoint: String, val sessions: List<SPSession>
)

private val logger = KotlinLogging.logger {}

class SleepingPillService(
    private val client: HttpClient,
    private val endpoints: List<EndpointConfig>,
    private val searchService: SearchService
) {

    private val sessions: List<Session> by lazy {
        runBlocking(Dispatchers.IO) {
            coroutineScope {
                endpoints.map { endpoint ->
                    async {
                        fetchEndpoint(endpoint)
                    }
                }.awaitAll().map {
                    it.sessions.map { session ->
                        session.toSession(it.year)
                    }
                }.flatten()
            }
        }
    }

    init {
        logger.debug { "SleepingPill - endpoints: $endpoints" }

        logger.debug { "SleepingPill - sessions: ${sessions.count()}" }

        runBlocking {
            searchService.ingest(sessions)
        }
    }

    private suspend fun fetchEndpoint(endpoint: EndpointConfig) =
        EndpointSessions(
            endpoint.year,
            endpoint.endpoint,
            client.get(endpoint.endpoint).body<SPSessions>().sessions
        )

    fun allVideos() = sessions.filter { it.hasVideo() }
}