package no.java.conf.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import no.java.conf.model.EndpointConfig
import no.java.conf.service.SleepingPillService

fun Application.configureSessionRouting() {
    val sleepingPillService =
        SleepingPillService(httpClient(), environment.config.configList("javazone.sleepingpill").map {
            EndpointConfig(it.property("year").getString().toInt(), it.property("endpoint").getString())
        })

    routing {
        get("/api/videos") {
            call.respond(sleepingPillService.allVideos().map { it.title to it.video })
        }
    }
}
