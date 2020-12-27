package app.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.custom_action_view.view.*

class CustomActionView @JvmOverloads constructor(
    context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var text: String? = ""
    private var url: String? = ""
    private var hideIcon: Boolean = false

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.custom_action_view, this)
        getAttributes(attrs)

        textView.text = text.orEmpty()

        if (hideIcon) {
            icon.visibility = GONE
        }
    }

    private fun getAttributes(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomActionView, 0, 0)
        try {
            text = a.getString(R.styleable.CustomActionView_text)
            url = a.getString(R.styleable.CustomActionView_url)
            hideIcon = a.getBoolean(R.styleable.CustomActionView_hideIcon, false)
        } finally {
            a.recycle()
        }
    }

    fun addActionMethod(actionMethod: (CustomActionView) -> Unit) {
        rootLayout.setOnClickListener { actionMethod(this) }
    }

    fun getUrl() = url.orEmpty()

    fun getText() = textView.text.toString()
}
