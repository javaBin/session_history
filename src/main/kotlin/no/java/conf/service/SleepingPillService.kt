package no.java.conf.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient

private val logger = KotlinLogging.logger {}

class SleepingPillService(val client: HttpClient, private val endpoints: List<String>) {

    init {
        logger.debug { "SleepingPill - endpoints: $endpoints" }
    }
}