package com.movify.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movify.app.data.repository.MovieRepository
import com.movify.app.ui.common.MovieListUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val repository: MovieRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Idle)
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    private var request: Job? = null

    fun loadMovies() {
        request?.cancel()
        _uiState.value = MovieListUiState.Loading
        request = viewModelScope.launch {
            try {
                val movies = withContext(ioDispatcher) { repository.getMovies() }
                _uiState.value = MovieListUiState.Success(movies)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.value = MovieListUiState.Error(error)
            }
        }
    }
}
