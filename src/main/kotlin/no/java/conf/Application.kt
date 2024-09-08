package no.java.conf

import io.ktor.server.application.Application
import no.java.conf.plugins.configureMonitoring
import no.java.conf.plugins.configureRouting
import no.java.conf.plugins.configureSecurity
import no.java.conf.plugins.configureSerialization
import no.java.conf.plugins.configureSessionRouting

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain
        .main(args)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting()
    configureSessionRouting()
}
