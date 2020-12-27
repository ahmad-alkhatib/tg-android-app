package com.tolerancegate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import app.apirequest.parse.ClassName
import app.apirequest.parse.Property.FULL_NAME
import app.uicomponents.BaseViewHolderWithPosition
import app.uicomponents.extensions.getNameLocale
import app.uicomponents.extensions.getStr
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_institution_category.view.*
import javax.inject.Inject

class InstitutionCategoryAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolderWithPosition>() {

    private var itemList: MutableList<ParseObject> = mutableListOf()
    private var onItemClick: (ParseObject) -> Unit = {}
    private var selectedIndex = 0
    private var defaultText = "All"
    var clickable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.adapter_institution_category, parent, false))
    }

    fun setItemList(itemList: MutableList<ParseObject>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        this.itemList.add(0, ParseObject(ClassName.NONE))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolderWithPosition, position: Int) {
        holder.bindItemViews(itemList[position], position)
    }

    fun addActionForItemClick(onItemClick: (ParseObject) -> Unit) {
        this.onItemClick = onItemClick
    }

    fun setDefaultText(text: String) {
        this.defaultText = text
    }

    fun getSelectedCategory(): ParseObject? {
        return if (selectedIndex == 0) null else itemList[selectedIndex]
    }

    inner class ViewHolder(itemView: View) : BaseViewHolderWithPosition(itemView) {
        override fun bindItemViews(item: ParseObject, position: Int) {
            itemView.categoryTv.text = if (position == 0) defaultText else item.getNameLocale()
            itemView.categoryTv.isSelected = position == selectedIndex
            itemView.setOnClickListener {
                if (clickable) {
                    selectedIndex = position
                    notifyDataSetChanged()
                    onItemClick(item)
                }
            }
        }
    }
}