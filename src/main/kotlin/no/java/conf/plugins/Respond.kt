package no.java.conf.plugins

import arrow.core.Either
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.RoutingContext
import no.java.conf.model.ApiError

context(RoutingContext)
suspend inline fun <reified A : Any> Either<ApiError, A>.respond(status: HttpStatusCode = HttpStatusCode.OK) =
    when (this) {
        is Either.Left -> respond(value)
        is Either.Right -> call.respond(status, value)
    }

suspend fun RoutingContext.respond(error: ApiError) =
    call.respond(error.statusCode, error.messageMap())

context(RoutingContext)
suspend inline fun <reified A : Any> Either<ApiError, A>.respondRedirect(url: String) =
    when (this) {
        is Either.Left -> respond(value)
        is Either.Right -> call.respondRedirect(url)
    }
