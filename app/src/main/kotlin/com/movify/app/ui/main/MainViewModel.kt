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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val repository: MovieRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(MovieListUiState.idle())
    val uiState: StateFlow<MovieListUiState> = mutableUiState.asStateFlow()
    private var request: Job? = null

    init {
        loadMovies()
    }

    fun loadMovies() {
        request?.cancel()
        mutableUiState.value = MovieListUiState.loading()
        request = viewModelScope.launch {
            try {
                val movies = withContext(ioDispatcher) { repository.getMovies() }
                mutableUiState.value = MovieListUiState.success(movies)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableUiState.value = MovieListUiState.error(error)
            }
        }
    }
}
