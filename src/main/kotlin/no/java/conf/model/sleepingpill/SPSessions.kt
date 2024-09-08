package no.java.conf.model.sleepingpill

import kotlinx.serialization.Serializable

@Serializable
data class SPSessions(
    val sessions: List<SPSession>,
)
