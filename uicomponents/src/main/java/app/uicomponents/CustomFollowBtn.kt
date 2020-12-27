package app.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.custom_follow_btn.view.*

class CustomFollowBtn @JvmOverloads constructor(
    context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var active: Boolean = true
    private var following: Boolean = false

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.custom_follow_btn, this)
        getAttributes(attrs)

        setFollowing(following)
        setActive(active)
    }

    private fun getAttributes(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomFollowBtn, 0, 0)
        try {
            active = a.getBoolean(R.styleable.CustomFollowBtn_active, true)
            following = a.getBoolean(R.styleable.CustomFollowBtn_following, false)
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

    fun setFollowing(following: Boolean) {
        this.following = following
        if (following) {
            followUnFollowBtn.text = context.getString(R.string.following)
            followUnFollowBtn.setBackgroundResource(R.drawable.button_green_rounded_bg)
        } else {
            followUnFollowBtn.text = context.getString(R.string.follow)
            followUnFollowBtn.setBackgroundResource(R.drawable.button_primary_bg)
        }
    }

    fun addActionMethod(actionMethod: (Boolean) -> Unit) {
        rootLayout.setOnClickListener { actionMethod(following) }
    }
}
