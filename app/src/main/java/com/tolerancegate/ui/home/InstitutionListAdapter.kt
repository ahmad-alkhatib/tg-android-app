package com.tolerancegate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.apirequest.parse.Property.FULL_NAME
import app.uicomponents.BaseViewHolder
import app.uicomponents.extensions.getAddress
import app.uicomponents.extensions.getImageUrl
import app.uicomponents.extensions.getStr
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_institution_list.view.*
import javax.inject.Inject

class InstitutionListAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolder>() {

    private var listOriginal: MutableList<ParseObject> = mutableListOf()
    private lateinit var listFiltered: List<ParseObject>
    private var onItemClick: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.adapter_institution_list, parent, false))
    }

    fun setItemList(listOriginal: List<ParseObject>) {
        this.listOriginal = listOriginal.toMutableList()
        this.listFiltered = listOriginal
    }

    override fun getItemCount(): Int {
        return listFiltered.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindItemViews(listFiltered[position])
    }

    fun addActionForItemClick(onItemClick: (Int) -> Unit) {
        this.onItemClick = onItemClick;
    }

    fun filterList(key: String) {
        listFiltered = if (key.isEmpty()) listOriginal else listOriginal.filter {
            it.getStr(FULL_NAME).contains(key, true)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bindItemViews(item: ParseObject) {
            itemView.itemTitleTv.text = item.getStr(FULL_NAME)
            itemView.itemAddressTv.text = item.getAddress()
            ImageLoaderUtils.loadImageFromServerCrop(
                item.getImageUrl(),
                itemView.itemImage,
                R.drawable.image_placeholder_gray
            )
        }
    }
}