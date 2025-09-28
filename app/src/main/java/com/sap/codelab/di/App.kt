package com.sap.codelab.di

import android.app.Application

/**
 * Application class with dependency injection setup.
 * Initializes the DI container and sets up the app-wide dependencies.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize the simple DI container
        SimpleDIContainer.initialize(this)
    }
}
