package com.tolerancegate.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import app.uicomponents.BaseViewHolder
import app.uicomponents.extensions.*
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_courses_list.view.itemImage
import kotlinx.android.synthetic.main.adapter_courses_list.view.itemTv
import kotlinx.android.synthetic.main.adapter_lecture_card_list.view.*
import javax.inject.Inject

class LectureListAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolder>() {

    private var listOriginal: MutableList<ParseObject> = mutableListOf()
    private lateinit var listFiltered: List<ParseObject>
    private var onItemClick: (Int) -> Unit = {}
    private var checkBookmarkStatus: (ImageView, ParseObject) -> Unit = { _, _ -> }
    private var addOrRemoveBookmark: (ImageView, ParseObject) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.adapter_lecture_card_list, parent, false))
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

    fun addCheckBookmarkStatus(checkBookmarkStatus: (ImageView, ParseObject) -> Unit) {
        this.checkBookmarkStatus = checkBookmarkStatus;
    }

    fun addOrRemoveBookmark(addOrRemoveBookmark: (ImageView, ParseObject) -> Unit) {
        this.addOrRemoveBookmark = addOrRemoveBookmark;
    }

    fun filterList(key: String) {
        listFiltered = if (key.isEmpty()) listOriginal else listOriginal.filter {
            it.getStr("title").contains(key, true)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bindItemViews(item: ParseObject) {
            itemView.itemTv.text = item.getStr("title")
            itemView.dateTv.text = item.createdAt.toStr()
            ImageLoaderUtils.loadImageFromServerCrop(
                item.getImageUrl(),
                itemView.itemImage,
                R.drawable.image_placeholder_gray
            )
            checkBookmarkStatus(itemView.bookmarkIcon, item)
            itemView.bookmarkIcon.setOnClickListener {
                addOrRemoveBookmark(itemView.bookmarkIcon, item)
            }
        }
    }
}