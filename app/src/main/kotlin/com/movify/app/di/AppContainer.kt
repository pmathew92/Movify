package com.movify.app.di

import android.content.Context
import com.movify.app.R
import com.movify.app.data.repository.MovieRepository
import com.movify.app.data.repository.MovieRepositoryImpl
import com.movify.app.network.NetworkClient
import com.movify.app.network.NetworkInterface
import com.movify.app.ui.common.MovieViewModelFactory

class AppContainer(context: Context) {
    private val service = NetworkClient.retrofit.create(NetworkInterface::class.java)
    val movieRepository: MovieRepository = MovieRepositoryImpl(
        service,
        context.applicationContext.getString(R.string.api_key),
    )
    val viewModelFactory = MovieViewModelFactory(movieRepository)
}
