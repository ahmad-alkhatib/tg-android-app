package com.tolerancegate.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.core.custom.CustomEditText
import app.uicomponents.extensions.getNameInSmall
import app.uicomponents.extensions.getNameLocale
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_item_picker.view.*
import kotlinx.android.synthetic.main.adapter_item_picker.view.rootLayout
import java.util.*
import javax.inject.Inject

class PickerItemAdapter @Inject constructor() : RecyclerView.Adapter<PickerItemAdapter.StateListViewHolder>() {

    private var listOriginal: MutableList<ParseObject> = mutableListOf()
    private lateinit var listFiltered: List<ParseObject>
    private lateinit var customEditText: CustomEditText
    private var appLanguage: String = "En"
    private var onItemClick: (ParseObject) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StateListViewHolder(inflater.inflate(R.layout.adapter_item_picker, parent, false))
    }

    fun setPickerList(listOriginal: List<ParseObject>) {
        this.listOriginal = listOriginal.toMutableList()
        this.listFiltered = listOriginal
    }

    override fun getItemCount(): Int {
        return listFiltered.size
    }

    override fun onBindViewHolder(holder: StateListViewHolder, position: Int) {
        holder.bindItemViews(listFiltered[position])
    }

    inner class StateListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItemViews(parseObject: ParseObject) {
            itemView.textView.text = parseObject.getNameLocale()
            if(customEditText.isItemContains(parseObject))
            {
                itemView.tickImageView.visibility = VISIBLE
            }
            else{
                itemView.tickImageView.visibility = GONE
            }
            itemView.rootLayout.setOnClickListener {
                onItemClick(parseObject)
                customEditText.updateItem(parseObject)
                notifyDataSetChanged()
            }
        }
    }

    fun filterList(key: String) {
        listFiltered = if (key.isEmpty()) listOriginal else listOriginal.filter {
            it.getNameLocale().contains(key, true)
        }
        notifyDataSetChanged()
    }

    fun setCustomEditText(customEditText: CustomEditText) {
        this.customEditText = customEditText
    }

    fun addActionForItemClick(onItemClick: (ParseObject) -> Unit) {
        this.onItemClick = onItemClick;
    }
}