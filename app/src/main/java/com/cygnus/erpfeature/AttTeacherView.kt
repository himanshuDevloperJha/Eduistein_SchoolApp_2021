package com.cygnus.erpfeature

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.AttendanceRecord
import com.google.android.material.radiobutton.MaterialRadioButton

class AttTeacherView : BaseView<AttTeachermodel> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var markPresentButton: MaterialRadioButton
    private var markAbsentButton: MaterialRadioButton
    private var studentNameView: TextView
    private var studentRollNoView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_attendance, this)
        markPresentButton = findViewById(R.id.markPresentButton)
        markAbsentButton = findViewById(R.id.markAbsentButton)
        studentNameView = findViewById(R.id.nameField)
        studentRollNoView = findViewById(R.id.rollNo)

        markPresentButton.isEnabled = false
        markAbsentButton.isEnabled = false
    }

    override fun updateView(model: AttTeachermodel) {
        studentNameView.text = model.date
        studentRollNoView.text = if (model.attendance) "Present" else "Absent"
        markPresentButton.isChecked = model.attendance
        markAbsentButton.isChecked = !model.attendance
    }

}