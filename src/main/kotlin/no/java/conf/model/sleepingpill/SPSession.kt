package no.java.conf.model.sleepingpill

import kotlinx.serialization.Serializable
import no.java.conf.model.sessions.Format
import no.java.conf.model.sessions.Language
import no.java.conf.model.sessions.Session

@Serializable
data class SPSession(
    val intendedAudience: String?,
    val length: String?,
    val format: String,
    val language: String?,
    val abstract: String?,
    val title: String,
    val room: String?,
    val startTime: String?,
    val endTime: String?,
    val video: String?,
    val startTimeZulu: String?,
    val endTimeZulu: String?,
    val id: String,
    val sessionId: String,
    val conferenceId: String,
    val startSlot: String?,
    val startSlotZulu: String?,
    val speakers: List<SPSpeaker>,
    val workshopPrerequisites: String?,
)

fun SPSession.toSession(year: Int) = Session(
    intendedAudience = this.intendedAudience,
    length = length?.toInt(),
    format = Format.fromCode(this.format),
    language = Language.fromCode(this.language),
    abstract = this.abstract,
    title = this.title,
    video = this.video?.fixVideo(),
    id = this.id,
    sessionId = this.sessionId,
    conferenceId = this.conferenceId,
    speakers = this.speakers.map(SPSpeaker::toSpeaker),
    year = year
)

fun String.fixVideo() = "https://vimeo.com/" + this.split("/").last()