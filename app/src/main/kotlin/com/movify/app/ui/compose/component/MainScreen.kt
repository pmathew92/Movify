package com.movify.app.ui.compose.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.movify.app.R
import com.movify.app.ui.common.MovieListUiState

@Composable
fun MainScreen(
    uiState: MovieListUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            MovieListUiState.Idle -> Unit
            MovieListUiState.Loading -> CircularProgressIndicator()
            is MovieListUiState.Error -> {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.error_loading_movies),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                    Button(onClick = onRetry) {
                        Text(text = stringResource(R.string.retry))
                    }
                }
            }
            is MovieListUiState.Success -> {
                if (uiState.movies.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_movies),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    MovieList(movieList = uiState.movies)
                }
            }
        }
    }
}
