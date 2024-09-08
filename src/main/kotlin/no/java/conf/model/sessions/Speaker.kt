package no.java.conf.model.sessions

import kotlinx.serialization.Serializable

@Serializable
data class Speaker(
    val name: String,
    val twitter: String?,
    val bio: String?,
)
