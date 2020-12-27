package com.tolerancegate.ui.common

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import app.core.base.BaseActivity
import app.core.base.BaseFragment
import app.core.custom.TopHeader
import app.uicomponents.CustomActionBtn
import app.uicomponents.CustomFollowBtn
import app.uicomponents.extensions.getBool
import app.uicomponents.extensions.getObjectIds
import app.utilities.Enums
import com.parse.ParseObject
import com.parse.ParseUser
import com.tolerancegate.R
import com.tolerancegate.ui.authentication.AuthenticationActivity
import javax.inject.Inject

open class BaseCommonFragment : BaseFragment() {

    lateinit var commonViewModel: CommonViewModel

    @Inject
    lateinit var factory: CommonViewModel.CommonViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonViewModel = ViewModelProviders.of(this, factory)[CommonViewModel::class.java]
    }

    fun startCommonActivity(rootScreen: Enums.CommonRootScreen) {
        activity?.let {
            navigationViewModel.commonRootScreen.postValue(rootScreen)
            CommonActivity.start(it)
        }
    }

    private fun checkAndUpdateFollowingStatus(userID: String, followBtn: CustomFollowBtn) {
        showProgress()
        if (userViewModel.isContainsInList(userID, "followings")) {
            userViewModel.removeFromList(userID, "followings").observe(this) {
                hideProgress()
                if (it) {
                    followBtn.setFollowing(false)
                }
            }
        } else {
            userViewModel.addInList(userID, "followings").observe(this) {
                hideProgress()
                if (it) {
                    followBtn.setFollowing(true)
                }
            }
        }
    }

    fun checkFollowingStatus(userID: String, followBtn: CustomFollowBtn) {
        if (userViewModel.hasCurrentUser()) {
            followBtn.setActive(true)
            followBtn.setFollowing(userViewModel.isContainsInList(userID, "followings"))
            followBtn.addActionMethod {
                checkAndUpdateFollowingStatus(userID, followBtn)
            }
        }
    }

    private fun allowMasterPermission(user: ParseObject, key: String) = user.getBool(key)

    private fun allowPermissionForUser(user: ParseObject, key: String) =
        user.getObjectIds(key).contains(userViewModel.getUserId())

    fun checkPermission(user: ParseObject, btn: CustomActionBtn, userPermission: String, masterPermission: String) {
        if (!userViewModel.hasCurrentUser()) {
            return
        } else if (allowPermissionForUser(user, userPermission)) {
            btn.setActive(true)
        }
        if (allowMasterPermission(user, masterPermission)) {
            btn.setActive(true)
        }
    }

    fun showUserPermissionDialog() {
        if (userViewModel.hasCurrentUser()) {
            activity?.let {
                var dialogUserPermission = DialogUserPermission.instance()
                dialogUserPermission.show(it.supportFragmentManager, "PERMISSION_DIALOG")
            }
        } else {
            showConfirmationDialog(
                R.string.you_are_not_logged_in,
                R.string.please_login_before_using_this_feature,
                R.string.login
            ) {
                activity?.let {
                    AuthenticationActivity.start(it)
                }
            }
        }
    }
}