package com.movify.app.ui.compose.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movify.app.ui.main.MainViewModel

@Composable
fun MainRoute(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val screenState = remember(uiState) { uiState.toMainScreenState() }

    MainScreen(
        uiState = screenState,
        onRetry = viewModel::loadMovies,
        modifier = modifier,
    )
}
