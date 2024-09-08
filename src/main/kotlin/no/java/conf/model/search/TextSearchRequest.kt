package no.java.conf.model.search

import kotlinx.serialization.Serializable

@Serializable
data class TextSearchRequest(
    val query: String
)
