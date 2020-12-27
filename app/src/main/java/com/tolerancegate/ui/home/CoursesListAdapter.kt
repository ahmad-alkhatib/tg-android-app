package com.tolerancegate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.uicomponents.BaseViewHolder
import app.uicomponents.extensions.*
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_courses_list.view.*
import java.util.*
import javax.inject.Inject

class CoursesListAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolder>() {

    private var listOriginal: MutableList<ParseObject> = mutableListOf()
    private var listFiltered: MutableList<ParseObject> = mutableListOf()
    private var onItemClick: (ParseObject) -> Unit = {}
    private var onInfoClick: (ParseObject) -> Unit = {}
    private var canShowStatus = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.adapter_courses_list, parent, false))
    }

    fun setItemList(listOriginal: List<ParseObject>) {
        this.listOriginal = listOriginal.toMutableList()
        this.listFiltered = listOriginal.toMutableList()
    }

    override fun getItemCount(): Int {
        return listFiltered.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindItemViews(listFiltered[position])
    }

    fun addActionForItemClick(onItemClick: (ParseObject) -> Unit) {
        this.onItemClick = onItemClick;
    }

    fun addActionForInfoClick(onInfoClick: (ParseObject) -> Unit) {
        this.onInfoClick = onInfoClick;
    }

    fun filterList(key: String) {
        listFiltered = if (key.isEmpty()) listOriginal else listOriginal.filter {
            it.getStr("title").contains(key, true)
        }.toMutableList()

        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bindItemViews(item: ParseObject) {
            itemView.itemTv.text = item.getStr("title")
            ImageLoaderUtils.loadImageFromServerCrop(
                item.getImageUrl(),
                itemView.itemImage,
                R.drawable.image_placeholder_gray
            )
            if (canShowStatus) {
                itemView.statusView.show()
                itemView.statusView.setActive(item.getBool("isActive"))
                itemView.infoBtn.show()
                itemView.infoBtn.setOnClickListener {
                    onInfoClick(item)
                }
            }
        }
    }

    fun removeItem(objectID: String) {
        listOriginal.removeAll {
            it.objectId == objectID
        }
        listFiltered.removeAll {
            it.objectId == objectID
        }
        notifyDataSetChanged()
    }

    fun canShowStatusView(canShow: Boolean) {
        this.canShowStatus = canShow
    }
}