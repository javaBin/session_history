package no.java.conf.plugins

import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import no.java.conf.model.EndpointConfig
import no.java.conf.service.SearchService
import no.java.conf.service.SleepingPillService

fun Application.sleepingPillService(
    httpClient: HttpClient,
    searchService: SearchService,
) = SleepingPillService(
    client = httpClient,
    endpoints =
        environment.config.configList("javazone.sleepingpill").map {
            EndpointConfig(it.property("year").getString().toInt(), it.property("endpoint").getString())
        },
    searchService = searchService,
)
