package com.movify.app.data.repository

import com.movify.app.models.MovieResponse
import com.movify.app.models.Result
import com.movify.app.network.NetworkInterface
import java.util.Collections
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepositoryImpl(
    private val service: NetworkInterface,
    private val apiKey: String,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MovieRepository {
    override suspend fun getMovies(): List<Result> = withContext(ioDispatcher) {
        service.getMovies(apiKey).movieList()
    }

    override suspend fun searchMovies(query: String): List<Result> = withContext(ioDispatcher) {
        service.getMoviesBasedOnQuery(apiKey, query).movieList()
    }

    private fun MovieResponse.movieList(): List<Result> =
        Collections.unmodifiableList(ArrayList(results.orEmpty()))
}
