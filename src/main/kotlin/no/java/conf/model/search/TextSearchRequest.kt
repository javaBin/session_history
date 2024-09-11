package no.java.conf.model.search

import kotlinx.serialization.Serializable
import no.java.conf.model.sessions.Format

@Serializable
data class TextSearchRequest(
    val query: String = "*",
    val years: List<Int> = emptyList(),
    val languages: List<String> = emptyList(),
    val formats: List<String> = emptyList(),
)

fun TextSearchRequest.filterOnly() = this.queryString() == "*"

fun TextSearchRequest.queryString() = when (this.query.isEmpty()) {
    true -> "*"
    else -> this.query
}