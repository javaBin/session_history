package no.java.conf.plugins

import arrow.core.raise.either
import com.jillesvangurp.ktsearch.KtorRestClient
import com.jillesvangurp.ktsearch.SearchClient
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.java.conf.model.search.TextSearchRequest
import no.java.conf.service.SearchService

fun searchClient(host: String, port: Int, username: String, password: String) = SearchClient(
    KtorRestClient(
        host = host,
        port = port,
        user = username,
        password = password,
    ),
)

fun Application.searchClient() = searchClient(
    host = environment.config.property("elastic.host").getString(),
    port = environment.config
        .property("elastic.port")
        .getString()
        .toInt(),
    username = environment.config.property("elastic.username").getString(),
    password = environment.config.property("elastic.password").getString(),
)

fun searchService(searchClient: SearchClient, skipIndex: Boolean) =
    SearchService(
        client = searchClient,
        skipIndex = skipIndex,
    )


fun Application.searchService() = searchService(
    searchClient = searchClient(),
    skipIndex = environment.config
        .property("elastic.skipindex")
        .getString()
        .toBoolean()
)

fun Application.configureSearchRouting(service: SearchService) {
    routing {
        route("/api/search") {
            get("/state") {
                call.respondText { service.state().name }
            }

            get("/videos") {
                either {
                    service.allVideos()
                }.respond()
            }

            get("/aggregate") {
                either {
                    service.totalAggregates()
                }.respond()
            }

            post {
                either {
                    service.textSearch(
                        runCatching<TextSearchRequest?> { call.receiveNullable<TextSearchRequest>() }.getOrNull()
                    )
                }.respond()
            }
        }
    }
}
