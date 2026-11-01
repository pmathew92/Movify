package com.movify.app.ui.compose.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.movify.app.ui.compose.model.MovieUiModel

@Composable
fun MovieList(
    movieList: List<MovieUiModel>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = movieList,
            key = { movie -> movie.id },
        ) { movie ->
            MovieCard(movie = movie)
        }
    }
}
