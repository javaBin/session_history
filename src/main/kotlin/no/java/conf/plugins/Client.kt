package no.java.conf.plugins

import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.server.application.Application

fun Application.httpClient() =
    HttpClient(CIO) {
        install(Logging)

        install(ContentNegotiation) {
            register(
                ContentType.Application.Json, JacksonConverter(
                    jacksonMapperBuilder()
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                        .build()
                )
            )
        }
    }