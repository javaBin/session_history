package no.java.conf.model.search

import kotlinx.serialization.Serializable

@Serializable
data class LanguageAggregate(
    val language: String,
    val count: Long
)

@Serializable
data class FormatAggregate(
    val format: String,
    val count: Long
)

@Serializable
data class YearAggregate(
    val year: Int,
    val count: Long
)

@Serializable
data class AggregateResponse(
    val languages: List<LanguageAggregate>,
    val formats: List<FormatAggregate>,
    val years: List<YearAggregate>
)
