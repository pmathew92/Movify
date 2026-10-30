package com.movify.app.network

import com.movify.app.models.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkInterface {
    @GET("discover/movie")
    suspend fun getMovies(@Query("api_key") apiKey: String): MovieResponse

    @GET("search/movie")
    suspend fun getMoviesBasedOnQuery(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
    ): MovieResponse
}
