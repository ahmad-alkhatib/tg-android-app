package app.uicomponents

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

class InputValidator(var textInputLayout: TextInputLayout) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        textInputLayout.error = null
        textInputLayout.isErrorEnabled = false
    }
}