package com.cygnus.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.showError
import com.cygnus.R
import com.cygnus.dao.UsersDao
import com.cygnus.model.Student
import com.cygnus.tasks.InvitationTask
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class AddStudentDialog : BottomSheetDialogFragment() {

    private var model: Student? = null
    private val isReadOnly: Boolean get() = model != null

    private lateinit var classId: String
    private lateinit var schoolId: String
    private lateinit var teacherId: String

    private lateinit var rollNumberField: TextInputEditText
    private lateinit var studentEmailField: TextInputEditText
    private lateinit var okButton: Button
    private lateinit var editButton: Button

    var onDismissListener: ((dialog: DialogInterface) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_add_student, container, false)

        try {
            val args = requireArguments()
            classId = args.getString(ARG_CLASS_ID)!!
            schoolId = args.getString(ARG_SCHOOL_ID)!!
            teacherId = args.getString(ARG_TEACHER_ID)!!
            model = args.getSerializable(ARG_SAVED_INSTANCE) as Student?
        } catch (ex: Exception) {
            Toast.makeText(v.context, ex.message, Toast.LENGTH_LONG).show()
            dismiss()
            return null
        }

        rollNumberField = v.findViewById(R.id.rollNoField)
        studentEmailField = v.findViewById(R.id.emailField)

        okButton = v.findViewById(R.id.okButton)
        editButton = v.findViewById(R.id.editButton)
        okButton.setOnClickListener { onOk() }
        editButton.setOnClickListener { dismiss() }

        setEditModeEnabled(!isReadOnly)
        return v
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.let { it(dialog) }
    }

    private fun setEditModeEnabled(enabled: Boolean) {
        rollNumberField.isEnabled = enabled
        studentEmailField.isEnabled = enabled
        okButton.visibility = if (enabled) View.VISIBLE else View.GONE
        editButton.visibility = if (enabled) View.GONE else View.VISIBLE

        if (!enabled) {
            rollNumberField.setText(model?.rollNo)
            studentEmailField.setText(model?.email)
        }
    }

    private fun inviteStudent(rollNo: String, email: String) {
        context?.let {
            InvitationTask(it, schoolId, email, teacherId, classId, rollNo).start { task ->
                isCancelable = true
                okButton.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(
                            studentEmailField.context,
                            getString(R.string.status_invitation_sent),
                            Toast.LENGTH_LONG
                    ).show()
                    dismiss()
                } else {
                    studentEmailField.showError(task.exception?.message
                            ?: getString(R.string.error_invitation_not_sent))
                }
            }
        }
    }

    private fun onOk() {
        if (rollNumberField.isNotBlank(true) && studentEmailField.isNotBlank(true)) {
            val rollNo = rollNumberField.text.toString().trim()
            val email = studentEmailField.text.toString().trim()
            if (email.isEmail()) {
                isCancelable = false
                okButton.isEnabled = false

                // Ensure roll number and email are unique
                UsersDao.getStudentByRollNumber(schoolId, rollNo, OnSuccessListener { existingStudent ->
                    if (existingStudent != null) {
                        rollNumberField.showError(getString(R.string.error_roll_no_exists))
                        isCancelable = true
                        okButton.isEnabled = true
                    } else UsersDao.getUserByEmail(schoolId, email, OnSuccessListener { existingUser ->
                        if (existingUser != null) {
                            studentEmailField.showError(getString(R.string.error_email_exists))
                            isCancelable = true
                            okButton.isEnabled = true
                        } else inviteStudent(rollNo, email)
                    })
                })
            } else {
                studentEmailField.showError(getString(R.string.error_email_invalid))
            }
        }
    }

    companion object {
        private const val ARG_CLASS_ID = "class_id"
        private const val ARG_SCHOOL_ID = "school_id"
        private const val ARG_TEACHER_ID = "teacher_id"
        private const val ARG_SAVED_INSTANCE = "saved"

        @JvmStatic
        fun newInstance(
                classId: String,
                teacherId: String,
                schoolId: String,
                savedInstance: Student? = null
        ): AddStudentDialog {
            return AddStudentDialog().also {
                it.arguments = Bundle().apply {
                    putString(ARG_CLASS_ID, classId)
                    putString(ARG_TEACHER_ID, teacherId)
                    putString(ARG_SCHOOL_ID, schoolId)
                    putSerializable(ARG_SAVED_INSTANCE, savedInstance)
                }
            }
        }
    }

}