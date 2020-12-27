package app.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.custom_active_view.view.*
import kotlinx.android.synthetic.main.custom_follow_btn.view.*
import kotlinx.android.synthetic.main.custom_follow_btn.view.rootLayout

class CustomStatusView @JvmOverloads constructor(
    context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var active: Boolean = true

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.custom_active_view, this)
        getAttributes(attrs)

        setActive(active)
    }

    private fun getAttributes(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomStatusView, 0, 0)
        try {
            active = a.getBoolean(R.styleable.CustomStatusView_active, true)
        } finally {
            a.recycle()
        }
    }

    fun setActive(active: Boolean) {
        itemView.isSelected = active
        itemView.text = context.getString(if (active) R.string.active else R.string.inactive)
    }
}
