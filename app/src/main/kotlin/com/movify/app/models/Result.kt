package com.movify.app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    @SerialName("vote_count") val voteCount: Int? = null,
    @SerialName("id") val id: Int? = null,
    @SerialName("video") val video: Boolean? = null,
    @SerialName("vote_average") val voteAverage: Float? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("popularity") val popularity: Float? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("genre_ids") val genreIds: List<Int>? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("adult") val adult: Boolean? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
)
