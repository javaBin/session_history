package no.java.conf.plugins

import arrow.core.raise.either
import com.jillesvangurp.ktsearch.KtorRestClient
import com.jillesvangurp.ktsearch.SearchClient
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import no.java.conf.service.SearchService

fun Application.searchClient() =
    SearchClient(
        KtorRestClient(
            host = environment.config.property("elastic.host").getString(),
            port =
                environment.config
                    .property("elastic.port")
                    .getString()
                    .toInt(),
            user = environment.config.property("elastic.username").getString(),
            password = environment.config.property("elastic.password").getString(),
        ),
    )

fun Application.searchService() = SearchService(client = searchClient())

fun Application.configureSearchRouting(service: SearchService) {
    routing {
        get("/api/search/videos") {
            either {
                service.allVideos()
            }.respond()
        }

        get("/api/search/state") {
            call.respondText { service.state().name }
        }
    }
}
