package no.java.conf.plugins

import arrow.core.Either
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import no.java.conf.model.ApiError

suspend inline fun <reified A : Any> Either<ApiError, A>.respond(
    context: RoutingContext,
    status: HttpStatusCode = HttpStatusCode.OK
) = when (this) {
    is Either.Left -> context.respond(value)
    is Either.Right -> context.call.respond(status, value)
}

suspend fun RoutingContext.respond(error: ApiError) = call.respond(error.statusCode, error.messageMap())
