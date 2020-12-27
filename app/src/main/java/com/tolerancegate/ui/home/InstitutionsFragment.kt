package com.tolerancegate.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import app.apirequest.parse.ClassName.NONE
import app.apirequest.parse.ClassName.USER
import app.core.listeners.SingleLiveEvent
import com.parse.ParseObject
import com.parse.ParseQuery
import com.tolerancegate.R
import com.tolerancegate.ui.common.DialogListFilters
import com.tolerancegate.ui.common.DialogListFilters.Companion.FILTER_COUNTRY
import kotlinx.android.synthetic.main.fragment_institutions.*
import javax.inject.Inject


class InstitutionsFragment : BaseHomeFragment() {

    private var filterCity: SingleLiveEvent<ParseObject> = SingleLiveEvent()
    private var filterCountry: SingleLiveEvent<ParseObject> = SingleLiveEvent()
    private var filterUpdated: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private var itemList: MutableList<ParseObject> = mutableListOf()
    private var categoryList: MutableList<ParseObject> = mutableListOf()

    @Inject
    lateinit var institutionListAdapter: InstitutionListAdapter

    @Inject
    lateinit var institutionCategoryAdapter: InstitutionCategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_institutions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenTitle(R.string.institutions)
        showBackIcon()
        setMenuIcon(R.drawable.nav_filter) {
            showFilterDialog()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                institutionListAdapter.filterList(s.toString())
            }
        })

        if (itemList.isNullOrEmpty()) {
            callApiToGetData()
        }

        if (categoryList.isNullOrEmpty()) {
            callApiToGetCategories()
        }

        institutionCategoryAdapter.setDefaultText(getString(R.string.all))

        institutionListAdapter.setItemList(itemList)
        recyclerView.adapter = institutionListAdapter

        institutionCategoryAdapter.setItemList(categoryList)
        categoryRecyclerView.adapter = institutionCategoryAdapter

        institutionCategoryAdapter.addActionForItemClick {
            clearListAndCallData()
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
        institutionListAdapter.setItemList(listOf())
        recyclerView.adapter = institutionListAdapter
        callApiToGetData()
    }

    private fun callApiToGetData() {
        refreshLayout.isRefreshing = true
        val query = ParseQuery.getQuery<ParseObject>(USER)

        filterCountry.value?.let {
            query.whereEqualTo("country", it)
        }
        filterCity.value?.let {
            query.whereEqualTo("city", it)
        }
        institutionCategoryAdapter.getSelectedCategory()?.let {
            query.whereEqualTo("institutionType", it)
        }

        homeViewModel.findAllInstitutions(query).observe(this, { response ->
            refreshLayout.isRefreshing = false
            if (response.isSuccessful) {
                itemList = response.data as MutableList<ParseObject>
                institutionListAdapter.setItemList(itemList)
                institutionListAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun callApiToGetCategories() {
        homeViewModel.findInstitutionType().observe(this, { response ->
            if (response.isSuccessful) {
                categoryList = response.data as MutableList<ParseObject>
                institutionCategoryAdapter.setItemList(categoryList)
                categoryRecyclerView.adapter = institutionCategoryAdapter
            }
        })
    }

    fun showFilterDialog() {
        activity?.let {
            var filterListDialog = DialogListFilters.newInstance(FILTER_COUNTRY);
            filterListDialog.setFilterCity(filterCity)
            filterListDialog.setFilterCountry(filterCountry)
            filterListDialog.setFilterUpdatedEvent(filterUpdated)
            filterListDialog.show(it.supportFragmentManager, "FILTER_DIALOG")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = InstitutionsFragment()
    }

}





