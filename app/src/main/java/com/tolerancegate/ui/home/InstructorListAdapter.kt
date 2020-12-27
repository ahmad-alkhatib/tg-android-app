package com.tolerancegate.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.apirequest.parse.Property.FULL_NAME
import app.uicomponents.BaseViewHolder
import app.uicomponents.extensions.getImageUrl
import app.uicomponents.extensions.getPTFirstSubject
import app.uicomponents.extensions.getStr
import app.utilities.Enums.InstructorSearchType
import app.utilities.Enums.InstructorSearchType.*
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_institution.view.*
import kotlinx.android.synthetic.main.adapter_instructor_list.view.*
import kotlinx.android.synthetic.main.adapter_instructor_list.view.itemImage
import javax.inject.Inject

class InstructorListAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolder>() {

    private var listOriginal: MutableList<ParseObject> = mutableListOf()
    private lateinit var listFiltered: List<ParseObject>
    private var onItemClick: (ParseObject) -> Unit = {}
    private var itemType: InstructorSearchType = HOME

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.adapter_instructor_list, parent, false))
    }

    fun setItemList(listOriginal: List<ParseObject>) {
        this.listOriginal = listOriginal.toMutableList()
        this.listFiltered = listOriginal
    }

    fun setItemType(itemType: InstructorSearchType) {
        this.itemType = itemType
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

    fun filterList(key: String) {
        listFiltered = if (key.isEmpty()) listOriginal else listOriginal.filter {
            it.getStr(FULL_NAME).contains(key, true)
        }
        notifyDataSetChanged()
    }

    private fun getDescriptionText(item: ParseObject, context: Context): String {
        return if (itemType == FOLLOWERS) {
            when (item.getStr("accountType")) {
                "instructor" -> context.getString(R.string.instructor)
                "student" -> context.getString(R.string.student)
                "institute" -> context.getString(R.string.institution)
                else -> ""
            }
        } else if (itemType == PT_STUDENT) {
            return "${context.getString(R.string.seeks_to_learn)}: ${item.getPTFirstSubject("subjectsTaught")}"
        } else if (itemType == PT_TUTOR) {
            return "${context.getString(R.string.teaches)}: ${item.getPTFirstSubject("ptSeeksToLearn")}"
        } else {
            item.getStr("specialization")
        }
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bindItemViews(item: ParseObject) {
            itemView.itemTitleTv.text = item.getStr(FULL_NAME)
            itemView.itemDescriptionTv.text = getDescriptionText(item, itemView.context)
            ImageLoaderUtils.loadCircleImage(
                item.getImageUrl(), itemView.itemImage, R.drawable.image_placeholder_user_gray
            )
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}