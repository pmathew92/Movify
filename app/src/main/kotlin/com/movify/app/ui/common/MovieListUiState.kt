package com.movify.app.ui.common

import com.movify.app.models.Result

sealed interface MovieListUiState {
    data object Idle : MovieListUiState

    data object Loading : MovieListUiState

    data class Error(val error: Throwable) : MovieListUiState

    data class Success(val movies: List<Result>) : MovieListUiState
}
