package com.tolerancegate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.uicomponents.BaseViewHolder
import app.uicomponents.extensions.getImageUrl
import app.uicomponents.extensions.getStr
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_institution.view.*
import javax.inject.Inject

class CoursesAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolder>() {

    private var itemList: List<ParseObject> = listOf()
    private var onItemClick: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.adapter_courses, parent, false))
    }

    fun setItemList(itemList: List<ParseObject>) {
        this.itemList = itemList
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindItemViews(itemList[position])
    }

    fun addActionForItemClick(onItemClick: (Int) -> Unit) {
        this.onItemClick = onItemClick;
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bindItemViews(item: ParseObject) {
            itemView.itemTv.text = item.getStr("title")
            ImageLoaderUtils.loadImageFromServerCrop(
                item.getImageUrl(),
                itemView.itemImage,
                R.drawable.image_placeholder_gray
            )
        }
    }
}