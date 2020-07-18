package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.AttendanceRecord
import com.google.android.material.radiobutton.MaterialRadioButton

class MarkAttendanceView : BaseView<AttendanceRecord> {

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

        markPresentButton.isChecked = false
        markAbsentButton.isChecked = true
    }

    override fun updateView(model: AttendanceRecord) {
        studentNameView.text = model.studentName
        studentRollNoView.text = "Roll # ${model.studentRollNo}"
        markPresentButton.isChecked = model.attendance
        markAbsentButton.isChecked = !model.attendance
        markPresentButton.setOnCheckedChangeListener { _, isChecked ->
            model.attendance = isChecked
        }
    }

}