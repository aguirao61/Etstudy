package com.example.studyapp.core

import android.app.Application
import com.example.studyapp.di.AppModule
import com.example.studyapp.di.AppModuleImpl

class App: Application() {
    lateinit var appModule: AppModule

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}