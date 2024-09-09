package no.java.conf.model.sessions

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable

private val logger = KotlinLogging.logger {}

@Serializable
enum class Format(
    private val code: String,
) {
    PRESENTATION("presentation"),
    LIGHTNING_TALK("lightning-talk"),
    WORKSHOP("workshop"),
    PANEL("panel"),
    BOF("bof"),
    UNKNOWN("unknown"),
    ;

    companion object {
        fun fromCode(code: String?) =
            entries.find { it.code == code } ?: UNKNOWN.also { logger.warn { "Unknown format $code" } }
    }
}
