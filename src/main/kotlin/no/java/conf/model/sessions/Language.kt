package no.java.conf.model.sessions

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable

private val logger = KotlinLogging.logger {}

@Serializable
enum class Language(
    private val code: String,
) {
    EN("en"),
    NO("no"),
    UNKNOWN("unknown"),
    ;

    companion object {
        fun fromCode(code: String?) =
            entries.find { it.code == code } ?: UNKNOWN.also { logger.warn { "Unknown language $code" } }
    }
}
