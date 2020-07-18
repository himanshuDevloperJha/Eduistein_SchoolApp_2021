package com.cygnus.sign_up

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.showError
import co.aspirasoft.view.WizardViewStep
import com.cygnus.R
import com.cygnus.model.Teacher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CreateAccountStep : WizardViewStep("Welcome to Eduistein") {

    private lateinit var signUpWelcomeMessage: TextView
    private lateinit var passwordField: TextInputEditText
    private lateinit var passwordRepeatField: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_create_account, container, false)

        // Get UI references
        signUpWelcomeMessage = v.findViewById(R.id.signUpMessage)
        passwordField = v.findViewById(R.id.passwordField)
        passwordRepeatField = v.findViewById(R.id.passwordRepeatField)

        // Mark required fields
        markRequired(passwordField.parent.parent as TextInputLayout)
        markRequired(passwordRepeatField.parent.parent as TextInputLayout)

        // Validate passwords as they are typed
        passwordField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordField.isNotBlank(true)
            }
        })
        passwordRepeatField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordRepeatField.isNotBlank(true)
                checkPasswordValid()
            }
        })

        return v
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            it as SignUpActivity
            signUpWelcomeMessage.text = String.format(
                    it.getString(R.string.sign_up_welcome_msg),
                    if (it.accountType == Teacher::class) "Respected" else "Dear", // Greeting
                    it.accountType.simpleName, // Account Type
                    if (it.accountType == Teacher::class) "school" else "class teacher" // Greeting
            )
        }
    }

    private fun checkPasswordValid(): Boolean {
        passwordField.showError(null)
        if (passwordField.text.isNullOrBlank() || passwordField.text.toString() != passwordRepeatField.text.toString()) {
            passwordField.showError(getString(R.string.error_password_mismatch))
            return false
        }

        return true
    }

    override fun isDataValid(): Boolean {
        return if (passwordField.isNotBlank(true) &&
                passwordRepeatField.isNotBlank(true) &&
                checkPasswordValid()) {
            val password = passwordField.text.toString().trim()

            data.apply {
                put(R.id.passwordField, password)
            }
            true
        } else false
    }

    /**
     * Appends a red asterisk to the [field]'s hint to mark it as required.
     */
    private fun markRequired(field: TextInputLayout) {
        field.hint = HtmlCompat.fromHtml(
                "${field.hint} ${getString(R.string.required_asterisk)}",
                HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }

}