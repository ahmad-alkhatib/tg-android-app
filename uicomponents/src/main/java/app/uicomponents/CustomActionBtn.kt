package app.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import kotlinx.android.synthetic.main.custom_action_btn.view.*

class CustomActionBtn @JvmOverloads constructor(
    context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var active: Boolean = true

    @DrawableRes
    private var iconRes: Int = R.drawable.icon_profile_email

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.custom_action_btn, this)
        getAttributes(attrs)

        icon.setImageResource(iconRes)
        setActive(active)
    }

    private fun getAttributes(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomActionBtn, 0, 0)
        try {
            iconRes = a.getResourceId(R.styleable.CustomActionBtn_icon, R.drawable.icon_profile_email)
            active = a.getBoolean(R.styleable.CustomActionView_hideIcon, true)
        } finally {
            a.recycle()
        }
    }

    fun setActive(active: Boolean) {
        if (active) {
            rootLayout.alpha = 1.0f
        } else {
            rootLayout.alpha = 0.5f
            rootLayout.setOnClickListener(null)
        }
    }

    fun addActionMethod(actionMethod: (CustomActionBtn) -> Unit) {
        rootLayout.setOnClickListener { actionMethod(this) }
    }
}
