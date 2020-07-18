package com.cygnus.sign_up

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.showError
import co.aspirasoft.view.WizardViewStep
import com.cygnus.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class ContactInfoStep : WizardViewStep("Create Profile") {

    private lateinit var emailField: TextInputEditText      // required
    private lateinit var streetField: TextInputEditText     // required
    private lateinit var cityField: TextInputEditText       // required
    private lateinit var stateField: TextInputEditText      // required
    private lateinit var postalCodeField: TextInputEditText // optional
    private lateinit var countryField: TextInputEditText    // required
    private lateinit var phoneField: TextInputEditText      // required

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_contact_info, container, false)

        // Get UI references
        emailField = v.findViewById(R.id.emailField)
        streetField = v.findViewById(R.id.streetField)
        cityField = v.findViewById(R.id.cityField)
        stateField = v.findViewById(R.id.stateField)
        postalCodeField = v.findViewById(R.id.postalCodeField)
        countryField = v.findViewById(R.id.countryField)
        phoneField = v.findViewById(R.id.phoneField)

        // Mark required fields
        markRequired(emailField.parent.parent as TextInputLayout)
        markRequired(streetField.parent.parent as TextInputLayout)
        markRequired(cityField.parent.parent as TextInputLayout)
        markRequired(stateField.parent.parent as TextInputLayout)
        markRequired(countryField.parent.parent as TextInputLayout)
        markRequired(phoneField.parent.parent as TextInputLayout)

        // Validate email as it is typed
        emailField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailField.showError(null)
                if (!s.toString().trim().isEmail()) {
                    emailField.showError(getString(R.string.error_email_invalid))
                }
            }
        })

        return v
    }

    override fun isDataValid(): Boolean {
        return if (emailField.isNotBlank(true) &&
                streetField.isNotBlank(true) &&
                cityField.isNotBlank(true) &&
                stateField.isNotBlank(true) &&
                countryField.isNotBlank(true) &&
                phoneField.isNotBlank(true)) {
            val email = emailField.text.toString().trim().toLowerCase(Locale.getDefault())
            val street = streetField.text.toString().trim()
            val city = cityField.text.toString().trim()
            val state = stateField.text.toString().trim()
            val postalCode = postalCodeField.text.toString().trim()
            val country = countryField.text.toString().trim()
            val phone = phoneField.text.toString().trim()

            if (!email.isEmail()) {
                emailField.showError(getString(R.string.error_email_invalid))
                false
            } else {
                data.put(R.id.emailField, email)
                data.put(R.id.streetField, "$street\n$postalCode $city\n$state $country")
                data.put(R.id.phoneField, phone)
                true
            }
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