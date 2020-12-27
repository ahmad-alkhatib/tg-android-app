package com.tolerancegate.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import app.core.custom.CustomEditText
import com.tolerancegate.R
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_forgot_password.editEmail
import kotlinx.android.synthetic.main.fragment_forgot_password.headerView
import kotlinx.android.synthetic.main.fragment_login.*

class ForgotPasswordFragment : BaseAuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView.updateStatusBarHeight(globalViewModel.getStatusBarHeight())
        setListeners()
    }

    private fun setListeners() {
        headerView.addActionForLeftIcon {
            popFragment()
        }

        resetButton.setOnClickListener {
            if (isValid(editEmail)) {
                callForgotPasswordAPI()
            }
        }
    }

    private fun isValid(customEditText: CustomEditText) =
        isFieldValid(R.string.forgot_password_title, customEditText, true)

    private fun callForgotPasswordAPI() {
        showProgress()
        authViewModel.forgotPassword(editEmail.getText()).observe(activity!!, Observer {
            hideProgress()
            if (it.isSuccessful) {
                showMessageDialog(R.string.forgot_password, getString(R.string.password_reset_link_sent)) {
                    popFragment()
                }
            } else {
                showApiError(R.string.forgot_password_title, it.error)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ForgotPasswordFragment()
    }
}