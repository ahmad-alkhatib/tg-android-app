package com.tolerancegate.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.apirequest.parse.Property.ACCOUNT_TYPE
import app.apirequest.parse.Property.SPECIALIZATION
import app.apirequest.parse.Property.SUBJECTS_TAUGHT
import app.apirequest.parse.PropertyValue
import app.uicomponents.CustomRecyclerView
import app.uicomponents.extensions.*
import app.utilities.Enums.CommonRootScreen.*
import app.utilities.Enums.InstructorSearchType.FOLLOWERS
import app.utilities.Enums.InstructorSearchType.FOLLOWING
import app.utilities.ImageLoaderUtils
import com.ncapdevi.fragnav.FragNavController.Companion.TAB3
import com.ncapdevi.fragnav.FragNavController.Companion.TAB4
import com.parse.ParseObject
import com.tolerancegate.R
import com.tolerancegate.ui.authentication.AuthenticationActivity
import com.tolerancegate.ui.common.DialogBottomSheetSetting
import com.tolerancegate.ui.home.BaseHomeFragment
import com.tolerancegate.ui.home.CoursesAdapter
import com.tolerancegate.ui.home.HomeActivity
import com.tolerancegate.ui.home.InstructorAdapter
import kotlinx.android.synthetic.main.fragment_my_profile.*
import javax.inject.Inject

class MyProfileFragment : BaseHomeFragment() {

    lateinit var user: ParseObject
    lateinit var currentUser: ParseObject

    @Inject
    lateinit var adapterBookmark: CoursesAdapter

    @Inject
    lateinit var followingAdapter: InstructorAdapter

    @Inject
    lateinit var followersAdapter: InstructorAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenTitle(R.string.my_profile)
        showBackIcon()
        setMenuIcon(R.drawable.nav_setting_dots) { showProfileOptions() }
        currentUser = userViewModel.getCurrentUser()
        if (this::user.isInitialized) {
            setUserDetails()
        } else {
            getUserDetails(true)
        }

        navigationViewModel.userUpdated.observe(viewLifecycleOwner, {
            if (it) {
                getUserDetails(false)
                navigationViewModel.userUpdated.postValue(false)
            }
        })

