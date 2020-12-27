package com.tolerancegate.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.apirequest.parse.ClassName
import app.core.listeners.SingleLiveEvent
import app.uicomponents.extensions.remove
import app.utilities.Enums
import app.utilities.Enums.CommonRootScreen.USER_DETAILS
import app.utilities.Enums.InstructorSearchType.*
import com.parse.ParseObject
import com.parse.ParseQuery
import com.tolerancegate.R
import com.tolerancegate.ui.common.BaseCommonFragment
import com.tolerancegate.ui.common.CommonActivity
import com.tolerancegate.ui.common.DialogListFilters
import com.tolerancegate.ui.common.DialogListFilters.Companion.FILTER_ALL
import com.tolerancegate.ui.common.DialogListFilters.Companion.FILTER_COUNTRY
import com.tolerancegate.ui.common.DialogListFilters.Companion.FILTER_COUNTRY_SUBJECT
import com.tolerancegate.ui.profile.UserDetailFragment
import kotlinx.android.synthetic.main.fragment_institutions.recyclerView
import kotlinx.android.synthetic.main.fragment_institutions.refreshLayout
import kotlinx.android.synthetic.main.fragment_institutions.searchEditText
import kotlinx.android.synthetic.main.fragment_instructors.*
import javax.inject.Inject

class InstructorsFragment : BaseCommonFragment() {

    private var filterSubjectList: SingleLiveEvent<List<ParseObject>> = SingleLiveEvent()
    private var filterCity: SingleLiveEvent<ParseObject> = SingleLiveEvent()
    private var filterCountry: SingleLiveEvent<ParseObject> = SingleLiveEvent()
    private var filterListOrder: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private var filterUpdated: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private var itemList: MutableList<ParseObject> = mutableListOf()

    @Inject
    lateinit var instructorListAdapter: InstructorListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instructors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenTitle(getScreenTitle())
        showBackIcon()
        setMenuIcon(R.drawable.nav_filter) {
            showFilterDialog()
        }

        allRadioBtn.isChecked = true

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                instructorListAdapter.filterList(s.toString())
            }
        })

        instructorListAdapter.setItemList(itemList)
        navigationViewModel.instructorSearchType.value?.let { instructorListAdapter.setItemType(it) }
        recyclerView.adapter = instructorListAdapter

        if (itemList.isNullOrEmpty()) {
            callApiToGetData()
        }

        refreshLayout.setOnRefreshListener {
            clearListAndCallData()
        }

        filterUpdated.observe(this, { isUpdated ->
            if (isUpdated) {
                clearListAndCallData()
                filterUpdated.postValue(false)
            }
        })

        arguments?.let {
            if (it.getBoolean(FROM_COMMON, false)) {
                segmentGroup.remove()
            }
            else{
                headerView.remove()
            }
        }

        if (!userViewModel.hasCurrentUser()) {
            segmentGroup.remove()
        }

        segmentGroup.setOnCheckedChangeListener { radioGroup, radioButtonID ->
            clearListAndCallData()
        }

        instructorListAdapter.addActionForItemClick {
            globalViewModel.selectedUserId.postValue(it.objectId)
            if (activity is CommonActivity) {
                pushFragment(UserDetailFragment.newInstance())
            } else {
                startCommonActivity(USER_DETAILS)
            }
        }
    }

    private fun clearListAndCallData() {
        instructorListAdapter.setItemList(listOf())
        recyclerView.adapter = instructorListAdapter
        callApiToGetData()
    }

    private fun callApiToGetData() {
        refreshLayout.isRefreshing = true
        val query = ParseQuery.getQuery<ParseObject>(ClassName.USER)
        filterCountry.value?.let {
            query.whereEqualTo("country", it)
        }
        filterCity.value?.let {
            query.whereEqualTo("city", it)
        }

        if (!filterSubjectList.value.isNullOrEmpty()) {
            if (navigationViewModel.instructorSearchType.value == PT_STUDENT) {
                query.whereContainedIn("subjectsTaught", filterSubjectList.value)
            } else {
                query.whereContainedIn("ptSeeksToLearn", filterSubjectList.value)
            }
        }

        addQueryFilters(query)

        if (userViewModel.hasCurrentUser()) {
            query.whereNotEqualTo("objectId", userViewModel.getUserId())
        }

        query.orderByDescending("updatedAt")

        filterListOrder.value?.let {
            if (it) query.orderByDescending("updatedAt") else query.orderByAscending("updatedAt")
        }
        query.builder.toString()
        commonViewModel.findAllInstructors(query).observe(this, { response ->
            refreshLayout.isRefreshing = false
            if (response.isSuccessful) {
                itemList = response.data as MutableList<ParseObject>
                instructorListAdapter.setItemList(itemList)
                instructorListAdapter.notifyDataSetChanged()
            }
        })
    }

    fun showFilterDialog() {
        activity?.let {
            var filterListDialog = DialogListFilters.newInstance(getFilterType());
            filterListDialog.setFilterCity(filterCity)
            filterListDialog.setFilterCountry(filterCountry)
            filterListDialog.setFilterSubjectList(filterSubjectList)
            filterListDialog.setFilterListOrder(filterListOrder)
            filterListDialog.setFilterUpdatedEvent(filterUpdated)
            filterListDialog.show(it.supportFragmentManager, "FILTER_DIALOG")
        }
    }

    private fun getFilterType(): String {
        return when (navigationViewModel.instructorSearchType.value) {
            FOLLOWERS -> FILTER_COUNTRY
            FOLLOWING -> FILTER_COUNTRY
            PT_STUDENT -> FILTER_ALL
            PT_TUTOR -> FILTER_ALL
            else -> FILTER_COUNTRY_SUBJECT
        }
    }

    private fun getScreenTitle(): Int {
        return when (navigationViewModel.instructorSearchType.value) {
            FOLLOWERS -> R.string.followers
            FOLLOWING -> R.string.following
            PT_STUDENT -> R.string.private_tutors
            PT_TUTOR -> R.string.private_tutoring
            else -> R.string.instructors
        }
    }

    private fun addQueryFilters(query: ParseQuery<ParseObject>) {
        when (navigationViewModel.instructorSearchType.value) {
            FOLLOWERS -> {
                query.whereEqualTo("followings", userViewModel.getUserId())
            }
            FOLLOWING -> {
                query.whereContainedIn("objectId", userViewModel.getFollowingIds())
                query.whereEqualTo("accountType", "instructor")
            }
            PT_STUDENT -> {
                if (userViewModel.shouldChargePT()) {
                    query.whereEqualTo("isPrivateTutoringActive", true)
                }
                query.whereEqualTo("hasPrivateTutoring", true)
                query.whereEqualTo("accountType", "instructor")
            }
            PT_TUTOR -> {
                if (userViewModel.shouldChargePT()) {
                    query.whereEqualTo("isPrivateTutoringActive", true)
                }
                query.whereEqualTo("hasPrivateTutoring", true)
                query.whereEqualTo("accountType", "student")
            }
            else -> {
                if (followingRadioBtn.isChecked) {
                    query.whereContainedIn("objectId", userViewModel.getFollowingIds())
                    query.whereEqualTo("accountType", "instructor")
                }
                query.whereEqualTo("accountType", "instructor")
                query.whereEqualTo("isApproved", true)
                query.whereEqualTo("isActive", true)
                query.whereEqualTo("isMembershipActive", true)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(asCommon: Boolean = false) = InstructorsFragment().apply {
            arguments = Bundle().apply {
                putBoolean(FROM_COMMON, asCommon)
            }
        }

        const val FROM_COMMON = "common"
    }
}





