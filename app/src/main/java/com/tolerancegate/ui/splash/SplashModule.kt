package com.tolerancegate.ui.splash

import app.core.navigation.NavigationActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SplashModule {
    @ContributesAndroidInjector
    fun contributeSplashActivityInjector(): SplashActivity
}