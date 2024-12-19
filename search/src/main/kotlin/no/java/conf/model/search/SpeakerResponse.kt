package no.java.conf.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SpeakerResponse(
    val name: String,
    val bio: String?,
    val twitter: String?
)
