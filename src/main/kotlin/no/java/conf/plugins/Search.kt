package no.java.conf.plugins

import com.jillesvangurp.ktsearch.KtorRestClient
import com.jillesvangurp.ktsearch.SearchClient
import io.ktor.server.application.Application

fun Application.searchClient() = SearchClient(
    KtorRestClient(
        host = environment.config.property("elastic.host").getString(),
        port = environment.config.property("elastic.port").getString().toInt(),
        user = environment.config.property("elastic.username").getString(),
        password = environment.config.property("elastic.password").getString()
    )
)
