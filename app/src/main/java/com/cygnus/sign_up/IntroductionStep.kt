package com.cygnus.sign_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.core.text.HtmlCompat
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.view.WizardViewStep
import com.cygnus.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class IntroductionStep : WizardViewStep("Say Hi") {

    private lateinit var userImage: ImageView           // optional
    private lateinit var nameField: TextInputEditText   // required
    private lateinit var dateOfBirthField: TextInputEditText // required
    private lateinit var genderField: RadioGroup        // required

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_introduction, container, false)

        // Get UI references
        userImage = v.findViewById(R.id.userImage)
        nameField = v.findViewById(R.id.nameField)
        dateOfBirthField = v.findViewById(R.id.dateOfBirthField)
        genderField = v.findViewById(R.id.genderField)

        // Mark required fields
        markRequired(nameField.parent.parent as TextInputLayout)
        markRequired(dateOfBirthField.parent.parent as TextInputLayout)

        // Show a date picker for birthday selection
        dateOfBirthField.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker().build()
            picker.addOnPositiveButtonClickListener {
                dateOfBirthField.setText(SimpleDateFormat("dd/MM/yyyy").format(Date(it)))
            }

            fragmentManager?.let { manager -> picker.show(manager, picker.toString()) }
        }

        // TODO: Allow selecting an image from Gallery

        return v
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            it as SignUpActivity
            userImage.setImageResource(R.drawable.ph_student)
        }
    }

    override fun isDataValid(): Boolean {
        return if (nameField.isNotBlank(true) && dateOfBirthField.isNotBlank(true)) {
            val imageUri = "" // TODO: Get uri of the selected user image
            val name = nameField.text.toString().trim()
            val birthday = dateOfBirthField.text.toString().trim()
            val gender = when (genderField.checkedRadioButtonId) {
                R.id.genderMale -> getString(R.string.label_male)
                R.id.genderFemale -> getString(R.string.label_female)
                else -> getString(R.string.label_non_binary)
            }

            data.apply {
                data.put(R.id.userImage, imageUri)
                data.put(R.id.nameField, name)
                data.put(R.id.dateOfBirthField, birthday)
                data.put(R.id.genderField, gender)
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