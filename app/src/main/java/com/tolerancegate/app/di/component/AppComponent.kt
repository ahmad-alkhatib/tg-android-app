package com.tolerancegate.app.di.component

import app.core.CoreModule
import app.di.scope.ApplicationScoped
import com.tolerancegate.app.di.module.ApplicationModule
import com.tolerancegate.app.di.module.ContextModule
import app.preferences.PreferencesModule
import com.tolerancegate.app.ApplicationInit
import com.tolerancegate.ui.authentication.AuthModule
import com.tolerancegate.ui.common.CommonModule
import com.tolerancegate.ui.home.HomeModule
import com.tolerancegate.ui.splash.SplashModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@ApplicationScoped
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        ContextModule::class,
        PreferencesModule::class,
        CoreModule::class,
        SplashModule::class,
        AuthModule::class,
        HomeModule::class,
        CommonModule::class
    ]
)

interface AppComponent : AndroidInjector<ApplicationInit> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: ApplicationInit): Builder

        fun build(): AppComponent
    }
}