package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.SchoolClass

class SchoolClassView : BaseView<SchoolClass> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var classNameView: TextView
    private var classTeacherView: TextView
    private var classTeacherNameView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_mamage_class, this)
        classNameView = findViewById(R.id.tv_clname)
        classTeacherView = findViewById(R.id.tv_teacherid)
        classTeacherNameView = findViewById(R.id.tv_teachername)

    }

    override fun updateView(model: SchoolClass) {
        classNameView.text = model.name
        if (model.teachername.isBlank()) {
            classTeacherNameView.visibility = View.GONE
        } else {
            classTeacherNameView.text ="Teacher Name: " + model.teachername
        }
        classTeacherView.text = if (model.teacherId.isBlank()) "No Teacher Assigned" else "Teacher ID: " + model.teacherId
    }

}