package com.tolerancegate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.apirequest.parse.Property.FULL_NAME
import app.uicomponents.BaseViewHolder
import app.uicomponents.extensions.getImageUrl
import app.uicomponents.extensions.getStr
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_institution.view.*
import javax.inject.Inject

class InstructorAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolder>() {

    private var itemList: List<ParseObject> = listOf()
    private var onItemClick: (ParseObject) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.adapter_instructor, parent, false))
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

    fun addActionForItemClick(onItemClick: (ParseObject) -> Unit) {
        this.onItemClick = onItemClick;
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bindItemViews(item: ParseObject) {
            itemView.itemTv.text = item.getStr(FULL_NAME)
            ImageLoaderUtils.loadCircleImage(
                item.getImageUrl(), itemView.itemImage, R.drawable.image_placeholder_user_gray
            )
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}