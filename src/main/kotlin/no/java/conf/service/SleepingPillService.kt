package no.java.conf.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import no.java.conf.config.EndpointConfig
import no.java.conf.model.sessions.Session
import no.java.conf.model.sleepingpill.SPSession
import no.java.conf.model.sleepingpill.SPSessions
import no.java.conf.model.sleepingpill.toSession

data class EndpointSessions(
    val year: Int,
    val endpoint: String,
    val sessions: List<SPSession>,
)

private val logger = KotlinLogging.logger {}

class SleepingPillService(
    private val client: HttpClient,
    private val endpoints: List<EndpointConfig>,
) {
    private lateinit var sessions: List<Session>

    suspend fun retrieve(): List<Session> {
        sessions =
            coroutineScope {
                endpoints
                    .map { endpoint ->
                        async {
                            fetchEndpoint(endpoint)
                        }
                    }.awaitAll()
                    .map {
                        it.sessions.map { session ->
                            session.toSession(it.year)
                        }
                    }.flatten()
                    .also { logger.debug { "SleepingPill - sessions: ${it.count()}" } }
            }

        return sessions
    }

    private suspend fun fetchEndpoint(endpoint: EndpointConfig) =
        EndpointSessions(
            endpoint.year,
            endpoint.endpoint,
            client.get(endpoint.endpoint).body<SPSessions>().sessions,
        )
}
