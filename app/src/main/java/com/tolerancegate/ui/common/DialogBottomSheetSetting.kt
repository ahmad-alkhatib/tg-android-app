package com.tolerancegate.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tolerancegate.R
import kotlinx.android.synthetic.main.dialog_bottom_sheet_setting.*

class DialogBottomSheetSetting : BottomSheetDialogFragment() {

    private var onButtonEditClick: () -> Unit = {}
    private var onButtonSettingClick: () -> Unit = {}
    private var onButtonChangePasswordClick: () -> Unit = {}
    private var onButtonLogOutClick: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_bottom_sheet_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editProfileBtn.setOnClickListener {
            onButtonEditClick()
            dismiss()
        }
        settingsBtn.setOnClickListener {
            onButtonSettingClick()
            dismiss()
        }
        changePasswordBtn.setOnClickListener {
            onButtonChangePasswordClick()
            dismiss()
        }
        logoutBtn.setOnClickListener {
            onButtonLogOutClick()
            dismiss()
        }
    }

    fun addButtonEditProfileAction(onButtonClick: () -> Unit) {
        this.onButtonEditClick = onButtonClick;
    }

    fun addButtonSettingsAction(onButtonClick: () -> Unit) {
        this.onButtonSettingClick = onButtonClick;
    }

    fun addButtonChangePasswordAction(onButtonClick: () -> Unit) {
        this.onButtonChangePasswordClick = onButtonClick;
    }

    fun addButtonLogoutAction(onButtonClick: () -> Unit) {
        this.onButtonLogOutClick = onButtonClick;
    }

    companion object {
        @JvmStatic
        fun instance() = DialogBottomSheetSetting()
    }
}