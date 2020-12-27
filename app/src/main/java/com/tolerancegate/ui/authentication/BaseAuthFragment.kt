package com.tolerancegate.ui.authentication

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import app.core.base.BaseActivity
import app.core.base.BaseFragment
import javax.inject.Inject

open class BaseAuthFragment : BaseFragment() {

    lateinit var authViewModel: AuthViewModel

    @Inject
    lateinit var factory: AuthViewModel.AuthenticationViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProviders.of(activity as BaseActivity, factory)[AuthViewModel::class.java]
    }

    fun getRegistrationType() = authViewModel.getRegistrationType()

    fun isPTEnabled() = authViewModel.registrationForPT()
}