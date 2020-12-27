package com.tolerancegate.app.di.module

import android.content.Context
import com.tolerancegate.app.ApplicationInit
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class ContextModule {

    @Binds
    internal abstract fun bindContext(application: ApplicationInit): Context
}