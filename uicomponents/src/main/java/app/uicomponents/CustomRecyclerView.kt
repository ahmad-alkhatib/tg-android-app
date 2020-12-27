package app.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import app.uicomponents.extensions.remove
import app.uicomponents.extensions.show
import kotlinx.android.synthetic.main.custom_action_view.view.*
import kotlinx.android.synthetic.main.custom_action_view.view.rootLayout
import kotlinx.android.synthetic.main.custom_recycler_view.view.*

class CustomRecyclerView @JvmOverloads constructor(
    context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var defaultAdapter: DefaultAdapter = DefaultAdapter()
    lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private var text: String = ""
    private var titleText: String = ""
    private var actionText: String = ""
    private var userPlaceholder: Boolean = false

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.custom_recycler_view, this)
        getAttributes(attrs)

        if (text.isNotEmpty()) {
            textTv.show()
            textTv.text = text
        }
        if (actionText.isNotEmpty()) {
            actionBtn.show()
            actionBtn.text = actionText
        }
        titleTv.text = titleText
        showEmptyData()
        placeholderLayout.setOnClickListener {}
    }

    private fun getAttributes(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomRecyclerView, 0, 0)
        try {
            text = a.getString(R.styleable.CustomRecyclerView_text) ?: ""
            actionText = a.getString(R.styleable.CustomRecyclerView_actionText) ?: ""
            titleText = a.getString(R.styleable.CustomRecyclerView_title) ?: ""
            userPlaceholder = a.getBoolean(R.styleable.CustomRecyclerView_userPlaceholder, false)
        } finally {
            a.recycle()
        }
    }

    fun addActionForDefaultBtn(actionMethod: () -> Unit) {
        actionBtn.setOnClickListener {
            actionMethod()
        }
    }

    fun addActionForViewAll(actionMethod: () -> Unit) {
        viewAllBtn.setOnClickListener { actionMethod() }
    }

    fun showEmptyData() {
        defaultAdapter.showUserPlaceHolder(userPlaceholder)
        recyclerView.adapter = defaultAdapter
        placeholderLayout.show()
        viewAllBtn.remove()
    }

    fun setListAdapter(listAdapter: RecyclerView.Adapter<BaseViewHolder>) {
        recyclerView.adapter = listAdapter
        placeholderLayout.remove()
        viewAllBtn.show()
    }
}
