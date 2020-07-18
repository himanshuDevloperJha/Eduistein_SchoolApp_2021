package com.cygnus.sign_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.text.HtmlCompat
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.showError
import co.aspirasoft.view.WizardViewStep
import com.cygnus.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EmergencyInfoStep : WizardViewStep("Emergency Info") {

    private lateinit var bloodTypeWrapper: TextInputLayout              // optional
    private lateinit var bloodTypeField: AutoCompleteTextView
    private lateinit var bloodTypes: List<String>

    private lateinit var emergencyContactNameField: TextInputEditText   // required
    private lateinit var emergencyContactPhoneField: TextInputEditText  // required
    private lateinit var emergencyContactEmailField: TextInputEditText  // optional


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_emergency_info, container, false)

        // Get UI references
        bloodTypeWrapper = v.findViewById(R.id.bloodTypeWrapper)
        bloodTypeField = v.findViewById(R.id.bloodTypeField)
        emergencyContactNameField = v.findViewById(R.id.emergencyContactNameField)
        emergencyContactPhoneField = v.findViewById(R.id.emergencyContactPhoneField)
        emergencyContactEmailField = v.findViewById(R.id.emergencyContactEmailField)

        // Mark required fields
        markRequired(emergencyContactNameField.parent.parent as TextInputLayout)
        markRequired(emergencyContactPhoneField.parent.parent as TextInputLayout)

        // Show list of blood types
        bloodTypes = resources.getStringArray(R.array.blood_types).toList()
        bloodTypeField.setAdapter(ArrayAdapter(v.context, android.R.layout.select_dialog_item, bloodTypes))

        return v
    }

    override fun isDataValid(): Boolean {
        bloodTypeWrapper.isErrorEnabled = false
        return if (emergencyContactNameField.isNotBlank(true) &&
                emergencyContactPhoneField.isNotBlank(true)) {
            val bloodType = bloodTypeField.text.toString().trim()
            val name = emergencyContactNameField.text.toString().trim()
            val phone = emergencyContactPhoneField.text.toString().trim()
            val email = emergencyContactEmailField.text.toString().trim()

            if (email.isNotBlank() && !email.isEmail()) {
                emergencyContactEmailField.showError(getString(R.string.error_email_invalid))
                false
            } else if (bloodType.isNotBlank() && !bloodTypes.contains(bloodType)) {
                bloodTypeWrapper.isErrorEnabled = true
                bloodTypeWrapper.error = getString(R.string.error_blood_type_invalid)
                false
            } else {
                data.apply {
                    put(R.id.bloodTypeField, bloodType)
                    put(R.id.emergencyContactNameField, name)
                    put(R.id.emergencyContactEmailField, email)
                    put(R.id.emergencyContactPhoneField, phone)
                }
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