package no.java.conf.model.search

import kotlinx.serialization.Serializable

@Serializable
data class VideoSearchResponse(
    val title: String,
    val video: String,
    val year: Int,
)
