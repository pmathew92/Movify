package com.movify.app.data.repository

import com.movify.app.models.MovieResponse
import com.movify.app.models.Result
import com.movify.app.network.NetworkInterface
import java.util.Collections

class MovieRepositoryImpl(
    private val service: NetworkInterface,
    private val apiKey: String,
) : MovieRepository {
    override suspend fun getMovies(): List<Result> = service.getMovies(apiKey).movieList()

    override suspend fun searchMovies(query: String): List<Result> =
        service.getMoviesBasedOnQuery(apiKey, query).movieList()

    private fun MovieResponse.movieList(): List<Result> =
        Collections.unmodifiableList(ArrayList(results.orEmpty()))
}
