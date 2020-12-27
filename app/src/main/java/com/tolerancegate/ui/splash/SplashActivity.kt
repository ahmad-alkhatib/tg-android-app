package com.tolerancegate.ui.splash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import app.core.base.BaseActivity
import app.preferences.Preferences
import app.preferences.PreferencesWrapper
import app.utilities.Constant
import com.tolerancegate.R
import com.tolerancegate.ui.home.HomeActivity
import com.tolerancegate.ui.splash.LocaleHelper.Companion.updateLocale
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    lateinit var preferences: Preferences
    private var appLanguageKey = "En"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusbarTransparent()
        setContentView(R.layout.activity_splash)
        Constant.appLanguageKey = appLanguageKey
        rootView.setOnApplyWindowInsetsListener { _, insets ->
            globalViewModel.setStatusBarHeight(insets.systemWindowInsetTop)
            globalViewModel.setBottomNotchSpace(insets.systemWindowInsetBottom)
            insets
        }
        openApp()
    }

    override fun attachBaseContext(newBase: Context?) {
        preferences = Preferences(PreferencesWrapper(newBase))
        appLanguageKey = if (preferences.language == "ar") "Ar" else "En"
        var localeUpdatedContext = updateLocale(newBase, preferences.language);
        super.attachBaseContext(localeUpdatedContext);
    }

    private fun openApp() {
        Handler().postDelayed({
            onStartApp()
        }, 1000)
    }

    private fun onStartApp() {
        HomeActivity.start(this)
    }

    companion object {
        @JvmStatic
        fun start(activity: Activity) {
            val intent = Intent(activity, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.anim_enter_slide_in, R.anim.anim_enter_slide_out)
        }
    }
}