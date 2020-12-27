package com.tolerancegate.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.core.base.BaseDialogFragment
import app.uicomponents.CustomSettingToggle
import com.tolerancegate.R
import kotlinx.android.synthetic.main.dialog_user_permission.*


class DialogUserPermission : BaseDialogFragment() {

    private lateinit var userID: String

    override fun getTheme(): Int {
        return R.style.StateListDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        return inflater.inflate(R.layout.dialog_user_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView.addActionForLeftIcon {
            dismiss()
        }
        globalViewModel.selectedUserId.observe(this) {
            if (!it.isNullOrEmpty()) {
                userID = it
                setCurrentSetting()
                setListeners()
            }
        }
    }

    private fun setCurrentSetting() {
        chatPermission.setItemChecked(userViewModel.isContainsInList(userID, "textChatList"))
        audioCallPermission.setItemChecked(userViewModel.isContainsInList(userID, "audioCallList"))
        videoCallPermission.setItemChecked(userViewModel.isContainsInList(userID, "videoCallList"))
        emailPermission.setItemChecked(userViewModel.isContainsInList(userID, "emailList"))
    }

    private fun setListeners() {
        chatPermission.addActionMethod(this@DialogUserPermission::onStateChange)
        audioCallPermission.addActionMethod(this@DialogUserPermission::onStateChange)
        videoCallPermission.addActionMethod(this@DialogUserPermission::onStateChange)
        emailPermission.addActionMethod(this@DialogUserPermission::onStateChange)
    }

    private fun onStateChange(view: CustomSettingToggle, status: Boolean) {
        when (view) {
            chatPermission -> updateUserPermission("textChatList", status)
            audioCallPermission -> updateUserPermission("audioCallList", status)
            videoCallPermission -> updateUserPermission("videoCallList", status)
            emailPermission -> updateUserPermission("emailList", status)
        }
    }

    private fun updateUserPermission(key: String, status: Boolean) {
        showProgress()
        if (status) {
            userViewModel.addInList(userID, key).observe(activity!!, {
                hideProgress()
            })
        } else {
            userViewModel.removeFromList(userID, key).observe(activity!!, {
                hideProgress()
            })
        }
    }

    companion object {
        @JvmStatic
        fun instance() = DialogUserPermission()
    }
}