package com.tolerancegate.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import app.apirequest.parse.Property
import app.apirequest.parse.Property.HAS_CITY
import app.core.base.BaseDialogFragment
import app.core.custom.CustomEditText
import app.core.listeners.SingleLiveEvent
import app.uicomponents.extensions.getBool
import app.uicomponents.extensions.isVisible
import app.uicomponents.extensions.show
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.dialog_item_picker.headerView
import kotlinx.android.synthetic.main.dialog_list_filter.*
import kotlinx.android.synthetic.main.dialog_list_filter.editCity
import kotlinx.android.synthetic.main.dialog_list_filter.editCountry
import kotlinx.android.synthetic.main.dialog_list_filter.editSubject


class DialogListFilters : BaseDialogFragment() {

    private var filterCity: SingleLiveEvent<ParseObject> = SingleLiveEvent()
    private var filterCountry: SingleLiveEvent<ParseObject> = SingleLiveEvent()
    private var filterSubjectList: SingleLiveEvent<List<ParseObject>> = SingleLiveEvent()
    private var filterListOrder: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private var filterUpdated: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun setFilterListOrder(filterListOrder: SingleLiveEvent<Boolean>) {
        this.filterListOrder = filterListOrder
    }

    fun setFilterCity(filterCity: SingleLiveEvent<ParseObject>) {
        this.filterCity = filterCity
    }

    fun setFilterCountry(filterCountry: SingleLiveEvent<ParseObject>) {
        this.filterCountry = filterCountry
    }

    fun setFilterSubjectList(filterSubjectList: SingleLiveEvent<List<ParseObject>>) {
        this.filterSubjectList = filterSubjectList
    }

    fun setFilterUpdatedEvent(filterUpdated: SingleLiveEvent<Boolean>) {
        this.filterUpdated = filterUpdated
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getTheme(): Int {
        return R.style.StateListDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        return inflater.inflate(R.layout.dialog_list_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView.addActionForLeftIcon {
            dismiss()
        }
        editCountry.addClickAction { showCountryPickerDialog(it) }
        editCity.addClickAction {
            if (editCountry.getText().isEmpty()) {
                showMessageDialog(R.string.search_filters, R.string.please_select_a_country_before_selecting_the_city)
            } else {
                showCityPickerDialog(it)
            }
        }
        editSubject.addClickAction { showPickerDialog(it) }
        clearBtn.setOnClickListener { clearFilters() }
        searchBtn.setOnClickListener {
            if (editCity.isVisible()) {
                filterCity.value = if (editCity.getSelectedObjects().isNotEmpty()) editCity.getSingleObject() else null
            } else {
                filterCity.value = null
            }
            filterCountry.value =
                if (editCountry.getSelectedObjects().isNotEmpty()) editCountry.getSingleObject() else null
            filterSubjectList.value = editSubject.getSelectedObjects()
            if (segmentGroup.isVisible()) {
                filterListOrder.value = newToOldRadioBtn.isChecked
            } else {
                filterListOrder.value = null
            }
            filterUpdated.postValue(true)
            dismiss()
        }
        setFilterView()
    }

    private fun clearFilters() {
        editCountry.setItems(mutableListOf())
        editCity.setItems(mutableListOf())
        editSubject.setItems(mutableListOf())
        newToOldRadioBtn.isChecked = true
        filterCity.value = null
        filterCountry.value = null
        filterListOrder.value = null
        filterSubjectList.value = mutableListOf()
        filterUpdated.postValue(true)
    }

    private fun showCityPickerDialog(customEditText: CustomEditText) {
        activity?.let {
            var pickerDialogFragment = DialogItemPicker.instance();
            pickerDialogFragment.setCustomEditText(customEditText)
            pickerDialogFragment.setCityFlag(true)
            pickerDialogFragment.setDependentEditText(editCountry)
            pickerDialogFragment.show(it.supportFragmentManager, "PICKER_DIALOG")
        }
    }

    private fun showPickerDialog(customEditText: CustomEditText) {
        activity?.let {
            var pickerDialogFragment = DialogItemPicker.instance();
            pickerDialogFragment.setCustomEditText(customEditText)
            pickerDialogFragment.show(it.supportFragmentManager, "PICKER_DIALOG")
        }
    }

    private fun showCountryPickerDialog(customEditText: CustomEditText) {
        activity?.let { c ->
            var pickerDialogFragment = DialogItemPicker.instance();
            pickerDialogFragment.setCustomEditText(customEditText)
            pickerDialogFragment.setCountryFlag(true)
            pickerDialogFragment.addActionForItemSelection { ocCountrySelected(it) }
            pickerDialogFragment.show(c.supportFragmentManager, "PICKER_DIALOG")
        }
    }

    private fun ocCountrySelected(parseObject: ParseObject) {
        editCity.removeSelection()

        if (parseObject.getBool(HAS_CITY)) {
            editCity.visibility = VISIBLE
        } else {
            editCity.visibility = View.GONE
        }
    }

    private fun setFilterView() {
        when (arguments?.getString(FILTER_TYPE, FILTER_ALL)) {
            FILTER_ALL -> {
                setCountryCity()
                setSubjectList()
                setListOrder()
            }
            FILTER_COUNTRY_SUBJECT -> {
                setCountryCity()
                setSubjectList()
            }
            FILTER_SUBJECT -> {
                setSubjectList()
            }
            FILTER_COUNTRY -> {
                setCountryCity()
            }
        }
    }

    private fun setSubjectList() {
        editSubject.show()
        editSubject.setItems(filterSubjectList.value?.toMutableList() ?: mutableListOf())
    }

    private fun setListOrder() {
        segmentGroup.show()
        var accending = filterListOrder.value ?: true
        if (accending) {
            newToOldRadioBtn.isChecked = accending
        } else {
            oldToNewRadioBtn.isChecked = accending
        }
    }

    private fun setCountryCity() {
        editCountry.show()
        if (filterCountry.value != null) {
            editCountry.setItems(mutableListOf(filterCountry.value!!))
        }
        editCity.show()
        if (filterCity.value != null) {
            editCity.setItems(mutableListOf(filterCity.value!!))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(filterType: String) = DialogListFilters().apply {
            arguments = Bundle().apply {
                putString(FILTER_TYPE, filterType)
            }
        }

        const val FILTER_COUNTRY = "filter_country"
        const val FILTER_SUBJECT = "filter_subject"
        const val FILTER_COUNTRY_SUBJECT = "filter_country_subject"
        const val FILTER_ALL = "filter_all"
        const val FILTER_TYPE = "filter_KEY"
    }
}