package com.tolerancegate.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.apirequest.parse.PropertyValue.INSTITUTE
import app.apirequest.parse.PropertyValue.INSTRUCTOR
import app.apirequest.parse.PropertyValue.STUDENT
import app.uicomponents.extensions.*
import app.utilities.AppUtils
import app.utilities.Enums
import app.utilities.Enums.CommonRootScreen.INSTRUCTOR_LIST
import app.utilities.Enums.InstructorSearchType.PT_STUDENT
import app.utilities.Enums.InstructorSearchType.PT_TUTOR
import com.ncapdevi.fragnav.FragNavController.Companion.TAB2
import com.ncapdevi.fragnav.FragNavController.Companion.TAB3
import com.ncapdevi.fragnav.FragNavController.Companion.TAB4
import com.parse.ParseObject
import com.tolerancegate.R
import com.tolerancegate.ui.authentication.AuthenticationActivity
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : BaseHomeFragment() {

    private var institutionList: List<ParseObject> = listOf()
    private var instructorList: List<ParseObject> = listOf()
    private var coursesList: List<ParseObject> = listOf()
    private var ptUserStudentList: List<ParseObject> = listOf()
    private var ptUserInstructorList: List<ParseObject> = listOf()
    private var ptUserIds: List<String> = listOf()
    private var canGetPrivateTutoringObjects = false
    private var isFirstTime = true

    @Inject
    lateinit var adapterInstitution: InstitutionAdapter

    @Inject
    lateinit var adapterPrivateTutoring: PrivateTutoringAdapter

    @Inject
    lateinit var adapterInstructor: InstructorAdapter

    @Inject
    lateinit var adapterCourses: CoursesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenTitle(R.string.home)
        hideBackIcon()
        setListeners()
        checkPrivateTutoringStatus()
        setAdaptersForRV()
        getRightMenuIcon().hide()

        navigationViewModel.userUpdated.observe(viewLifecycleOwner, {
            if (it) {
                getData(false)
                navigationViewModel.userUpdated.postValue(false)
            }
        })

        getData(isFirstTime)
    }

    private fun getData(showProgress: Boolean) {
        context?.let {
            doCallForGetHomeScreenData(AppUtils.getDeviceId(it), showProgress)
        }
    }

    private fun setAdaptersForRV() {
        adapterInstitution.setItemList(institutionList)
        adapterInstructor.setItemList(instructorList)
        adapterCourses.setItemList(coursesList)

        adapterInstructor.addActionForItemClick {
            globalViewModel.selectedUserId.postValue(it.objectId)
            startCommonActivity(Enums.CommonRootScreen.USER_DETAILS)
        }

        adapterPrivateTutoring.addActionForItemClick {
            globalViewModel.selectedUserId.postValue(it.objectId)
            startCommonActivity(Enums.CommonRootScreen.USER_PT_DETAILS)
        }

        institutionRecyclerView.adapter = adapterInstitution
        instructorRecyclerView.adapter = adapterInstructor
        coursesRecyclerView.adapter = adapterCourses
        privateTutoringRV.adapter = adapterPrivateTutoring
    }

    private fun doCallForGetHomeScreenData(deviceID: String, showProgress: Boolean) {
        if (showProgress)
            showProgress()
        homeViewModel.getHomeData(deviceID, "test").observe(this, { dataList ->
            if (dataList.isSuccessful) {
                isFirstTime = false
                institutionList = dataList.data?.filter { it.getStr("accountType") == "institute" } ?: listOf()
                instructorList = dataList.data?.filter { it.getStr("accountType") == "instructor" } ?: listOf()
                coursesList = dataList.data?.filter { it.className == "Course" } ?: listOf()

                ptUserIds =
                    dataList.data?.filter {
                        it.getBool("showOnHomeScreen_PT_Student") || it.getBool("showOnHomeScreen_PT_Tutor")
                    }?.map { it.objectId } ?: listOf()

                if (ptUserIds.isNotEmpty() && canGetPrivateTutoringObjects) {
                    getPrivateTutoringObjects()
                } else {
                    hideProgress()
                }
                adapterInstitution.setItemList(institutionList)
                adapterInstitution.notifyDataSetChanged()
                adapterInstructor.setItemList(instructorList)
                adapterInstructor.notifyDataSetChanged()
                adapterCourses.setItemList(coursesList)
                adapterCourses.notifyDataSetChanged()
            } else {
                hideProgress()
            }
        })
    }

    private fun getPrivateTutoringObjects() {
        homeViewModel.findPrivateTutoringObjects(ptUserIds).observe(this, { dataList ->
            hideProgress()
            if (dataList.isSuccessful) {
                ptUserStudentList = dataList.data?.filter { it.getBool("showOnHomeScreen_PT_Student") } ?: listOf()
                ptUserInstructorList = dataList.data?.filter { it.getBool("showOnHomeScreen_PT_Tutor") } ?: listOf()
                if (userViewModel.getAccountType() == INSTRUCTOR) {
                    adapterPrivateTutoring.setItemList(ptUserStudentList)
                    adapterPrivateTutoring.showAsStudents(true)
                    ptViewAllBtn.setOnClickListener {
                        navigationViewModel.instructorSearchType.postValue(PT_TUTOR)
                        startCommonActivity(INSTRUCTOR_LIST)
                    }
                } else if (userViewModel.getAccountType() == STUDENT) {
                    adapterPrivateTutoring.setItemList(ptUserInstructorList)
                    adapterPrivateTutoring.showAsStudents(false)
                    ptViewAllBtn.setOnClickListener {
                        navigationViewModel.instructorSearchType.postValue(PT_STUDENT)
                        startCommonActivity(INSTRUCTOR_LIST)
                    }
                }
                adapterPrivateTutoring.notifyDataSetChanged()
            }
        })
    }

    private fun checkPrivateTutoringStatus() {
        ptListView.remove()
        ptViewAllBtn.remove()
        canGetPrivateTutoringObjects = false

        if (userViewModel.hasCurrentUser()) {
            if (userViewModel.getAccountType() == INSTITUTE) {
                ptMainLayout.remove()
                return
            }
            if (userViewModel.isPrivateTutoringEnable()) {
                canGetPrivateTutoringObjects = true
                ptViewAllBtn.show()
                ptListView.show()
                ptDefaultCardView.remove()
            } else if (userViewModel.getAccountType() == INSTRUCTOR) {
                enablePTforInstructorBtn.show()
                enablePTforStudentbtn.remove()
            } else if (userViewModel.getAccountType() == STUDENT) {
                enablePTforStudentbtn.show()
                enablePTforInstructorBtn.remove()
            }
        }
    }

    private fun setListeners() {
        enablePTforStudentbtn.setOnClickListener {
            checkAndNavigateToPrivateTutoring(STUDENT)
        }
        enablePTforInstructorBtn.setOnClickListener { _ ->
            checkAndNavigateToPrivateTutoring(INSTRUCTOR)
        }
        viewAllInstituteBtn.setOnClickListener {
            switchTab(TAB2)
        }
        viewAllInstructorBtn.setOnClickListener {
            switchTab(TAB3)
        }
        viewAllCoursesBtn.setOnClickListener {
            switchTab(TAB4)
        }
    }

    private fun checkAndNavigateToPrivateTutoring(userType: String) {
        if (userViewModel.hasCurrentUser()) {
            showConfirmationDialog(
                R.string.private_tutoring,
                R.string.please_fill_private_tutoring_information_to_continue,
                R.string.update_profile
            ) {
                activity?.let {
                    AuthenticationActivity.start(it, userType = userViewModel.getAccountType())
                }
            }
        } else {
            showConfirmationDialog(
                R.string.you_are_not_logged_in,
                R.string.please_login_before_using_this_feature,
                R.string.login
            ) {
                activity?.let {
                    AuthenticationActivity.start(it, defaultPTStatus = true, userType = userType)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onResume() {
        super.onResume()
        checkPrivateTutoringStatus()
    }
}





