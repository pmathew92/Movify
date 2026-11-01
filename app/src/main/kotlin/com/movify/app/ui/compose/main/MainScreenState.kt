package com.movify.app.ui.compose.main

import com.movify.app.ui.common.MovieListUiState
import com.movify.app.ui.compose.model.MovieUiModel
import com.movify.app.ui.compose.model.toMovieUiModels

sealed interface MainScreenState {
    data object Idle : MainScreenState
    data object Loading : MainScreenState
    data object Error : MainScreenState
    data class Success(val movies: List<MovieUiModel>) : MainScreenState
}

internal fun MovieListUiState.toMainScreenState(): MainScreenState = when (this) {
    MovieListUiState.Idle -> MainScreenState.Idle
    MovieListUiState.Loading -> MainScreenState.Loading
    is MovieListUiState.Error -> MainScreenState.Error
    is MovieListUiState.Success -> MainScreenState.Success(movies.toMovieUiModels())
}
