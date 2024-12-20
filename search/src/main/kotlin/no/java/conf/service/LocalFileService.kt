package no.java.conf.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import no.java.conf.config.LocalFileConfig
import no.java.conf.model.sessions.Session
import no.java.conf.model.sleepingpill.SPSessions
import no.java.conf.model.sleepingpill.toSession

private val logger = KotlinLogging.logger {}

class LocalFileService(
    private val filenames: List<LocalFileConfig>
) {
    private lateinit var sessions: List<Session>

    private val json =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }

    fun read(): List<Session> {
        sessions =
            filenames
                .map {
                    val fileContent = javaClass.getResource(it.filename)?.readText() ?: "[]"

                    val decodedSessions = json.decodeFromString<SPSessions>(fileContent)

                    decodedSessions.sessions.map { session ->
                        session.toSession(it.year).copy(
                            id = "Local-${it.year}-${session.id}",
                            sessionId = "Local-${it.year}-${session.id}",
                            conferenceId = "Local-${it.year}",
                        )
                    }
                }.flatten()
                .also { logger.debug { "Local File - sessions: ${it.count()}" } }

        return sessions
    }
}
