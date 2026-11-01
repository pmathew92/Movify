package com.movify.app.ui.compose.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.movify.app.R
import com.movify.app.ui.compose.model.MovieUiModel

@Composable
fun MovieCard(
    movie: MovieUiModel,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PosterImage(
                imageUrl = movie.posterUrl,
            )
            MovieContent(movie = movie, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PosterImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    val placeholder = painterResource(R.drawable.movie_poster_placeholder)

    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        placeholder = placeholder,
        error = placeholder,
        fallback = placeholder,
        modifier = modifier
            .width(100.dp)
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun MovieContent(
    movie: MovieUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = movie.title ?: stringResource(R.string.untitled_movie),
            style = MaterialTheme.typography.titleLarge,
        )
        movie.releaseDate?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        movie.overview?.let {
            Text(text = it, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
