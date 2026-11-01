package com.movify.app.ui.compose.model

import com.movify.app.models.Result

data class MovieUiModel(
    val id: Int,
    val title: String?,
    val releaseDate: String?,
    val overview: String?,
    val posterUrl: String?,
)

// Records without identity cannot safely participate in a keyed movie list.
internal fun List<Result>.toMovieUiModels(): List<MovieUiModel> =
    mapNotNull { movie ->
        val id = movie.id?.takeIf { it > 0 } ?: return@mapNotNull null
        MovieUiModel(
            id = id,
            title = movie.title.normalized(),
            releaseDate = movie.releaseDate.normalized(),
            overview = movie.overview.normalized(),
            posterUrl = movie.posterPath?.trim()?.trimStart('/')
                ?.takeIf { it.isNotBlank() }
                ?.let { "https://image.tmdb.org/t/p/w500/$it" },
        )
    }.distinctBy { it.id }

private fun String?.normalized(): String? = this?.trim()?.takeIf { it.isNotEmpty() }