        followersAdapter.addActionForItemClick(this::goToUserDetails)
        followingAdapter.addActionForItemClick(this::goToUserDetails)
    }

    fun goToUserDetails(it: ParseObject) {
        globalViewModel.selectedUserId.postValue(it.objectId)
        startCommonActivity(USER_DETAILS)
    }

    fun getUserDetails(showProgress: Boolean) {
        if (showProgress)
            showProgress()
        homeViewModel.getCurrentUserObj(userViewModel.getUserId()).observe(this, {
            hideProgress()
            if (it.isSuccessful) {
                it.data?.let {
                    user = it
                    setUserDetails()
                }
            }
        })
    }

    private fun setViewsForInstitute() {
        instituteLogoImageCard.show()
        userProfileImage.remove()
        ImageLoaderUtils.loadImageFromServerCrop(
            user.getImageUrl(),
            instituteLogoImage,
            R.drawable.image_placeholder_gray
        )
        myAdvertiseBts.show()
        myAdvertiseBts.setOnClickListener {
            // TODO show subscription details for this user
        }
    }

    private fun setViewsForInstructor() {
        ptBtnText.text = getString(R.string.view_my_private_tutoring)

        var descriptionText = user.getStr(SPECIALIZATION)
        if (descriptionText.isNotEmpty()) {
            descriptionTv.show()
            descriptionTv.text = descriptionText
        }

        var subjects = user.getObjects(SUBJECTS_TAUGHT).toStr()
        if (subjects.isNotEmpty()) {
            subjectsTv.show()
            subjectsTv.text = subjects
        }

        myCoursesBts.show()
        myCoursesBts.setOnClickListener {
            startCommonActivity(MY_COURSES)
        }

        followersRecyclerView.show()
    }

    private fun setViewsForStudent() {
        ptBtnText.text = getString(R.string.i_seek_to_learn_view_here)
    }

    private fun canShowPrivateTutor(): Boolean {
        return userViewModel.isPrivateTutoringActive() && true
        // TODO ARK - at the place of true second second condition should be check from remoteConfig
    }

    private fun setUserDetails() {
        userNameTv.text = userViewModel.getFullName()

        var accountType = user.getStr(ACCOUNT_TYPE)
        if (accountType == PropertyValue.INSTITUTE) {
            setViewsForInstitute()
        } else {
            ImageLoaderUtils.loadCircleImage(
                user.getImageUrl(),
                userProfileImage,
                R.drawable.image_placeholder_user_gray
            )
            if (canShowPrivateTutor()) {
                showPrivateTutoringBtn.show()
                showPrivateTutoringBtn.setOnClickListener {
                    globalViewModel.selectedUserId.postValue(userViewModel.getUserId())
                    startCommonActivity(USER_PT_DETAILS)
                }
            }
            if (accountType == PropertyValue.INSTRUCTOR) {
                setViewsForInstructor()
            } else if (accountType == PropertyValue.STUDENT) {
                setViewsForStudent()
            }
        }

        subscriptionBtn.setOnClickListener {
            // TODO show subscription details for this user
        }

        myChatsBts.setOnClickListener {
            startCommonActivity(USER_CONVERSATION)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyProfileFragment()
    }

    private fun showProfileOptions() {
        val profileSettingOptions = DialogBottomSheetSetting.instance()
        profileSettingOptions.addButtonEditProfileAction {
            activity?.let {
                AuthenticationActivity.start(it, userType = userViewModel.getAccountType())
            }
        }
        profileSettingOptions.addButtonChangePasswordAction {
            startCommonActivity(CHANGE_PASSWORD)
        }
        profileSettingOptions.addButtonLogoutAction {
            userViewModel.logout()
            activity?.let {
                HomeActivity.start(it, startAuth = true)
            }
        }
        profileSettingOptions.addButtonSettingsAction {
            startCommonActivity(SETTING)
        }
        activity?.let {
            profileSettingOptions.show(it.supportFragmentManager, "BOTTOM_SHEET_DIALOG")
        }
    }

    private fun getUserBookmarks(recyclerView: CustomRecyclerView) {
        var objIds: List<String> = currentUser.getObjectIds("lectureBookmarkList")
        homeViewModel.findUserBookmarks(objIds).observe(this, { dataList ->
            if (dataList.isSuccessful && !dataList.data.isNullOrEmpty()) {
                adapterBookmark.setItemList(dataList.data!!)
                recyclerView.setListAdapter(adapterBookmark)
            } else {
                recyclerView.showEmptyData()
            }
        })

        recyclerView.addActionForViewAll {
            startCommonActivity(USER_BOOKMARK)
        }

        recyclerView.addActionForDefaultBtn {
            switchTab(TAB4)
        }
    }

    private fun getUserFollowing(recyclerView: CustomRecyclerView) {
        var objIds: List<String> = currentUser.getObjectIds("followings")
        homeViewModel.findUserFollowing(objIds).observe(this, { dataList ->
            if (dataList.isSuccessful && !dataList.data.isNullOrEmpty()) {
                followingAdapter.setItemList(dataList.data!!)
                recyclerView.setListAdapter(followingAdapter)
            } else {
                recyclerView.showEmptyData()
            }
        })
        recyclerView.addActionForViewAll {
            navigationViewModel.instructorSearchType.postValue(FOLLOWING)
            startCommonActivity(INSTRUCTOR_LIST)
        }
        recyclerView.addActionForDefaultBtn {
            switchTab(TAB3)
        }
    }

    private fun getUserFollowers(recyclerView: CustomRecyclerView) {
        homeViewModel.findUserFollowers(userViewModel.getUserId()).observe(this, { dataList ->
            if (dataList.isSuccessful && !dataList.data.isNullOrEmpty()) {
                followersAdapter.setItemList(dataList.data!!)
                recyclerView.setListAdapter(followersAdapter)
            } else {
                recyclerView.showEmptyData()
            }
        })
        recyclerView.addActionForViewAll {
            navigationViewModel.instructorSearchType.postValue(FOLLOWERS)
            startCommonActivity(INSTRUCTOR_LIST)
        }
    }

    override fun onResume() {
        super.onResume()
        getUserBookmarks(bookmarkedRecyclerView)
        getUserFollowing(followingRecyclerView)
        getUserFollowers(followersRecyclerView)
    }
}





