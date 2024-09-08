package no.java.conf.plugins

import arrow.core.Either
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.util.pipeline.PipelineContext
import no.java.conf.model.ApiError

context(PipelineContext<Unit, ApplicationCall>)
suspend inline fun <reified A : Any> Either<ApiError, A>.respond(status: HttpStatusCode = HttpStatusCode.OK) =
    when (this) {
        is Either.Left -> respond(value)
        is Either.Right -> call.respond(status, value)
    }

suspend fun PipelineContext<Unit, ApplicationCall>.respond(error: ApiError) =
    call.respond(error.statusCode, error.messageMap())

context(PipelineContext<Unit, ApplicationCall>)
suspend inline fun <reified A : Any> Either<ApiError, A>.respondRedirect(url: String) =
    when (this) {
        is Either.Left -> respond(value)
        is Either.Right -> call.respondRedirect(url)
    }
