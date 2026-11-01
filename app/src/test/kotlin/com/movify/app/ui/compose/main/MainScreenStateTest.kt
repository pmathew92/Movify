package com.movify.app.ui.compose.main

import com.movify.app.models.Result
import com.movify.app.ui.common.MovieListUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MainScreenStateTest {
    @Test
    fun `mapping normalizes display fields and builds poster URL`() {
        val state = MovieListUiState.Success(listOf(Result(
            id = 7, title = "  Movie  ", releaseDate = "  ", overview = " Description ",
            posterPath = " //poster.jpg ",
        ))).toMainScreenState() as MainScreenState.Success
        val movie = state.movies.single()
        assertEquals("Movie", movie.title)
        assertNull(movie.releaseDate)
        assertEquals("Description", movie.overview)
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", movie.posterUrl)
    }

    @Test
    fun `mapping excludes invalid identities and keeps first duplicate`() {
        val movies = listOf(
            Result(id = null), Result(id = 0), Result(id = -1),
            Result(id = 3, title = "First"), Result(id = 3, title = "Duplicate"), Result(id = 4),
        )
        val state = MovieListUiState.Success(movies).toMainScreenState() as MainScreenState.Success
        assertEquals(listOf(3, 4), state.movies.map { it.id })
        assertEquals("First", state.movies.first().title)
        val reversed = MovieListUiState.Success(movies.reversed()).toMainScreenState() as MainScreenState.Success
        assertEquals(setOf(3, 4), reversed.movies.map { it.id }.toSet())
    }

    @Test
    fun `blank content becomes absent and invalid list becomes empty success`() {
        val state = MovieListUiState.Success(listOf(Result(
            id = 1, title = " ", overview = "\n", posterPath = " / ",
        ))).toMainScreenState() as MainScreenState.Success
        assertNull(state.movies.single().title)
        assertNull(state.movies.single().overview)
        assertNull(state.movies.single().posterUrl)
        assertEquals(MainScreenState.Success(emptyList()),
            MovieListUiState.Success(listOf(Result())).toMainScreenState())
    }

    @Test
    fun `non success states map without exposing throwable to screen`() {
        assertEquals(MainScreenState.Idle, MovieListUiState.Idle.toMainScreenState())
        assertEquals(MainScreenState.Loading, MovieListUiState.Loading.toMainScreenState())
        assertEquals(MainScreenState.Error,
            MovieListUiState.Error(IllegalStateException()).toMainScreenState())
    }
}
