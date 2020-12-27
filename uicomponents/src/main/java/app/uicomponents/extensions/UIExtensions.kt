package app.uicomponents.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout

fun View.show(): View {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    return this
}

fun View.hide(): View {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
    return this
}

fun View.remove(): View {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
    return this
}

fun View.isVisible(): Boolean {
    if (this.visibility == View.VISIBLE) {
        return true
    }
    return false
}

fun View.hideKeyboard(): Boolean {
    return try {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
        false
    }
}