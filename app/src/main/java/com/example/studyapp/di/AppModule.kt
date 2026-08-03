package com.example.studyapp.di

import android.content.Context

interface AppModule {

}

class AppModuleImpl (
    private val context: Context
) : AppModule {

}