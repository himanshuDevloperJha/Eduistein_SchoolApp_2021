package com.cygnus.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import co.aspirasoft.util.InputUtils.isNotBlank
import com.cygnus.R
import com.cygnus.dao.SubjectsDao
import com.cygnus.model.Subject
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddSubjectDialog : BottomSheetDialogFragment() {

    private var model: Subject? = null
    private val isReadOnly: Boolean get() = model != null

    private lateinit var schoolId: String
    private lateinit var classes: ArrayList<String>
    private lateinit var teachers: ArrayList<String>

    private lateinit var subjectNameField: TextInputEditText
    private lateinit var subjectClassField: AutoCompleteTextView
    private lateinit var subjectClassWrapper: TextInputLayout
    private lateinit var subjectTeacherField: AutoCompleteTextView
    private lateinit var subjectTeacherWrapper: TextInputLayout
    private lateinit var skipTeacherAssignment: MaterialCheckBox
    private lateinit var okButton: Button
    private lateinit var editButton: Button

    var onOkListener: ((subject: Subject) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_add_subject, container, false)

        try {
            val args = requireArguments()
            classes = args.getStringArrayList(ARG_CLASSES)!!
            teachers = args.getStringArrayList(ARG_TEACHERS)!!
            schoolId = args.getString(ARG_SCHOOL_ID)!!
            model = args.getSerializable(ARG_SAVED_INSTANCE) as Subject?
        } catch (ex: Exception) {
            Toast.makeText(v.context, ex.message, Toast.LENGTH_LONG).show()
            dismiss()
            return null
        } finally {
            if (classes.isEmpty()) {
                Toast.makeText(v.context, "You must add your school classes first.", Toast.LENGTH_LONG).show()
                dismiss()
                return null
            }
        }

        subjectNameField = v.findViewById(R.id.subjectNameField)

        // Show list of available classes
        subjectClassWrapper = v.findViewById(R.id.subjectClassWrapper)
        subjectClassField = v.findViewById(R.id.subjectClassField)
        subjectClassField.setAdapter(ArrayAdapter(v.context, android.R.layout.select_dialog_item, classes))
        subjectClassField.threshold = 1

        // Show list of available teachers
        subjectTeacherWrapper = v.findViewById(R.id.subjectTeacherWrapper)
        subjectTeacherField = v.findViewById(R.id.subjectTeacherField)
        subjectTeacherField.setAdapter(ArrayAdapter(v.context, android.R.layout.select_dialog_item, teachers))
        subjectTeacherField.threshold = 1

        skipTeacherAssignment = v.findViewById(R.id.skipTeacherAssignment)
        skipTeacherAssignment.setOnCheckedChangeListener { _, isChecked ->
            subjectTeacherWrapper.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        okButton = v.findViewById(R.id.okButton)
        editButton = v.findViewById(R.id.editButton)
        okButton.setOnClickListener { onOk() }
        editButton.setOnClickListener { onEdit() }

        setEditModeEnabled(!isReadOnly)
        return v
    }

    private fun setEditModeEnabled(enabled: Boolean) {
        subjectNameField.isEnabled = enabled && !isReadOnly
        subjectClassField.isEnabled = enabled && !isReadOnly
        subjectTeacherField.isEnabled = enabled
        skipTeacherAssignment.visibility = if (enabled) View.VISIBLE else View.GONE
        okButton.visibility = if (enabled) View.VISIBLE else View.GONE
        editButton.visibility = if (enabled) View.GONE else View.VISIBLE

        if (enabled && teachers.isEmpty()) {
            skipTeacherAssignment.isEnabled = !enabled
            skipTeacherAssignment.isChecked = enabled
        } else if (!enabled) {
            subjectNameField.setText(model?.name)
            subjectClassField.setText(model?.classId)
            subjectTeacherField.setText(model?.teacherId)
        }
    }

    private fun onEdit() {
        setEditModeEnabled(true)
    }

    private fun onOk() {
        subjectClassWrapper.isErrorEnabled = false
        subjectTeacherWrapper.isErrorEnabled = false
        if (subjectNameField.isNotBlank(true)) {
            isCancelable = false
            okButton.isEnabled = false

            val subjectName = subjectNameField.text.toString().trim()
            val subjectClass = subjectClassField.text.toString().trim()
            var subjectTeacher = subjectTeacherField.text.toString().trim()
            if (subjectClass.isBlank() || (!skipTeacherAssignment.isChecked && subjectTeacher.isBlank())) {
                onError()
                return
            } else if (skipTeacherAssignment.isChecked) {
                subjectTeacher = ""
            }

            if (teachers.contains(subjectTeacher) || skipTeacherAssignment.isChecked) {
                val subject = Subject(subjectName, subjectTeacher, subjectClass)
                SubjectsDao.add(schoolId, subject, OnCompleteListener {
                    if (it.isSuccessful) {
                        onOkListener?.let { it(subject) }
                        dismiss()
                    } else onError(it.exception?.message)
                })
            } else onError()
        }
    }

    private fun onError(error: String? = null) {
        subjectTeacherWrapper.isErrorEnabled = true
        subjectTeacherWrapper.error = error ?: "Select a valid teacher from the list."
        isCancelable = true
        okButton.isEnabled = true
    }

    companion object {
        private const val ARG_CLASSES = "classes"
        private const val ARG_TEACHERS = "teachers"
        private const val ARG_SCHOOL_ID = "school"
        private const val ARG_SAVED_INSTANCE = "saved"

        @JvmStatic
        fun newInstance(
                classes: ArrayList<String>,
                teachers: ArrayList<String>,
                schoolId: String,
                savedInstance: Subject? = null
        ): AddSubjectDialog {
            return AddSubjectDialog().also {
                it.arguments = Bundle().apply {
                    putStringArrayList(ARG_CLASSES, classes)
                    putStringArrayList(ARG_TEACHERS, teachers)
                    putString(ARG_SCHOOL_ID, schoolId)
                    putSerializable(ARG_SAVED_INSTANCE, savedInstance)
                }
            }
        }
    }
}
