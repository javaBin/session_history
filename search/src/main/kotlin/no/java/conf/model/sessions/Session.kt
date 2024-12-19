package no.java.conf.model.sessions

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val intendedAudience: String?,
    val length: Int?,
    val format: Format,
    val language: Language,
    val abstract: String?,
    val title: String,
    val video: String?,
    val id: String,
    val sessionId: String,
    val conferenceId: String,
    val speakers: List<Speaker>,
    val year: Int,
) {
    fun hasVideo() = this.video != null
}
