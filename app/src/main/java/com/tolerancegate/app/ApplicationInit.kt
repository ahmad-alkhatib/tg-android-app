package com.tolerancegate.app

import androidx.annotation.NonNull
import app.apirequest.parse.ParseApiManager
import app.core.base.BaseApp
import com.parse.facebook.ParseFacebookUtils
import com.tolerancegate.app.di.component.AppComponent
import com.tolerancegate.app.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class ApplicationInit : BaseApp(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var parseApiManager: ParseApiManager

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate() {
        super.onCreate()
        createComponent().inject(this)
        parseApiManager.initialize()
        ParseFacebookUtils.initialize(this)
    }

    @NonNull
    fun createComponent(): AppComponent {
        return DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}