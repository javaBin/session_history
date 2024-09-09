package no.java.conf

import io.ktor.server.application.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.java.conf.plugins.configureMonitoring
import no.java.conf.plugins.configureRouting
import no.java.conf.plugins.configureSearchRouting
import no.java.conf.plugins.configureSecurity
import no.java.conf.plugins.configureSerialization
import no.java.conf.plugins.httpClient
import no.java.conf.plugins.localFileService
import no.java.conf.plugins.searchService
import no.java.conf.plugins.sleepingPillService

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain
        .main(args)
}

fun Application.module() {
    val searchService = searchService()
    val sleepingPillService = sleepingPillService(httpClient())
    val localFileService = localFileService()

    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting()
    configureSearchRouting(searchService)

    val scope = CoroutineScope(Dispatchers.IO)

    scope.launch {
        searchService.setup()
        val spSessions = sleepingPillService.retrieve()
        val localSessions = localFileService.read()
        searchService.ingest(localSessions + spSessions)
    }
}
