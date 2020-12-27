package com.tolerancegate.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.core.base.BaseDialogFragment
import app.core.custom.CustomEditText
import com.parse.ParseObject
import com.parse.ParseQuery
import com.tolerancegate.R
import kotlinx.android.synthetic.main.dialog_item_picker.*
import javax.inject.Inject


class DialogItemPicker : BaseDialogFragment() {

    private var pickerList: MutableList<ParseObject> = mutableListOf()
    private lateinit var customEditText: CustomEditText
    private lateinit var dependentEditText: CustomEditText
    private var isCountry: Boolean = false
    private var isCity: Boolean = false
    private var onItemSelected: (ParseObject) -> Unit = {}

    @Inject
    lateinit var pickerItemAdapter: PickerItemAdapter

    override fun getTheme(): Int {
        return R.style.StateListDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        return inflater.inflate(R.layout.dialog_item_picker, container, false)
    }

    private fun setItemsForList() {
        pickerItemAdapter.setPickerList(pickerList)
        pickerItemAdapter.setCustomEditText(customEditText)
        if (isCountry) {
            pickerItemAdapter.addActionForItemClick { onItemSelected(it) }
        }
        pickerListRecyclerView.adapter = pickerItemAdapter

        searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                pickerItemAdapter.filterList(s.toString())
            }
        })
    }

    private fun showProgressView() {
        refreshLayout.isEnabled = true
        refreshLayout.isRefreshing = true
    }

    private fun hideProgressView() {
        refreshLayout.isEnabled = false
        refreshLayout.isRefreshing = false
    }

    private fun callApiToGetData() {
        showProgressView()
        var query = ParseQuery.getQuery<ParseObject>(customEditText.getClassName())
        query.whereEqualTo("isActive", true)
        if (isCity) {
            query.whereEqualTo("country", dependentEditText.getSingleObject())
        }
        query.findInBackground { objects, e ->
            if (refreshLayout != null) {
                hideProgressView()
                if (e == null) {
                    pickerList = objects
                    setItemsForList()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        headerView.setTitle(customEditText.getScreenTitle())
        headerView.addActionForLeftIcon {
            dismiss()
        }
        callApiToGetData()
    }

    fun setCustomEditText(customEditText: CustomEditText) {
        this.customEditText = customEditText
    }

    fun setDependentEditText(dependentEditText: CustomEditText) {
        this.dependentEditText = dependentEditText
    }

    fun setCountryFlag(isCountry: Boolean) {
        this.isCountry = isCountry
    }

    fun addActionForItemSelection(onItemSelected: (ParseObject) -> Unit) {
        this.onItemSelected = onItemSelected;
    }

    fun setCityFlag(isCity: Boolean) {
        this.isCity = isCity
    }

    companion object {
        @JvmStatic
        fun instance() = DialogItemPicker()
    }
}