package com.tolerancegate.app.di.module

import android.app.Application
import android.content.Context
import app.di.qualifiers.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ApplicationModule {

    @Provides
    @ApplicationContext
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application
    }
}