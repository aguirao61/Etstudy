package com.example.studyapp.core

import android.app.Application
import com.example.studyapp.core.util.DatabaseInitializer
import com.example.studyapp.di.AppModule
import com.example.studyapp.di.AppModuleImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App: Application() {
    lateinit var appModule: AppModule

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)

        // Initialize data in a background thread at start of app
        CoroutineScope(Dispatchers.IO).launch {
            val initializer = DatabaseInitializer(this@App, appModule.database)
            initializer.initializeData()
        }
    }
}