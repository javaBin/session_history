package no.java.conf.model.sessions

enum class Format(private val code: String) {
    PRESENTATION("presentation"),
    LIGHTNING_TALK("lightning-talk"),
    WORKSHOP("workshop"),
    UNKNOWN("unknown");

    companion object {
        fun fromCode(code: String?) = entries.find { it.code == code } ?: UNKNOWN
    }
}