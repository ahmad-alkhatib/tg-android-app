package app.core.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageButton
import app.apirequest.ApiResponse
import app.core.GlobalViewModel
import app.core.R
import app.core.UserViewModel
import app.core.navigation.NavigationViewModel
import app.uicomponents.dialogs.DialogProvider
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

open class BaseDialogFragment : DaggerAppCompatDialogFragment() {

    private var baseContext: Context? = null
    lateinit var userViewModel: UserViewModel
    lateinit var globalViewModel: GlobalViewModel
    lateinit var dialogProvider: DialogProvider
    lateinit var navigationViewModel: NavigationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseContext = this.context

        userViewModel = (activity as BaseActivity).userViewModel
        globalViewModel = (activity as BaseActivity).globalViewModel
        dialogProvider = (activity as BaseActivity).dialogProvider
        navigationViewModel = (activity as BaseActivity).navigationViewModel
    }


    fun getTextFromString(stringKey: Int): String {
        return (activity as BaseActivity).getTextFromString(stringKey)
    }

    fun setBackClickListner() {
        (context as BaseActivity).findViewById<AppCompatImageButton>(R.id.leftIcon)
            .setOnClickListener {
                (context as BaseActivity).onBackPressed()
            }
    }

    fun showProgress() {
        (activity as BaseActivity).showProgress()
    }

    fun showProgress(message: String) {
        (activity as BaseActivity).showProgress()
    }

    fun hideProgress() {
        (activity as BaseActivity).hideProgress()
    }

    fun showApiError(screen: Int, error: ApiResponse.ErrorResponse?) {
        (activity as BaseActivity).showApiError(screen, error)
    }

    fun showMessageDialog(screen: Int, message: Int, onDismiss: () -> Unit) {
        (activity as BaseActivity).showMessageDialog(screen, message, onDismiss)
    }

    fun showMessageDialog(screen: Int, message: Int) {
        (activity as BaseActivity).showMessageDialog(screen, message)
    }
}
