package no.java.conf.model.sleepingpill

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.java.conf.model.sessions.Speaker

@JsonIgnoreProperties(ignoreUnknown = true)
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
