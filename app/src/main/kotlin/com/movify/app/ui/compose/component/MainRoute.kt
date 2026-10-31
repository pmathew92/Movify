package com.movify.app.ui.compose.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movify.app.ui.main.MainViewModel

@Composable
fun MainRoute(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainScreen(
        uiState = uiState,
        onRetry = viewModel::loadMovies,
        modifier = modifier,
    )
}
