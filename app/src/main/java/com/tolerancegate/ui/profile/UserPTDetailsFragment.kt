package com.tolerancegate.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.apirequest.parse.Property
import app.apirequest.parse.Property.FULL_NAME
import app.apirequest.parse.Property.SPECIALIZATION
import app.apirequest.parse.Property.SUBJECTS_TAUGHT
import app.apirequest.parse.PropertyValue.STUDENT
import app.uicomponents.extensions.*
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.tolerancegate.R
import com.tolerancegate.R.string.years
import com.tolerancegate.ui.common.BaseCommonFragment
import kotlinx.android.synthetic.main.fragment_my_profile.descriptionTv
import kotlinx.android.synthetic.main.fragment_my_profile.subjectsTv
import kotlinx.android.synthetic.main.fragment_my_profile.userNameTv
import kotlinx.android.synthetic.main.fragment_my_profile.userProfileImage
import kotlinx.android.synthetic.main.fragment_user_pt_details.*
import kotlinx.android.synthetic.main.fragment_user_pt_details.addressTv
import kotlinx.android.synthetic.main.fragment_user_pt_details.callBtn
import kotlinx.android.synthetic.main.fragment_user_pt_details.emailBtn
import kotlinx.android.synthetic.main.fragment_user_pt_details.followBtn
import kotlinx.android.synthetic.main.fragment_user_pt_details.followLayout
import kotlinx.android.synthetic.main.fragment_user_pt_details.messageBtn
import kotlinx.android.synthetic.main.fragment_user_pt_details.videoCallBtn

class UserPTDetailsFragment : BaseCommonFragment() {

    lateinit var user: ParseObject
    private lateinit var userID: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_pt_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBackIcon()

        globalViewModel.selectedUserId.observe(this) {
            if (!it.isNullOrEmpty()) {
                userID = it
                getUserDetails()
            }
        }
    }

    fun getUserDetails() {
        showProgress()
        commonViewModel.getUserPTDetails(userID).observe(this, {
            hideProgress()
            if (it.isSuccessful) {
                it.data?.let {
                    user = it
                    setUserDetails()
                }
            }
        })
    }

    private fun showStudentPTDetails() {
        userNameTv.text = user.getStr(Property.FULL_NAME)
        studentLayout.show()
        seeksToLearnTv.text = user.getObjects("ptSeeksToLearn").toStr()
        tuitionTypeTv.text = user.getObjects("ptTuitionType").toStr()
        setScreenTitle(R.string.student_profile)
        studentLayout.show()
    }

    private fun showInstructorPTDetails() {
        userNameTv.text = user.getNameWithDegree()
        tutorLayout.show()
        yearsOfExperienceTv.text = "${user.getStr("ptTuitionType", "0.0")} ${getString(years)}"
        feesPerHourTv.text = "${user.getStr("ptCurrency", "$")} ${user.getStr("ptFeesPerHour", "0.0")}"
        tutoringExperienceTv.text = user.getStr("ptTutoringExperience")
        teachingModeTv.text = user.getObjects("ptTeachingMode").toStr()
        setScreenTitle(R.string.instructor_profile)
        tutorLayout.show()
        followLayout.show()
    }

    private fun getCurrentUserId(): String {
        if (userViewModel.hasCurrentUser()) {
            return userViewModel.getUserId()
        }
        return ""
    }

    private fun setUserDetails() {
        ImageLoaderUtils.loadCircleImage(
            user.getImageUrl(),
            userProfileImage,
            R.drawable.image_placeholder_user_gray
        )
        var descriptionText = ""
        var subjects = ""
        var address = user.getAddress()
        if (user.getStr("accountType") == STUDENT) {
            descriptionText = user.getObject("programOfStudy")?.getStrLocale("name") ?: ""
            subjects = user.getObject("degree")?.getStrLocale("name") ?: ""
            showStudentPTDetails()
        } else {
            descriptionText = user.getStr(SPECIALIZATION)
            subjects = user.getObjects(SUBJECTS_TAUGHT).toStr()
            showInstructorPTDetails()
        }

        if (user.objectId == getCurrentUserId()) {
            setScreenTitle(R.string.my_profile)
            userNameTv.text = user.getStr(Property.FULL_NAME)
        } else {
            checkFollowingStatus(user.objectId, followBtn)
            checkAllPermissions()
            menuIcon.show()
            menuIcon.setOnClickListener {
                showUserPermissionDialog()
            }
        }

        if (descriptionText.isNotEmpty()) {
            descriptionTv.show()
            descriptionTv.text = descriptionText
        }

        if (subjects.isNotEmpty()) {
            subjectsTv.show()
            subjectsTv.text = subjects
        }

        if (address.isNotEmpty()) {
            addressTv.show()
            addressTv.text = address
        }
    }

    private fun checkAllPermissions() {
        checkPermission(user, emailBtn, "emailList", "emailMasterPermission")
        checkPermission(user, videoCallBtn, "videoCallList", "videoCallMasterPermission")
        checkPermission(user, callBtn, "audioCallList", "audioCallMasterPermission")
        checkPermission(user, messageBtn, "textChatList", "chatMasterPermission")
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserPTDetailsFragment()
    }
}





