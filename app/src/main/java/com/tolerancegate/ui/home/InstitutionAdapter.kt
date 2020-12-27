package com.tolerancegate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.apirequest.parse.Property.FULL_NAME
import app.uicomponents.extensions.getImageUrl
import app.uicomponents.extensions.getStr
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_institution.view.*
import javax.inject.Inject

class InstitutionAdapter @Inject constructor() : RecyclerView.Adapter<InstitutionAdapter.StateListViewHolder>() {

    private var itemList: List<ParseObject> = listOf()
    private var onItemClick: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StateListViewHolder(inflater.inflate(R.layout.adapter_institution, parent, false))
    }

    fun setItemList(itemList: List<ParseObject>) {
        this.itemList = itemList
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: StateListViewHolder, position: Int) {
        holder.bindItemViews(itemList[position])
    }

    inner class StateListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItemViews(item: ParseObject) {
            itemView.itemTv.text = item.getStr(FULL_NAME)
            ImageLoaderUtils.loadImageFromServerCrop(item.getImageUrl(), itemView.itemImage, R.drawable.image_placeholder_gray)
        }
    }

    fun addActionForItemClick(onItemClick: (Int) -> Unit) {
        this.onItemClick = onItemClick;
    }
}