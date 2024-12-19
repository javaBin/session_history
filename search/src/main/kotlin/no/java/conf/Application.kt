package no.java.conf

import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.cio.EngineMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.java.conf.config.EndpointConfig
import no.java.conf.config.LocalFileConfig
import no.java.conf.plugins.configureMonitoring
import no.java.conf.plugins.configureRouting
import no.java.conf.plugins.configureSearchRouting
import no.java.conf.plugins.configureSerialization
import no.java.conf.plugins.httpClient
import no.java.conf.plugins.searchClient
import no.java.conf.plugins.searchService
import no.java.conf.service.LocalFileService
import no.java.conf.service.SleepingPillService

fun main(args: Array<String>) {
    EngineMain
        .main(args)
}

fun localFileService(config: List<LocalFileConfig>) = LocalFileService(filenames = config)

fun sleepingPillService(
    httpClient: HttpClient,
    endpoints: List<EndpointConfig>
) = SleepingPillService(
    client = httpClient,
    endpoints = endpoints,
)

fun Application.module() {
    val searchClient = searchClient(environment.searchClientConfig())
    val searchService = searchService(environment.searchServiceConfig(searchClient))

    val sleepingPillService = sleepingPillService(httpClient(), environment.endpointConfig())
    val localFileService = localFileService(environment.localFileConfig())

    configureSerialization()
    configureMonitoring()
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
