package com.tolerancegate.ui.authentication

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import app.apirequest.parse.Property.FULL_NAME
import app.apirequest.parse.Property.IMAGE
import app.apirequest.parse.Property.SOCIAL_LOGIN_EMAIL
import app.apirequest.parse.Property.SOCIAL_LOGIN_ID
import app.apirequest.parse.PropertyValue.FILE_JPEG
import app.apirequest.parse.PropertyValue.STUDENT
import app.core.custom.CustomEditText
import app.uicomponents.extensions.getByte
import app.utilities.Enums
import app.utilities.Enums.CommonRootScreen.SETTING
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.parse.ParseFile
import com.parse.ParseUser
import com.parse.facebook.ParseFacebookUtils
import com.tolerancegate.R
import com.tolerancegate.ui.common.AppSettingFragment
import com.tolerancegate.ui.common.CommonActivity
import com.tolerancegate.ui.home.HomeActivity
import kotlinx.android.synthetic.main.fragment_login.*
import java.net.URL


class LoginFragment : BaseAuthFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (userViewModel.hasCurrentUser()) {
            if (userViewModel.isSocialSignUpNotCompleted()) {
                openRegistrationScreen()
            } else if (userViewModel.isSignUpCompleted()) {
                replaceFragment(RegisterFragment.newInstance())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView.updateStatusBarHeight(globalViewModel.getStatusBarHeight())
        setListeners()
    }

    private fun setListeners() {
        loginButton.setOnClickListener {
            if (isValid(editEmail, true) && isValid(editPassword)) {
                callLoginUserAPI()
            }
        }

        forgotPasswordButton.setOnClickListener {
            pushFragment(ForgotPasswordFragment.newInstance())
        }

        registerAccountBtn.setOnClickListener {
            openRegistrationScreen()
        }

        loginFacebookBtn.setOnClickListener {
            doRequestToFBLogin()
        }

        headerView.addActionForRightIcon {
            openSettingScreen()
        }
    }

    fun openSettingScreen() {
        activity?.let {
            navigationViewModel.commonRootScreen.postValue(SETTING)
            CommonActivity.start(it)
        }
    }

    private fun openRegistrationScreen() {
        if (isPTEnabled().not()) {
            getRegistrationType().postValue(STUDENT)
        }
        pushFragment(RegisterFragment.newInstance())
    }

    private fun doRequestToFBLogin() {
        showProgress()
        val permissions: Collection<String> = listOf("public_profile", "email")
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions) { user, e ->
            if (e != null) {
                ParseUser.logOut()
                showApiError(R.string.login, e.message ?: "")
                hideProgress()
            } else if (user == null) {
                ParseUser.logOut()
                hideProgress()
            } else if (user.isNew) {
                userViewModel.setSocialLogin()
                getUserDetailFromFB()
            } else {
                hideProgress()
                if (userViewModel.isSocialSignUpNotCompleted()) {
                    openRegistrationScreen()
                } else {
                    navigateToHome()
                }
            }
        }
    }

    private fun getUserDetailFromFB() {
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { userData, response ->
            if (response.error != null) {
                ParseUser.logOut()
                showMessageDialog(R.string.login, response.error.errorMessage)
                hideProgress()
            } else {
                Thread {
                    val user = ParseUser.getCurrentUser()
                    userData.getString("email")?.let {
                        user.put(SOCIAL_LOGIN_EMAIL, it)
                    }
                    userData.getString("name")?.let {
                        user.put(FULL_NAME, it)
                    }
                    userData.getString("id")?.let { fbId ->
                        user.put(SOCIAL_LOGIN_ID, fbId)
                        var userProfile = "https://graph.facebook.com/$fbId/picture?type=large"
                        val url = URL(userProfile)
                        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        bmp?.let {
                            var parseFile = ParseFile(FILE_JPEG, it.getByte());
                            parseFile.save()
                            user.put(IMAGE, parseFile)
                        }
                        user.saveInBackground {
                            hideProgress()
                            if (it == null) {
                                openRegistrationScreen()
                            } else {
                                ParseUser.logOut()
                                showApiError(R.string.login, it.localizedMessage)
                            }
                        }
                    }
                }.start()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "name,email")
        request.parameters = parameters
        request.executeAsync()
    }


    private fun callLoginUserAPI() {
        showProgress()
        authViewModel.loginUser(editEmail.getText(), editPassword.getText()).observe(activity!!, Observer {
            hideProgress()
            if (it.isSuccessful) {
                navigateToHome()
            } else {
                showApiError(R.string.login, it.error)
            }
        })
    }

    private fun navigateToHome() {
        activity?.let { activity -> HomeActivity.start(activity) }
    }

    private fun isValid(customEditText: CustomEditText, email: Boolean = false) =
        isFieldValid(R.string.login, customEditText, email)

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data)
    }
}





