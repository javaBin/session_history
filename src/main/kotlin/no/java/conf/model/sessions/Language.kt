package no.java.conf.model.sessions

import kotlinx.serialization.Serializable

@Serializable
enum class Language(private val code: String) {
    EN("en"),
    NO("no"),
    UNKNOWN("unknown");

    companion object {
        fun fromCode(code: String?) = entries.find { it.code == code } ?: UNKNOWN
    }
}