package com.movify.app.ui.common

import com.movify.app.models.Result
import java.util.Collections

@ConsistentCopyVisibility
data class MovieListUiState private constructor(
    val status: Status,
    val movies: List<Result> = emptyList(),
    val error: Throwable? = null,
) {
    enum class Status { IDLE, LOADING, SUCCESS, ERROR }

    companion object {
        fun idle() = MovieListUiState(Status.IDLE)

        fun loading() = MovieListUiState(Status.LOADING)

        fun success(movies: List<Result>) = MovieListUiState(
            Status.SUCCESS,
            Collections.unmodifiableList(ArrayList(movies)),
        )

        fun error(error: Throwable) = MovieListUiState(Status.ERROR, error = error)
    }
}
