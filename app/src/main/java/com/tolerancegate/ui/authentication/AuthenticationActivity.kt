package com.tolerancegate.ui.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import app.apirequest.parse.PropertyValue.STUDENT
import app.core.base.BaseFragment
import app.core.navigation.AuthScreen
import app.core.navigation.AuthScreen.LOGIN
import app.core.navigation.NavigationActivity
import javax.inject.Inject


class AuthenticationActivity : NavigationActivity() {

    private lateinit var authViewModel: AuthViewModel

    @Inject
    lateinit var factory: AuthViewModel.AuthenticationViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusbarTransparent()
        authViewModel = ViewModelProviders.of(this, factory)[AuthViewModel::class.java]
        authViewModel.setRegistrationForPT(intent.getBooleanExtra(PT_STATUS, false))
        authViewModel.getRegistrationType().value = intent.getStringExtra(USER_TYPE)
    }


    override val numberOfRootFragments: Int
        get() = 1

    override fun getRootFragment(index: Int): BaseFragment {
        return when (rootScreen) {
            LOGIN -> LoginFragment.newInstance()
            else -> LoginFragment.newInstance()
        }
    }

    companion object {
        @JvmStatic
        fun start(
            activity: Activity,
            rootScreen: AuthScreen = LOGIN,
            defaultPTStatus: Boolean = false,
            userType: String = STUDENT
        ) {
            this.rootScreen = rootScreen
            var intent = Intent(activity, AuthenticationActivity::class.java)
            intent.putExtra(PT_STATUS, defaultPTStatus)
            intent.putExtra(USER_TYPE, userType)
            activity.startActivity(intent)
        }

        var rootScreen = LOGIN
        var PT_STATUS = "PT_STATUS"
        var USER_TYPE = "USER_TYPE"
    }

    override fun onBackPressed() {
        if (fragNavController.isRootFragment)
            super.onBackPressed()
    }
}