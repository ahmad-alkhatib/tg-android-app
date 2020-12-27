package com.tolerancegate.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.apirequest.parse.Property.FULL_NAME
import app.uicomponents.BaseViewHolder
import app.uicomponents.extensions.getImageUrl
import app.uicomponents.extensions.getObject
import app.uicomponents.extensions.getStr
import app.utilities.ImageLoaderUtils
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.parse.ParseObject
import com.parse.ParseUser
import com.tolerancegate.R
import kotlinx.android.synthetic.main.adapter_conversation_list.view.*
import javax.inject.Inject

class ConversationListAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolder>() {

    private var itemList: MutableList<ParseObject> = mutableListOf()
    private var onItemClick: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.adapter_conversation_list, parent, false))
    }

    fun setItemList(itemList: List<ParseObject>) {
        this.itemList = itemList.toMutableList()
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
            setUserDetails(item)
            setLastMessageDetails(item)
        }

        private fun setUserDetails(item: ParseObject) {

            var otherUser = item.getObject("user1")
            if (otherUser?.objectId ?: "" == ParseUser.getCurrentUser().objectId) {
                otherUser = item.getObject("user2")
            }

            otherUser?.let {
                itemView.itemTitleTv.text = it.getStr(FULL_NAME)
                ImageLoaderUtils.loadCircleImage(
                    it.getImageUrl(), itemView.itemImage, R.drawable.image_placeholder_user_gray
                )
            }
        }

        private fun setLastMessageDetails(item: ParseObject) {
            item.getObject("lastMessage")?.let {
                itemView.itemDescriptionTv.text = it.getStr("message")
                var date = it.createdAt
                itemView.dateTv.text = TimeAgo.using(date.time)
            }
        }
    }
}