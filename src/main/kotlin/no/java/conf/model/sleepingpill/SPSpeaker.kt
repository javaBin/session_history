package no.java.conf.model.sleepingpill

import kotlinx.serialization.Serializable
import no.java.conf.model.sessions.Speaker

@Serializable
data class SPSpeaker(
    val name: String,
    val twitter: String?,
    val bio: String?,
)

fun SPSpeaker.toSpeaker() = Speaker(
    name = this.name,
    twitter = this.twitter,
    bio = this.bio
)
