package no.java.conf.plugins

import io.ktor.server.application.Application
import no.java.conf.service.LocalFileConfig
import no.java.conf.service.LocalFileService

fun localFileService(config: List<LocalFileConfig>) =
    LocalFileService(
        filenames = config,
    )

fun Application.localFileService() =
    localFileService(
        config =
            environment.config.configList("javazone.localfile").map {
                LocalFileConfig(it.property("year").getString().toInt(), it.property("filename").getString())
            },
    )
