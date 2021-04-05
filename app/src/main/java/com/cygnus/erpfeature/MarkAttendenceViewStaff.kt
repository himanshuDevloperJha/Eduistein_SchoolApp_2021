package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.erpfeature.AttTeachermodel
import com.cygnus.model.AttendanceRecord
import com.google.android.material.radiobutton.MaterialRadioButton

class MarkAttendenceViewStaff : BaseView<AttTeachermodel> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var markPresentButton: MaterialRadioButton
    private var markAbsentButton: MaterialRadioButton
    private var teacherNameView: TextView
    private var teacherIDView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_attendance_staff, this)
        markPresentButton = findViewById(R.id.markPresentButton1)
        markAbsentButton = findViewById(R.id.markAbsentButton1)
        teacherNameView = findViewById(R.id.teachernamee)
        teacherIDView = findViewById(R.id.teacheridd)

        markPresentButton.isChecked = false
        markAbsentButton.isChecked = true
    }

    override fun updateView(model: AttTeachermodel) {
        teacherNameView.text = model.name
        teacherIDView.text = "Email # ${model.id}"
        Log.e("msg", "Attendanc:" + model.name!!)

        if (model.attendance.equals(true)) {
            markPresentButton.isChecked = true
            Log.e("msg", "Attendancee112::" + model.id!! + ", " + model.attendance)

        }
//        else if(model.attendance.equals(false)){
//        }


        markPresentButton.setOnCheckedChangeListener { _, isChecked ->
            model.attendance = isChecked
        }
    }
}



