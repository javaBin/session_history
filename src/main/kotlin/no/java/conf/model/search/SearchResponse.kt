package no.java.conf.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val sessionsResponse: List<SessionResponse>,
    val aggregateResponse: AggregateResponse
)
