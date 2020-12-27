package com.tolerancegate.ui.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import app.apirequest.parse.Property.AUDIO_CALL_MASTER_PERMISSION
import app.apirequest.parse.Property.CHAT_MASTER_PERMISSION
import app.apirequest.parse.Property.EMAIL_MASTER_PERMISSION
import app.apirequest.parse.Property.VIDEO_CALL_MASTER_PERMISSION
import app.core.R.string
import app.uicomponents.CustomActionView
import app.uicomponents.CustomSettingToggle
import app.uicomponents.extensions.getBool
import app.uicomponents.extensions.remove
import com.parse.ParseUser
import com.tolerancegate.R
import com.tolerancegate.ui.splash.SplashActivity
import kotlinx.android.synthetic.main.fragment_app_setting.*

class AppSettingFragment : BaseCommonFragment() {

    private val ENGLISH = "en"
    private val ARABIC = "ar"
    private var forced = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenTitle(R.string.settings)
        showBackIcon()
        updateUi()
        setListeners()
    }

    private fun setCurrentSetting() {
        var user = userViewModel.getCurrentUser()
        chatPermission.setItemChecked(user.getBool(CHAT_MASTER_PERMISSION))
        audioCallPermission.setItemChecked(user.getBool(AUDIO_CALL_MASTER_PERMISSION))
        videoCallPermission.setItemChecked(user.getBool(VIDEO_CALL_MASTER_PERMISSION))
        emailPermission.setItemChecked(user.getBool(EMAIL_MASTER_PERMISSION))
    }

    private fun updateUi() {
        englishRadioBtn.isChecked = globalViewModel.getAppLanguage() == ENGLISH
        arabicRadioBtn.isChecked = globalViewModel.getAppLanguage() == ARABIC

        if (userViewModel.hasCurrentUser()) {
            setCurrentSetting()
        } else {
            permissionSettingLayout.remove()
        }
    }

    private fun setListeners() {
        segmentGroup.setOnCheckedChangeListener { radioGroup, radioButtonID ->
            if (forced.not()) {
                when (radioButtonID) {
                    R.id.englishRadioBtn -> showLanguageChangeDialog(ENGLISH, arabicRadioBtn)
                    R.id.arabicRadioBtn -> showLanguageChangeDialog(ARABIC, englishRadioBtn)
                }
            } else {
                forced = false
            }
        }

        chatPermission.addActionMethod(this@AppSettingFragment::onStateChange)
        audioCallPermission.addActionMethod(this@AppSettingFragment::onStateChange)
        videoCallPermission.addActionMethod(this@AppSettingFragment::onStateChange)
        emailPermission.addActionMethod(this@AppSettingFragment::onStateChange)

        aboutUsBtn.addActionMethod(this@AppSettingFragment::showWebPageUrl)
        termsAndConditionBtn.addActionMethod(this@AppSettingFragment::showWebPageUrl)
        privacyPolicyBtn.addActionMethod(this@AppSettingFragment::showWebPageUrl)
        feedbackBtn.addActionMethod {
            emailFeedback(it.getUrl())
        }
        shareAppBtn.addActionMethod {
            shareApp()
        }
    }

    private fun onStateChange(view: CustomSettingToggle, status: Boolean) {
        var user = userViewModel.getCurrentUser()
        when (view) {
            chatPermission -> user.put(CHAT_MASTER_PERMISSION, status)
            audioCallPermission -> user.put(AUDIO_CALL_MASTER_PERMISSION, status)
            videoCallPermission -> user.put(VIDEO_CALL_MASTER_PERMISSION, status)
            emailPermission -> user.put(EMAIL_MASTER_PERMISSION, status)
        }
        updateUserPermission(user)
    }

    private fun updateUserPermission(user: ParseUser) {
        showProgress()
        commonViewModel.updateUser(user).observe(activity!!, {
            hideProgress()
        })
    }

    private fun showLanguageChangeDialog(selectedLanguage: String, radioButton: RadioButton) {
        activity?.let {
            dialogProvider.showAndroidDialogMessage(it, getString(R.string.language),
                getString(R.string.do_you_want_to_change_the_language),
                {
                    updateLanguageAndStartFromSplash(selectedLanguage)
                },
                {
                    forced = true
                    radioButton.isChecked = true
                }
            )
        }
    }

    private fun updateLanguageAndStartFromSplash(selectedLanguage: String) {
        activity?.let {
            globalViewModel.setAppLanguage(selectedLanguage)
            SplashActivity.start(it)
        }
    }

    private fun emailFeedback(email: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$email"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            intent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            showMessageDialog(string.email, string.mail_client_not_found)
        }
    }

    private fun shareApp() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        share.putExtra(Intent.EXTRA_SUBJECT, getString(string.app_name))
        share.putExtra(Intent.EXTRA_TITLE, getString(string.app_name))
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_link))
        startActivity(Intent.createChooser(share, getString(string.share_app)))
    }

//    private fun openUrl(view: CustomActionView) {
//        val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
//        defaultBrowser.data = Uri.parse(view.getUrl())
//        startActivity(defaultBrowser)
//    }

    private fun showWebPageUrl(view: CustomActionView) {
        activity?.let {
            var webPageFragment = DialogWebPageFragment.instance()
            webPageFragment.setTitle(view.getText())
            webPageFragment.setPageUrl(view.getUrl())
            webPageFragment.show(it.supportFragmentManager, "WEB_PAGE_DIALOG")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AppSettingFragment()
    }
}