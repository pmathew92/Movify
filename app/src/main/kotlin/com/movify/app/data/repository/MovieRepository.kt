package com.movify.app.data.repository

import com.movify.app.models.Result

interface MovieRepository {
    suspend fun getMovies(): List<Result>

    suspend fun searchMovies(query: String): List<Result>
}
