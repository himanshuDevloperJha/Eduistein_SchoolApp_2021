package co.aspirasoft.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Matcher
import java.util.regex.Pattern

object InputUtils {

    /**
     * Returns `true` if this [TextInputEditText] contains some valid non-whitespace characters.
     *
     * If [isErrorEnabled] is `true`, an appropriate error message is generated.
     */
    fun TextInputEditText.isNotBlank(isErrorEnabled: Boolean = false): Boolean {
        this.showError(null)
        return if (this.text.isNullOrBlank()) {
            if (isErrorEnabled) this.showError("${this.hint} is required.")
            false
        } else true
    }

    /**
     * Shows the [error] message in an appropriate format.
     */
    fun TextInputEditText.showError(error: String?) {
        try {
            val wrapper = this.parent.parent as TextInputLayout
            wrapper.isErrorEnabled = error != null
            wrapper.error = error
        } catch (ignored: Exception) {
            error?.let { Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show() }
        }
    }

    /**
     * Method is used for checking valid email address format.
     *
     * @return boolean true for valid false for invalid
     */
    fun String.isEmail(): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(this)
        return matcher.matches()
    }


    fun Activity.hideKeyboard() {
        val imm: InputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = this.currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}