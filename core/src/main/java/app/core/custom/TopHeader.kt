package app.core.custom

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import app.core.R
import app.uicomponents.extensions.remove
import app.uicomponents.extensions.show
import kotlinx.android.synthetic.main.top_header.view.*

open class TopHeader @JvmOverloads constructor(
    context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private var title: String? = ""
    private var transparentBg: Boolean = false
    private var showIconLeft: Boolean = false
    private var showIconRight: Boolean = false
    private var hideStatusBar: Boolean = false

    @DrawableRes
    private var startIcon: Int = R.drawable.nav_arrow_back

    @DrawableRes
    private var endIcon: Int = R.drawable.nav_setting_dots

    private var tintList: ColorStateList? = null;

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.top_header, this)
        getAttributes(attrs)
        setIcons()
        setTitle(title.orEmpty())
        if (hideStatusBar) {
            statusBarView.visibility = GONE
        }
    }

    private fun getAttributes(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TopHeader, 0, 0
        )
        try {
            title = a.getString(R.styleable.TopHeader_title)
            transparentBg = a.getBoolean(R.styleable.TopHeader_transparent, false)
            showIconLeft = a.getBoolean(R.styleable.TopHeader_showLeftIcon, false)
            showIconRight = a.getBoolean(R.styleable.TopHeader_showRightIcon, false)
            hideStatusBar = a.getBoolean(R.styleable.TopHeader_hideStatusBar, false)
            startIcon = a.getResourceId(R.styleable.TopHeader_leftIcon, R.drawable.nav_arrow_back)
            endIcon = a.getResourceId(R.styleable.TopHeader_rightIcon, R.drawable.nav_setting_dots)
            tintList = a.getColorStateList(R.styleable.TopHeader_android_tint)
        } finally {
            a.recycle()
        }
    }

    fun setTitle(text: String) {
        titleTextView.text = text
    }

    fun getTitle() = titleTextView.text.toString()

    private fun setIcons() {
        if (showIconLeft) {
            leftIcon.visibility = VISIBLE
            leftIcon.setImageResource(startIcon)
            leftIcon.imageTintList = tintList
        }
        if (showIconRight) {
            rightIcon.visibility = VISIBLE
            rightIcon.setImageResource(endIcon)
            rightIcon.imageTintList = tintList
        }
        if (transparentBg) {
            rootLayout.setBackgroundResource(R.color.transparent)
        }
    }

    fun addActionForRightIcon(clickAction: () -> Unit) {
        rightIcon.setOnClickListener { clickAction() }
    }

    fun addActionForLeftIcon(clickAction: () -> Unit) {
        leftIcon.setOnClickListener { clickAction() }
    }

    fun hideStatusBar() = statusBarView.remove()

    fun showStatusBar() = statusBarView.show()

    fun updateStatusBarHeight(height: Int) {
        var param = LayoutParams(LayoutParams.MATCH_PARENT, height)
        statusBarView.layoutParams = param
    }
}