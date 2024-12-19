package no.java.conf.model.search

import kotlinx.serialization.Serializable

@Serializable
data class TextSearchRequest(
    val query: String = "*",
    val years: List<Int> = emptyList(),
    val languages: List<String> = emptyList(),
    val formats: List<String> = emptyList(),
)

fun TextSearchRequest.hasFilter() = this.years.isNotEmpty() || this.formats.isNotEmpty() || this.languages.isNotEmpty()

fun TextSearchRequest.hasQuery() = this.query.isNotBlank() || this.query == "*"
