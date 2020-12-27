package app.core.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import app.apirequest.ApiResponse
import app.core.GlobalViewModel
import app.core.R
import app.core.UserViewModel
import app.core.custom.CustomEditText
import app.core.navigation.NavigationActivity
import app.core.navigation.NavigationViewModel
import app.uicomponents.dialogs.DialogProvider
import app.uicomponents.extensions.show
import app.utilities.Enums
import app.utilities.ImageLoaderUtils
import dagger.android.support.DaggerFragment
import javax.inject.Inject

open class BaseFragment : DaggerFragment() {

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

    fun setScreenTitle(key: Int) {
        (context as BaseActivity).findViewById<TextView>(R.id.titleTextView).text =
            getTextFromString(key)
    }

    fun setScreenTitle(title: String) {
        (context as BaseActivity).findViewById<TextView>(R.id.titleTextView).text = title
    }

    fun showBackIcon() {
        var view = (context as BaseActivity).findViewById<View>(R.id.leftIcon)
        view.visibility = VISIBLE
        view.setOnClickListener {
            onBackPressed()
        }
    }

    fun onBackPressed(){
        (activity as BaseActivity).onBackPressed()
    }

    fun getRightMenuIcon(): AppCompatImageButton {
        return (context as BaseActivity).findViewById(R.id.rightIcon)
    }

    fun getRightMenuIcon2(): AppCompatImageButton {
        return (context as BaseActivity).findViewById(R.id.rightIcon2)
    }

    fun getRightMenuIcon3(): AppCompatImageButton {
        return (context as BaseActivity).findViewById(R.id.rightIcon3)
    }

    fun hideBackIcon() {
        (context as BaseActivity).findViewById<View>(R.id.leftIcon).visibility = INVISIBLE
    }

    fun setBackClickListner() {
        (context as BaseActivity).findViewById<View>(R.id.leftIcon).setOnClickListener {
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

    fun popFragment() {
        (activity as NavigationActivity).popFragment()
    }

    fun popUntilBase() {
        (activity as NavigationActivity).popUntilBase()
    }

    fun pushFragment(fragment: Fragment) {
        (activity as NavigationActivity).pushFragment(fragment)
    }

    fun replaceFragment(fragment: Fragment) {
        (activity as NavigationActivity).replaceFragment(fragment)
    }

    fun isFieldValid(screen: Int, editText: CustomEditText, email: Boolean = false): Boolean {
        context?.let {
            if (!editText.isValid()) {
                showNativeDialog(it, it.resources.getString(screen), editText.getError())
                return false
            } else if (email && !editText.isValidEmail()) {
                showNativeDialog(it, it.resources.getString(screen), editText.getError())
                return false
            }
        }
        return true
    }

    fun isValidPassword(screen: Int, editText: CustomEditText, matchStr: String = ""): Boolean {
        context?.let {
            if (matchStr.isNotEmpty() && !editText.doesPasswordMatch(matchStr)) {
                showNativeDialog(
                    it,
                    it.resources.getString(screen),
                    it.resources.getString(R.string.your_password_and_confirmation_password_do_not_match)
                )
                return false
            } else if (!editText.isValidPassword()) {
                showNativeDialog(
                    it,
                    it.resources.getString(screen),
                    it.resources.getString(R.string.password_should_be_between_6_to_10_characters)
                )
                return false
            }
        }
        return true
    }

    fun showNativeDialog(context: Context, title: String, message: String) {
        dialogProvider.showNativeErrorDialog(context, title, message)
    }

    fun showApiError(screen: Int, error: ApiResponse.ErrorResponse?) {
        context?.let {
            showNativeDialog(it, it.resources.getString(screen), error?.message ?: "")
        }
    }

    fun showApiError(screen: Int, message: String) {
        context?.let {
            showNativeDialog(it, it.resources.getString(screen), message)
        }
    }

    fun showMessageDialog(title: Int, message: Int) {
        context?.let {
            showNativeDialog(it, it.resources.getString(title), it.resources.getString(message))
        }
    }

    fun showMessageDialog(screen: Int, message: String, onDismiss: () -> Unit) {
        context?.let {
            dialogProvider.showNativeMessageDialog(it, it.resources.getString(screen), message, onDismiss)
        }
    }

    fun showMessageDialog(screen: Int, message: String) {
        context?.let {
            dialogProvider.showNativeMessageDialog(it, it.resources.getString(screen), message) {}
        }
    }

    fun showConfirmationDialog(title: Int, message: Int, primaryBtn: Int, primaryBtnAction: () -> Unit) {
        context?.let {
            dialogProvider.showConfirmationDialog(
                it,
                it.resources.getString(title),
                it.resources.getString(message),
                it.resources.getString(primaryBtn),
                it.resources.getString(R.string.cancel),
                primaryBtnAction
            )
        }
    }

    fun showConfirmationDialog(title: Int, message: Int, primaryBtnAction: () -> Unit) {
        context?.let {
            dialogProvider.showConfirmationDialog(
                it,
                it.resources.getString(title),
                it.resources.getString(message),
                it.resources.getString(R.string.yes),
                it.resources.getString(R.string.no),
                primaryBtnAction
            )
        }
    }

    fun loadCircleImage(url: String, imgView: AppCompatImageView, placeholder:Int = R.drawable.image_placeholder_user_gray) {
        ImageLoaderUtils.loadCircleImage(url, imgView, placeholder)
    }

    fun loadImageFromServerCrop(url: String, imgView: AppCompatImageView, placeholder:Int = R.drawable.image_placeholder_gray) {
        ImageLoaderUtils.loadImageFromServerCrop(url, imgView, placeholder)
    }

    fun setMenuIcon(icon: Int, action: () -> Unit) {
        setMenu(getRightMenuIcon(), icon, action)
    }

    fun setMenuIcon2(icon: Int, action: () -> Unit) {
        setMenu(getRightMenuIcon2(), icon, action)
    }

    fun setMenuIcon3(icon: Int, action: () -> Unit) {
        setMenu(getRightMenuIcon3(), icon, action)
    }

    fun setMenu(iconView:AppCompatImageButton, icon: Int, action: () -> Unit) {
        iconView.show()
        iconView.setImageResource(icon)
        iconView.setOnClickListener { action() }
    }
}