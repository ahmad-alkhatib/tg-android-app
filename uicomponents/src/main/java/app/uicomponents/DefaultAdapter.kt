package app.uicomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import javax.inject.Inject

class DefaultAdapter @Inject constructor() : RecyclerView.Adapter<DefaultAdapter.ViewHolders>() {

    private var isProfile = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolders {
        val inflater = LayoutInflater.from(parent.context)
        if (isProfile)
            return ViewHolders(inflater.inflate(R.layout.adapter_default_profile_item, parent, false))
        else
            return ViewHolders(inflater.inflate(R.layout.adapter_default_item, parent, false))
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolders, position: Int) = Unit

    inner class ViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun showUserPlaceHolder(flag: Boolean) {
        this.isProfile = flag
    }
}