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
import no.java.conf.plugins.searchClient
import no.java.conf.plugins.sleepingPillService
import no.java.conf.service.SearchService

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain
        .main(args)
}

fun Application.module() {
    val searchClient = searchClient()
    val searchService = SearchService(searchClient)
    val sleepingPillService = sleepingPillService(httpClient(), searchService)

    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting()
    configureSearchRouting(searchService)

    val scope = CoroutineScope(Dispatchers.IO)

    scope.launch {
        searchService.setup()
        sleepingPillService.retrieve()
    }
}
