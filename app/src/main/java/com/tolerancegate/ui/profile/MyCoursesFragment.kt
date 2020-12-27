package com.tolerancegate.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.apirequest.parse.ClassName
import app.core.listeners.SingleLiveEvent
import com.parse.ParseObject
import com.parse.ParseQuery
import com.tolerancegate.R
import com.tolerancegate.ui.common.BaseCommonFragment
import com.tolerancegate.ui.common.DialogBottomSheetCourse
import com.tolerancegate.ui.common.DialogListFilters
import com.tolerancegate.ui.home.CoursesListAdapter
import kotlinx.android.synthetic.main.fragment_courses.*
import javax.inject.Inject

class MyCoursesFragment : BaseCommonFragment() {

    private var filterSubjectList: SingleLiveEvent<List<ParseObject>> = SingleLiveEvent()
    private var filterUpdated: SingleLiveEvent<Boolean> = SingleLiveEvent()

    @Inject
    lateinit var coursesListAdapter: CoursesListAdapter
    private var isApiCalled = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenTitle(R.string.courses)
        showBackIcon()
        setMenuIcon(R.drawable.nav_plus) {
            onAddClick()
        }
        setMenuIcon2(R.drawable.nav_filter) {
            showFilterDialog()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                coursesListAdapter.filterList(s.toString())
            }
        })

        coursesListAdapter.addActionForInfoClick {
            showCoursesOptions(it)
        }
        coursesListAdapter.canShowStatusView(true)
        recyclerView.adapter = coursesListAdapter

        refreshLayout.setOnRefreshListener {
            clearListAndCallData()
        }

        filterUpdated.observe(this, { isUpdated ->
            if (isUpdated) {
                clearListAndCallData()
                filterUpdated.postValue(false)
            }
        })

        if (!isApiCalled) {
            isApiCalled = true
            callApiToGetData()
        }

        globalViewModel.courseListUpdated.observe(viewLifecycleOwner, {
            if (it) {
                clearListAndCallData()
                globalViewModel.courseListUpdated.postValue(false)
            }
        })
    }

    private fun showCoursesOptions(courseObject: ParseObject) {
        val dialogBottomSheetCourse = DialogBottomSheetCourse.instance()
        dialogBottomSheetCourse.addButtonViewAction {
            globalViewModel.selectedCourseId.postValue(courseObject.objectId)
            pushFragment(CourseInfoFragment.newInstance())
        }
        dialogBottomSheetCourse.addButtonDeleteAction {
            showDeleteCourseDialog(courseObject)
        }
        dialogBottomSheetCourse.addButtonEditAction {
            pushFragment(AddEditCourseFragment.newInstance(courseObject.objectId))
        }
        activity?.let {
            dialogBottomSheetCourse.show(it.supportFragmentManager, "BOTTOM_SHEET_DIALOG")
        }
    }

    private fun callApiToDeleteCourse(courseObject: ParseObject) {
        showProgress()
        courseObject.put("isDeleted", true)
        commonViewModel.saveOrUpdateObject(courseObject).observe(activity!!, {
            hideProgress()
            if (it.isSuccessful) {
                coursesListAdapter.removeItem(courseObject.objectId)
            } else {
                showApiError(R.string.delete_course, it.error)
            }
        })
    }

    private fun showDeleteCourseDialog(courseObject: ParseObject) {
        showConfirmationDialog(R.string.delete_course, R.string.do_you_want_to_delete_this_course) {
            callApiToDeleteCourse(courseObject)
        }
    }

    private fun clearListAndCallData() {
        coursesListAdapter.setItemList(listOf())
        recyclerView.adapter = coursesListAdapter
        callApiToGetData()
    }

    private fun callApiToGetData() {
        refreshLayout.isRefreshing = true
        val query = ParseQuery.getQuery<ParseObject>(ClassName.COURSE)
        if (!filterSubjectList.value.isNullOrEmpty()) {
            query.whereContainedIn("subject", filterSubjectList.value)
        }
        query.whereEqualTo("instructor", userViewModel.getCurrentUser())

        commonViewModel.findCoursesList(query).observe(this, { response ->
            refreshLayout.isRefreshing = false
            if (response.isSuccessful) {
                coursesListAdapter.setItemList(response.data!!)
                coursesListAdapter.notifyDataSetChanged()
            }
        })
    }

    fun showFilterDialog() {
        activity?.let {
            var filterListDialog = DialogListFilters.newInstance(DialogListFilters.FILTER_SUBJECT);
            filterListDialog.setFilterSubjectList(filterSubjectList)
            filterListDialog.setFilterUpdatedEvent(filterUpdated)
            filterListDialog.show(it.supportFragmentManager, "FILTER_DIALOG")
        }
    }

    fun onAddClick() {
        if (!userViewModel.isMembershipActive() && userViewModel.shouldChargeInstructor()) {
            showDialogForInActiveMembership()
        } else if (!userViewModel.isActive()) {
            showDialogForInActiveAccount()
        } else if (!userViewModel.isApproved()) {
            showDialogForNotApprovedAccount()
        } else {
            addNewCourse()
        }
    }

    private fun showDialogForInActiveMembership() {
        showConfirmationDialog(
            R.string.membership,
            R.string.your_membership_is_not_active_for_course,
            R.string.subscribe_now
        ) {
            // TODO open membership screen
        }
    }

    private fun showDialogForInActiveAccount() {
        showMessageDialog(R.string.account_status, R.string.your_account_is_inactive_for_course)
    }

    private fun showDialogForNotApprovedAccount() {
        showMessageDialog(R.string.account_status, R.string.your_account_is_not_approved_yet)
    }

    fun addNewCourse() {
        pushFragment(AddEditCourseFragment.newInstance())
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyCoursesFragment()
    }
}





