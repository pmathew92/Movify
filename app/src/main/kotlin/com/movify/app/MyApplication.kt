package com.movify.app

import android.app.Application
import com.movify.app.di.AppContainer

class MyApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()
    }
}
