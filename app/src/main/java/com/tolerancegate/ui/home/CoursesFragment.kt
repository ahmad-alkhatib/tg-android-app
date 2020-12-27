package com.tolerancegate.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.apirequest.parse.ClassName
import app.core.listeners.SingleLiveEvent
import app.uicomponents.extensions.getBool
import app.uicomponents.extensions.getObject
import app.uicomponents.extensions.remove
import com.parse.ParseObject
import com.parse.ParseQuery
import com.tolerancegate.R
import com.tolerancegate.ui.common.DialogListFilters
import kotlinx.android.synthetic.main.fragment_courses.*
import kotlinx.android.synthetic.main.fragment_courses.recyclerView
import kotlinx.android.synthetic.main.fragment_courses.refreshLayout
import kotlinx.android.synthetic.main.fragment_courses.searchEditText
import kotlinx.android.synthetic.main.fragment_institutions.*
import javax.inject.Inject

class CoursesFragment : BaseHomeFragment() {

    private var filterSubjectList: SingleLiveEvent<List<ParseObject>> = SingleLiveEvent()
    private var filterUpdated: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private var itemList: MutableList<ParseObject> = mutableListOf()

    @Inject
    lateinit var coursesListAdapter: CoursesListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenTitle(R.string.course)
        showBackIcon()
        setMenuIcon(R.drawable.nav_filter) {
            showFilterDialog()
        }

        headerView.remove()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                coursesListAdapter.filterList(s.toString())
            }
        })

        coursesListAdapter.setItemList(itemList)
        recyclerView.adapter = coursesListAdapter

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
        query.whereEqualTo("isActive", true)
        homeViewModel.findCoursesList(query).observe(this, { response ->
            refreshLayout.isRefreshing = false
            if (response.isSuccessful) {

                var data = response.data?.filter {
                    var user = it.getObject("instructor") ?: ParseObject(ClassName.NONE)
                    val isApproved = user.getBool("isApproved")
                    val isActive = user.getBool("isActive")
                    val isMembershipActive = user.getBool("isMembershipActive")
                    if (isApproved && isActive && isMembershipActive) {
                        return@filter true
                    }
                    return@filter false
                } ?: listOf()

                itemList = data as MutableList<ParseObject>

                coursesListAdapter.setItemList(itemList)
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

    companion object {
        @JvmStatic
        fun newInstance() = CoursesFragment()
    }
}





