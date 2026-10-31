package com.movify.app.ui.compose.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.movify.app.models.Result

@Composable
fun MovieCard(
    movie: Result,
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
                imageUrl = movie.posterPath,
                contentDescription = movie.title?.takeIf { it.isNotBlank() }?.let {
                    stringResource(R.string.movie_poster_description, it)
                },
            )
            MovieContent(movie = movie, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PosterImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val poster = imageUrl
        ?.trim()
        ?.trimStart('/')
        ?.takeIf { it.isNotBlank() }
        ?.let { "https://image.tmdb.org/t/p/w500/$it" }
    val placeholder = painterResource(R.mipmap.ic_launcher)

    AsyncImage(
        model = poster,
        contentDescription = contentDescription,
        placeholder = placeholder,
        error = placeholder,
        fallback = placeholder,
        modifier = modifier.size(100.dp),
        contentScale = ContentScale.Fit,
    )
}

@Composable
fun MovieContent(
    movie: Result,
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
