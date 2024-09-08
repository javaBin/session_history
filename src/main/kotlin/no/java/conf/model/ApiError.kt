package no.java.conf.model

import io.ktor.http.HttpStatusCode

sealed class ApiError(
    val statusCode: HttpStatusCode,
    private val message: String,
) {
    open fun messageMap(): Map<String, String> = mapOf("message" to message)
}

data object IndexNotReady : ApiError(HttpStatusCode.ServiceUnavailable, "Index is not yet available")

data object SearchMissing : ApiError(HttpStatusCode.BadRequest, "Search request missing")
