package com.movify.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movify.app.data.repository.MovieRepository
import com.movify.app.ui.common.MovieListUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val repository: MovieRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(MovieListUiState.idle())
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
            mutableUiState.value = MovieListUiState.idle()
            return
        }
        val requestedQuery = query
        mutableUiState.value = MovieListUiState.loading()
        request = viewModelScope.launch {
            try {
                delay(DEBOUNCE_MILLIS)
                val movies = withContext(ioDispatcher) {
                    repository.searchMovies(requestedQuery)
                }
                mutableUiState.value = MovieListUiState.success(movies)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableUiState.value = MovieListUiState.error(error)
            }
        }
    }

    private companion object {
        const val DEBOUNCE_MILLIS = 300L
    }
}
