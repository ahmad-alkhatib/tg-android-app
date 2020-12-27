package com.tolerancegate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.uicomponents.extensions.getImageUrl
import app.uicomponents.extensions.getNameLocale
import app.uicomponents.extensions.getPTFirstSubject
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_institution.view.itemImage
import kotlinx.android.synthetic.main.adapter_institution.view.itemTv
import kotlinx.android.synthetic.main.adapter_pt_user.view.*
import javax.inject.Inject

class PrivateTutoringAdapter @Inject constructor() :
    RecyclerView.Adapter<PrivateTutoringAdapter.StateListViewHolder>() {

    private var itemList: List<ParseObject> = listOf()
    private var isStudent = false
    private var onItemClick: (ParseObject) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StateListViewHolder(inflater.inflate(R.layout.adapter_pt_user, parent, false))
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
            ImageLoaderUtils.loadCircleImage(
                item.getImageUrl(), itemView.itemImage, R.drawable.image_placeholder_user_gray
            )
            if (isStudent) {
                itemView.userTypeTv.text = itemView.context.getString(R.string.seeks_to_learn)
                itemView.itemTv.text = item.getPTFirstSubject("ptSeeksToLearn")
            } else {
                itemView.userTypeTv.text = itemView.context.getString(R.string.teaches)
                itemView.itemTv.text = item.getPTFirstSubject("subjectsTaught")
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    fun addActionForItemClick(onItemClick: (ParseObject) -> Unit) {
        this.onItemClick = onItemClick;
    }

    fun showAsStudents(isStudent: Boolean) {
        this.isStudent = isStudent
    }
}