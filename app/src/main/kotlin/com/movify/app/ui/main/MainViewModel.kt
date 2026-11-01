package com.movify.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movify.app.data.repository.MovieRepository
import com.movify.app.ui.common.MovieListUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MovieRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Idle)
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    private var request: Job? = null

    init {
        loadMovies()
    }

    fun loadMovies() {
        request?.cancel()
        _uiState.value = MovieListUiState.Loading
        request = viewModelScope.launch {
            try {
                val movies = repository.getMovies()
                _uiState.value = MovieListUiState.Success(movies)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.value = MovieListUiState.Error(error)
            }
        }
    }
}
