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
import com.cygnus.CygnusApp
import com.cygnus.R
import com.cygnus.dao.ClassesDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.SchoolClass
import com.cygnus.model.Teacher
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class AddClassDialog : BottomSheetDialogFragment() {

    private var model: SchoolClass? = null
    private val isReadOnly: Boolean get() = model != null

    private lateinit var schoolId: String
    private lateinit var teachers: ArrayList<String>

    private lateinit var classNameField: TextInputEditText
    private lateinit var class_teacher_name: TextInputEditText
    private lateinit var classTeacherField: AutoCompleteTextView
    private lateinit var classTeacherWrapper: TextInputLayout
    private lateinit var skipTeacherAssignment: MaterialCheckBox
    private lateinit var okButton: Button
    private lateinit var editButton: Button

    var onOkListener: ((schoolClass: SchoolClass) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_add_class, container, false)

        try {
            val args = requireArguments()
            teachers = args.getStringArrayList(ARG_TEACHERS)!!
            schoolId = args.getString(ARG_SCHOOL_ID)!!
            model = args.getSerializable(ARG_SAVED_CLASS) as SchoolClass?
        } catch (ex: Exception) {
            Toast.makeText(v.context, ex.message, Toast.LENGTH_LONG).show()
            dismiss()
        }

        classNameField = v.findViewById(R.id.classNameField)

        classTeacherField = v.findViewById(R.id.classTeacherField)
        class_teacher_name = v.findViewById(R.id.class_teacher_name)
        classTeacherWrapper = v.findViewById(R.id.classTeacherWrapper)
        classTeacherField.setAdapter(ArrayAdapter(v.context, android.R.layout.select_dialog_item, teachers))
        classTeacherField.threshold = 1

        skipTeacherAssignment = v.findViewById(R.id.skipTeacherAssignment)
        skipTeacherAssignment.setOnCheckedChangeListener { _, isChecked ->
            classTeacherWrapper.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        okButton = v.findViewById(R.id.okButton)
        editButton = v.findViewById(R.id.editButton)
        okButton.setOnClickListener { onOk() }
        editButton.setOnClickListener { onEdit() }

        setEditModeEnabled(!isReadOnly)
        return v
    }

    private fun setEditModeEnabled(enabled: Boolean) {
        classNameField.isEnabled = enabled && !isReadOnly
        classTeacherField.isEnabled = enabled
        class_teacher_name.isEnabled = enabled
        skipTeacherAssignment.visibility = if (enabled) View.VISIBLE else View.GONE
        okButton.visibility = if (enabled) View.VISIBLE else View.GONE
        editButton.visibility = if (enabled) View.GONE else View.VISIBLE

        if (enabled && teachers.isEmpty()) {
            skipTeacherAssignment.isEnabled = !enabled
            skipTeacherAssignment.isChecked = enabled
        } else if (!enabled) {
            classNameField.setText(model?.name)
            class_teacher_name.setText(model?.teachername)
            classTeacherField.setText(model?.teacherId)
        }
    }

    private fun onEdit() {
        setEditModeEnabled(true)
    }

    private fun onOk() {
        classTeacherWrapper.isErrorEnabled = false
        if (classNameField.isNotBlank(true)) {
            isCancelable = false
            okButton.isEnabled = false

            val className = classNameField.text.toString().trim()
            val teacherName = class_teacher_name.text.toString().trim()
            var classTeacher = classTeacherField.text.toString().trim()
            if (!skipTeacherAssignment.isChecked && classTeacher.isBlank()) {
                onError()
                return
            } else if (skipTeacherAssignment.isChecked) {
                classTeacher = ""
            }

            if (teachers.contains(classTeacher) || skipTeacherAssignment.isChecked) {
                val schoolClass = SchoolClass(className, classTeacher,teacherName)
                if (classTeacher.isNotBlank()) {
                    CygnusApp.refToClasses(schoolId)
                            .orderByChild("teacherId")
                            .equalTo(classTeacher)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val t = object : GenericTypeIndicator<HashMap<String, SchoolClass>>() {}
                                    snapshot.getValue(t)?.values?.filter { it.name != className }?.forEach {
                                        CygnusApp.refToClasses(schoolId)
                                                .child(it.name)
                                                .child("teacherId")
                                                .setValue("")
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })
                }

                val oldTeacher = model?.teacherId
                if (!oldTeacher.isNullOrBlank() && oldTeacher != classTeacher) {
                    UsersDao.getUserByEmail(schoolId, oldTeacher, OnSuccessListener {
                        it?.let { user ->
                            (user as Teacher).classId = ""
                            UsersDao.update(schoolId, user, OnSuccessListener { })
                        }
                    })
                }

                ClassesDao.add(schoolId, schoolClass, OnCompleteListener {
                    if (it.isSuccessful) {
                        if (classTeacher.isBlank()) onSuccess(schoolClass) // N -> N
                        else UsersDao.getUserByEmail(schoolId, classTeacher, OnSuccessListener {
                            it?.let { user ->
                                if (user is Teacher) {
                                    user.classId = className
                                    UsersDao.update(schoolId, user, OnSuccessListener { success ->
                                        if (success) onSuccess(schoolClass) else onError(getString(R.string.error_unknown))
                                    })
                                } else onError()
                            } ?: onError()
                        })
                    } else onError(it.exception?.message)
                })
            } else onError()
        }
    }

    private fun onSuccess(schoolClass: SchoolClass) {
        onOkListener?.let { it(schoolClass) }
        dismiss()
    }

    private fun onError(error: String? = null) {
        classTeacherWrapper.isErrorEnabled = true
        classTeacherWrapper.error = error ?: getString(R.string.error_invalid_teacher)
        isCancelable = true
        okButton.isEnabled = true
    }

    companion object {
        private const val ARG_TEACHERS = "teachers"
        private const val ARG_SCHOOL_ID = "school"
        private const val ARG_SAVED_CLASS = "class"

        @JvmStatic
        fun newInstance(teachers: ArrayList<String>, schoolId: String, savedInstance: SchoolClass? = null): AddClassDialog {
            return AddClassDialog().also {
                it.arguments = Bundle().apply {
                    putStringArrayList(ARG_TEACHERS, teachers)
                    putString(ARG_SCHOOL_ID, schoolId)
                    putSerializable(ARG_SAVED_CLASS, savedInstance)
                }
            }
        }
    }
}
