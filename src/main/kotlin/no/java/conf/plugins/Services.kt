package no.java.conf.plugins

import io.ktor.server.application.Application
import no.java.conf.service.SleepingPillService

fun Application.configureServices() {
    val sleepingPillService = SleepingPillService(httpClient(), environment.config.property("javazone.sleepingpill").getList())
}