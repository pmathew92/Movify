package com.movify.app.di

import com.movify.app.BuildConfig
import com.movify.app.data.repository.MovieRepository
import com.movify.app.data.repository.MovieRepositoryImpl
import com.movify.app.network.NetworkClient
import com.movify.app.network.NetworkInterface
import com.movify.app.ui.common.MovieViewModelFactory

class AppContainer {
    private val service = NetworkClient.retrofit.create(NetworkInterface::class.java)
    val movieRepository: MovieRepository = MovieRepositoryImpl(
        service,
        BuildConfig.TMDB_API_KEY,
    )
    val viewModelFactory = MovieViewModelFactory(movieRepository)
}
