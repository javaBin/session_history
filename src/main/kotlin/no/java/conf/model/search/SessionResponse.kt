package no.java.conf.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    val title: String,
    val video: String?,
    val year: Int,
    val abstract: String?,
    val format: String?,
    val language: String?,
    val speakers: List<SpeakerResponse>
)
