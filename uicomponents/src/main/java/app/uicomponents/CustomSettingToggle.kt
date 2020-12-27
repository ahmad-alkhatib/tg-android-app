package app.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.custom_setting_toggle.view.*

class CustomSettingToggle @JvmOverloads constructor(
    context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var text: String? = ""
    var actionMethod: (CustomSettingToggle, Boolean) -> Unit = { view: CustomSettingToggle, status: Boolean -> }

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.custom_setting_toggle, this)

        getAttributes(attrs)

        textView.text = text

        switchButton.setOnCheckedChangeListener { buttonView, isChecked ->
            actionMethod(this, isChecked)
        }
    }

    private fun getAttributes(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomSettingToggle, 0, 0
        )
        try {
            text = a.getString(R.styleable.CustomSettingToggle_text)
        } finally {
            a.recycle()
        }
    }

    fun isItemChecked() = switchButton.isChecked

    fun setItemChecked(status: Boolean) {
        switchButton.isChecked = status
    }

    fun addActionMethod(actionMethod: (CustomSettingToggle, Boolean) -> Unit) {
        this.actionMethod = actionMethod
    }
}
