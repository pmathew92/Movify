package com.movify.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movify.app.data.repository.MovieRepository
import com.movify.app.ui.common.MovieListUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: MovieRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Idle)
    val uiState: StateFlow<MovieListUiState> = mutableUiState.asStateFlow()
    private var request: Job? = null
    private var currentQuery: String = ""
    val query: String get() = currentQuery

    fun setQuery(query: String) {
        val normalized = query.trim()
        if (currentQuery != normalized) {
            currentQuery = normalized
            search()
        }
    }

    fun retry() {
        if (query.isNotEmpty()) {
            search()
        }
    }

    private fun search() {
        request?.cancel()
        if (query.isEmpty()) {
            mutableUiState.value = MovieListUiState.Idle
            return
        }
        val requestedQuery = query
        mutableUiState.value = MovieListUiState.Loading
        request = viewModelScope.launch {
            try {
                delay(DEBOUNCE_MILLIS)
                val movies = repository.searchMovies(requestedQuery)
                mutableUiState.value = MovieListUiState.Success(movies)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableUiState.value = MovieListUiState.Error(error)
            }
        }
    }

    private companion object {
        const val DEBOUNCE_MILLIS = 300L
    }
}
