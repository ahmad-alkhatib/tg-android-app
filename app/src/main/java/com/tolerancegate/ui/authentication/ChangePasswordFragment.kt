package com.tolerancegate.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import app.core.custom.CustomEditText
import com.tolerancegate.R
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_forgot_password.headerView

class ChangePasswordFragment : BaseAuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenTitle(R.string.change_password)
        showBackIcon()
        setListeners()
    }

    private fun setListeners() {
        changePasswordBtn.setOnClickListener {
            if (isValid(editNewPassword) && isValid(editNewPassword, true)
                && isValid(editConfirmNewPassword, matchStr = editNewPassword.getText())
            ) {
                callChangePasswordAPI()
            }
        }
    }

    private fun isValid(customEditText: CustomEditText, password: Boolean = false, matchStr: String = ""): Boolean {
        return if (password) {
            isValidPassword(R.string.forgot_password_title, customEditText)
        } else if (matchStr.isNotEmpty()) {
            isValidPassword(R.string.forgot_password_title, customEditText, matchStr)
        } else {
            isFieldValid(R.string.forgot_password_title, customEditText)
        }
    }


    private fun callChangePasswordAPI() {
        showProgress()
        var user = userViewModel.getCurrentUser()
        user.setPassword(editNewPassword.getText())
        authViewModel.updateUser(user).observe(activity!!, Observer {
            hideProgress()
            if (it.isSuccessful) {
                popFragment()
            } else {
                showApiError(R.string.forgot_password_title, it.error)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChangePasswordFragment()
    }
}