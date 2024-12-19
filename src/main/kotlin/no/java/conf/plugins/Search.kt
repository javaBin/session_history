package no.java.conf.plugins

import arrow.core.raise.either
import com.jillesvangurp.ktsearch.KtorRestClient
import com.jillesvangurp.ktsearch.SearchClient
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.java.conf.config.SearchClientConfig
import no.java.conf.config.SearchServiceConfig
import no.java.conf.model.search.TextSearchRequest
import no.java.conf.service.SearchService

private val logger = KotlinLogging.logger {}

fun searchClient(config: SearchClientConfig): SearchClient {
    logger.info { "Connection Info: $config" }

    return SearchClient(
        KtorRestClient(
            host = config.host,
            port = config.port,
            user = config.username,
            password = config.password,
        ),
    )
}

fun searchService(config: SearchServiceConfig) =
    SearchService(
        indexer = config.indexer,
        ingester = config.ingester,
        searcher = config.searcher,
        skipIndex = config.skipIndex,
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
                }.respond(this)
            }

            post {
                either {
                    service.textSearch(
                        runCatching<TextSearchRequest?> { call.receiveNullable<TextSearchRequest>() }.getOrNull()
                    )
                }.respond(this)
            }
        }
    }
}
