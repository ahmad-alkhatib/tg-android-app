package app.core.custom

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import app.apirequest.parse.ClassName.NONE
import app.core.R
import app.uicomponents.extensions.getNameLocale
import app.uicomponents.extensions.isValidEmail
import app.uicomponents.extensions.isValidPassword
import app.uicomponents.extensions.remove
import com.parse.ParseObject
import kotlinx.android.synthetic.main.custom_edittext.view.*

class CustomEditText @JvmOverloads constructor(
    context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var textInput: String? = ""
    private var textHint: String? = ""
    private var className: String? = ""
    private var screenTitle: String? = ""
    private var textErrorMessage: String? = ""
    private var dropdownPicker: Boolean = false
    private var roundedBackground: Boolean = false
    private var multiline: Boolean = false
    private var multiSelection: Boolean = false
    private var showCurrency: Boolean = false
    private var optional: Boolean = false
    private var hideStartIcon: Boolean = false
    private var maxLength: Int = 0
    lateinit var inputEditText: EditText
    lateinit var inputTextField: TextView
    lateinit var currencyTextView: TextView
    private var selectedItems: MutableList<ParseObject> = mutableListOf()

    @DrawableRes
    private var startIcon: Int = R.drawable.icon_name

    @DrawableRes
    private var endIcon: Int = R.drawable.icon_arrow_down
    private var inputType = EditorInfo.TYPE_NULL;
    private var imeOptions = EditorInfo.IME_NULL;

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val rootView = View.inflate(context, R.layout.custom_edittext, this)
        inputEditText = rootView.findViewWithTag<EditText>("inputEditText")
        inputTextField = rootView.findViewWithTag<TextView>("inputTextField")
        currencyTextView = rootView.findViewWithTag<TextView>("currencyTextView")
        getAttributes(attrs)
        setViews()
        setIcons()
        setText(textInput.orEmpty())
        setHintText(textHint.orEmpty())
        if (maxLength > 0) {
            inputEditText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        }
    }

    private fun setViews() {
        if (multiline) {
            setMultilineView()
        } else if (dropdownPicker) {
            setDropdownView()
        } else {
            setNormalEditText()
        }
        if (showCurrency) {
            currencyTextView.visibility = VISIBLE
        }
        rootLayout.setBackgroundResource(if (roundedBackground) R.drawable.textfield_bg_rounded else R.drawable.textfield_bg)
    }

    private fun setMultilineView() {
        inputEditText.visibility = VISIBLE
        inputTextField.visibility = GONE
        iconEnd.visibility = GONE
        inputEditText.inputType = inputType
        inputEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        inputEditText.maxLines = 5
        inputEditText.filters = arrayOf(*inputEditText.filters, InputFilter.LengthFilter(180))
        inputEditText.gravity = Gravity.TOP or Gravity.START
        inputEditText.isSingleLine = false
        var height = resources.getDimension(R.dimen.editText_multiline_height).toInt()
        var param = LayoutParams(LayoutParams.MATCH_PARENT, height)
        rootLayout.layoutParams = param
        rootLayout.gravity = Gravity.TOP
        var padding = resources.getDimension(R.dimen.general_large).toInt()
        var paddingThin = resources.getDimension(R.dimen.tiny).toInt()
        rootLayout.setPadding(paddingThin, padding, paddingThin, padding)
    }

    private fun setDropdownView() {
        inputEditText.visibility = GONE
        inputTextField.visibility = VISIBLE
        iconEnd.visibility = VISIBLE
    }

    fun setDisableView() {
        dropdownPicker = true
        inputEditText.visibility = GONE
        inputTextField.visibility = VISIBLE
        iconEnd.visibility = GONE
        inputTextField.alpha = 0.4f
    }

    private fun setNormalEditText() {
        inputEditText.visibility = VISIBLE
        inputTextField.visibility = GONE
        iconEnd.visibility = GONE
        inputEditText.inputType = inputType
        inputEditText.imeOptions = imeOptions
    }

    private fun getAttributes(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomEditText, 0, 0
        )
        try {
            textInput = a.getString(R.styleable.CustomEditText_text)
            textHint = a.getString(R.styleable.CustomEditText_hintText)
            textErrorMessage = a.getString(R.styleable.CustomEditText_errorTextMessage)
            className = a.getString(R.styleable.CustomEditText_className)
            screenTitle = a.getString(R.styleable.CustomEditText_screenTitle)
            dropdownPicker = a.getBoolean(R.styleable.CustomEditText_dropdownPicker, false)
            roundedBackground = a.getBoolean(R.styleable.CustomEditText_backgroundRounded, false)
            multiline = a.getBoolean(R.styleable.CustomEditText_multiline, false)
            multiSelection = a.getBoolean(R.styleable.CustomEditText_multiSelection, false)
            showCurrency = a.getBoolean(R.styleable.CustomEditText_showCurrency, false)
            optional = a.getBoolean(R.styleable.CustomEditText_optional, false)
            hideStartIcon = a.getBoolean(R.styleable.CustomEditText_hideStartIcon, false)
            startIcon = a.getResourceId(R.styleable.CustomEditText_startIcon, R.drawable.icon_name)
            endIcon = a.getResourceId(R.styleable.CustomEditText_endIcon, R.drawable.icon_arrow_down)
            inputType = a.getInt(R.styleable.CustomEditText_android_inputType, EditorInfo.TYPE_NULL)
            imeOptions = a.getInt(R.styleable.CustomEditText_android_imeOptions, EditorInfo.IME_NULL)
            maxLength = a.getInt(R.styleable.CustomEditText_android_maxLength, 0)
        } finally {
            a.recycle()
        }
    }

    fun setText(text: String) {
        if (dropdownPicker) inputTextField.text = text else inputEditText.setText(text)
    }

    private fun setIcons() {
        iconStart.setImageResource(startIcon)
        iconEnd.setImageResource(endIcon)
        if (hideStartIcon) {
            iconStart.remove()
        }
    }

    fun setHintText(text: String) {
        if (dropdownPicker) inputTextField.hint = text else inputEditText.hint = text
    }

    fun getText(): String {
        return if (dropdownPicker) inputTextField.text.toString() else inputEditText.text.toString()
    }

    fun getError(): String {
        return textErrorMessage.orEmpty()
    }

    fun addClickAction(clickAction: (CustomEditText) -> Unit) {
        rootLayout.setOnClickListener { clickAction(this) }
    }

    fun isValid(): Boolean {
        if (optional) return true
        if (getText().isEmpty()) return false
        return true
    }

    fun isValidPassword() = (getText().isValidPassword() || optional)

    fun isValidEmail() = (getText().isValidEmail() || optional)

    fun doesPasswordMatch(password: String) = password == getText()

    private fun showSelectedItems() {
        if (selectedItems.isNullOrEmpty()) {
            inputTextField.text = ""
        } else {
            inputTextField.text = selectedItems.joinToString(separator = ", ") { it.getNameLocale() }
        }
    }

    fun updateItem(parseObject: ParseObject) {
        if (isItemContains(parseObject)) {
            removeItem(parseObject)
        } else {
            addItem(parseObject)
        }
    }

    private fun addItem(parseObject: ParseObject) {
        if (multiSelection.not()) {
            selectedItems.clear()
        }
        selectedItems.add(parseObject)
        showSelectedItems()
    }

    fun setItems(items: MutableList<ParseObject>) {
        selectedItems.clear()
        selectedItems.addAll(items)
        showSelectedItems()
    }

    private fun removeItem(parseObject: ParseObject) {
        loop@ for (i in 0 until selectedItems.size) {
            if (selectedItems.get(i).objectId == parseObject.objectId) {
                selectedItems.removeAt(i)
                break@loop
            }
        }
        //TODO ARK this is very useful method but only allow after 24
        //  selectedItems.removeIf { it.objectId == parseObject.objectId }
        showSelectedItems()
    }

    fun removeSelection() {
        selectedItems.clear()
        showSelectedItems()
    }

    fun isItemContains(parseObject: ParseObject) = selectedItems.any { it.objectId == parseObject.objectId }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putString("inputEditText", inputEditText.text.toString()) // ... save stuff
        bundle.putString("inputTextField", inputTextField.text.toString()) // ... save stuff
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle: Bundle = state
            if (bundle.getString("inputEditText").isNullOrEmpty().not())
                inputEditText.setText(bundle.getString("inputEditText"))
            if (bundle.getString("inputTextField").isNullOrEmpty().not())
                inputTextField.setText(bundle.getString("inputTextField"))
            super.onRestoreInstanceState(bundle.getParcelable("superState")!!)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun getSingleObject() = if (selectedItems.isNotEmpty()) selectedItems[0] else ParseObject(NONE)

    fun getSelectedObjects() = selectedItems

    fun getClassName() = className.orEmpty()

    fun getScreenTitle() = screenTitle.orEmpty()

    fun getCurrency() = currencyTextView.text.toString()

    fun setCurrency(currency: String) {
        currencyTextView.text = currency
    }

    fun setOptional(optional: Boolean) {
        this.optional = optional
    }
}
