package com.tolerancegate.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.apirequest.parse.ClassName
import app.apirequest.parse.Property
import app.apirequest.parse.PropertyValue
import app.uicomponents.extensions.*
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.parse.ParseQuery
import com.tolerancegate.R
import com.tolerancegate.ui.common.BaseCommonFragment
import com.tolerancegate.ui.home.CoursesListAdapter
import com.tolerancegate.ui.home.InstitutionCategoryAdapter
import kotlinx.android.synthetic.main.fragment_my_profile.descriptionTv
import kotlinx.android.synthetic.main.fragment_my_profile.subjectsTv
import kotlinx.android.synthetic.main.fragment_my_profile.userNameTv
import kotlinx.android.synthetic.main.fragment_my_profile.userProfileImage
import kotlinx.android.synthetic.main.fragment_user_detail.*
import javax.inject.Inject

class UserDetailFragment : BaseCommonFragment() {

    lateinit var user: ParseObject
    private lateinit var userID: String

    @Inject
    lateinit var categoryAdapter: InstitutionCategoryAdapter

    @Inject
    lateinit var coursesListAdapter: CoursesListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_detail, container, false)
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

        menuIcon.setOnClickListener {
            showUserPermissionDialog()
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

    private fun showInstructorDetails() {
        userNameTv.text = user.getNameWithDegree()
        setScreenTitle(R.string.instructor_profile)
        coursesLayout.show()
        followLayout.show()
        var categoryList = user.getObjects("subjectsTaught")
        categoryAdapter.setDefaultText(getString(R.string.all))
        categoryAdapter.setItemList(categoryList)
        categoryRecyclerView.adapter = categoryAdapter
        clearListAndGetData()
        categoryAdapter.addActionForItemClick {
            clearListAndGetData()
        }
        checkFollowingStatus(user.objectId, followBtn)
    }

    private fun clearListAndGetData() {
        coursesListAdapter.setItemList(listOf())
        recyclerView.adapter = coursesListAdapter
        callApiToGetCourseList()
    }

    private fun callApiToGetCourseList() {
        loader.show()
        categoryAdapter.clickable = false
        val query = ParseQuery.getQuery<ParseObject>(ClassName.COURSE)
        query.whereEqualTo("instructor", user)
        query.whereEqualTo("isActive", true)
        categoryAdapter.getSelectedCategory()?.let {
            query.whereEqualTo("subject", it)
        }

        commonViewModel.findCoursesList(query).observe(this, { response ->
            loader.remove()
            categoryAdapter.clickable = true
            if (response.isSuccessful && !response.data.isNullOrEmpty()) {
                coursesListAdapter.setItemList(response.data!!)
                recyclerView.adapter = coursesListAdapter
            }
        })
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

        if (user.getStr("accountType") == PropertyValue.STUDENT) {
            userNameTv.text = user.getStr(Property.FULL_NAME)
            descriptionText = user.getObject("programOfStudy")?.getStrLocale("name") ?: ""
            subjects = user.getObject("degree")?.getStrLocale("name") ?: ""
            setScreenTitle(R.string.student_profile)
        } else {
            descriptionText = user.getStr(Property.SPECIALIZATION)
            subjects = user.getObjects(Property.SUBJECTS_TAUGHT).toStr()
            showInstructorDetails()
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

        // TODO in future
//        if(shouldChargePT){
//            viewPTDetailsBtn.remove()
//        }

        if(userViewModel.hasCurrentUser()){
            if (user.getBool("isPrivateTutoringActive") &&
                userViewModel.isPrivateTutoringActive()
            ) {
                viewPTDetailsBtn.show()
                viewPTDetailsBtn.setOnClickListener {
                    pushFragment(UserPTDetailsFragment.newInstance())
                }
            }
            checkAllPermissions()
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
        fun newInstance() = UserDetailFragment()
    }
}





