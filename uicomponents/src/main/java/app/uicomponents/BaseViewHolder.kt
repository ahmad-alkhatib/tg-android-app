package app.uicomponents

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import app.uicomponents.extensions.getImageUrl
import app.uicomponents.extensions.getStr
import com.parse.ParseObject

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    abstract fun bindItemViews(item: ParseObject)
}