package app.core.base

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import app.apirequest.ApiResponse
import app.core.*
import app.uicomponents.dialogs.DialogProvider
import app.core.navigation.NavigationViewModel
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var navigationViewModelFactory: NavigationViewModel.NavigationViewModelFactory

    @Inject
    lateinit var globalViewModelFactory: GlobalViewModel.GlobalViewModelFactory

    @Inject
    lateinit var userViewModelFactory: UserViewModel.UserViewModelFactory

    @Inject
    lateinit var dialogProvider: DialogProvider

    lateinit var navigationViewModel: NavigationViewModel
    lateinit var globalViewModel: GlobalViewModel
    lateinit var userViewModel: UserViewModel

    lateinit var progressDialogFragment: ProgressDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressDialogFragment = ProgressDialogFragment.Builder().build()!!

        navigationViewModel =
            ViewModelProviders.of(this, navigationViewModelFactory).get(NavigationViewModel::class.java)

        globalViewModel = ViewModelProviders.of(this, globalViewModelFactory).get(GlobalViewModel::class.java)

        userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)
    }

    fun setStatusBarWhite() {
        window.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                statusBarColor = Color.WHITE
            } else {
                statusBarColor = Color.BLACK
            }
        }
    }

    fun setStatusbarTransparent() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    fun setStatusbarTransparentWithBlackText() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    fun showProgress() {
        try {
            if (progressDialogFragment.isAdded)
                return

            progressDialogFragment.setMessage(getString(R.string.please_wait))
            progressDialogFragment.show(supportFragmentManager, "")
            supportFragmentManager.executePendingTransactions()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showProgress(message: String) {
        if (progressDialogFragment.isAdded)
            return
        progressDialogFragment.setMessage(message)
        progressDialogFragment.show(supportFragmentManager, "")
        supportFragmentManager.executePendingTransactions()
    }

    fun hideProgress() {
        progressDialogFragment.dismiss()
    }

    fun getTextFromString(key: Int) = applicationContext.resources.getString(key)

    fun showApiError(screen: Int, error: ApiResponse.ErrorResponse?) {
        dialogProvider.showNativeErrorDialog(
            this,
            applicationContext.resources.getString(screen),
            error?.message ?: ""
        )
    }

    fun showMessageDialog(screen: Int, message: Int, onDismiss: () -> Unit) {
        showMessageDialog(screen, applicationContext.resources.getString(message), onDismiss)
    }

    fun showMessageDialog(screen: Int, message: Int) {
        showMessageDialog(screen, applicationContext.resources.getString(message)) {}
    }

    fun showMessageDialog(screen: Int, message: String, onDismiss: () -> Unit) {
        dialogProvider.showNativeMessageDialog(
            this,
            applicationContext.resources.getString(screen),
            message,
            onDismiss
        )
    }
}