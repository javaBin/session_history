package no.java.conf.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import no.java.conf.model.EndpointConfig
import no.java.conf.service.SearchService
import no.java.conf.service.SleepingPillService

fun Application.configureSessionRouting() {
    val searchService = SearchService(client = searchClient())

    val sleepingPillService = SleepingPillService(
        client = httpClient(),
        endpoints = environment.config.configList("javazone.sleepingpill").map {
            EndpointConfig(it.property("year").getString().toInt(), it.property("endpoint").getString())
        },
        searchService = searchService
    )

    routing {
        get("/api/videos") {
            call.respond(sleepingPillService.allVideos().map { it.title to it.video })
        }
    }
}
