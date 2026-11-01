package com.movify.app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movify.app.data.repository.MovieRepository
import com.movify.app.ui.main.MainViewModel
import com.movify.app.ui.search.SearchViewModel

class MovieViewModelFactory(
    private val repository: MovieRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            MainViewModel::class.java -> MainViewModel(repository)
            SearchViewModel::class.java -> SearchViewModel(repository)
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
        return checkNotNull(modelClass.cast(viewModel))
    }
}
