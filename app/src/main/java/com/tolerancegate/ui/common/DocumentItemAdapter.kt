package com.tolerancegate.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.uicomponents.extensions.getStrLocale
import com.parse.ParseObject
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_item_document.view.*
import javax.inject.Inject

class DocumentItemAdapter @Inject constructor() : RecyclerView.Adapter<DocumentItemAdapter.StateListViewHolder>() {

    private var documentList: MutableList<Documents> = mutableListOf()
    private var appLanguage: String = "En"
    private var onItemClick: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StateListViewHolder(inflater.inflate(R.layout.adapter_item_document, parent, false))
    }

    fun setItemList(documentList: MutableList<Documents>) {
        this.documentList = documentList
    }

    override fun getItemCount(): Int {
        return documentList.size
    }

    override fun onBindViewHolder(holder: StateListViewHolder, position: Int) {
        holder.bindItemViews(documentList[position], position)
    }

    inner class StateListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItemViews(documentObj: Documents, position: Int) {
            itemView.icon.setOnClickListener {
                onItemClick(position)
            }
            if (documentObj.isSelected) {
                itemView.icon.setImageResource(R.drawable.icon_cross)
                itemView.rootLayout.isSelected = true
                itemView.textView.text = documentObj.filename
            } else {
                itemView.icon.setImageResource(R.drawable.icon_upload)
                itemView.rootLayout.isSelected = false
                itemView.textView.text = documentObj.fileObj.getStrLocale("filename")
            }
        }
    }

    fun addActionForItemClick(onItemClick: (Int) -> Unit) {
        this.onItemClick = onItemClick;
    }
}