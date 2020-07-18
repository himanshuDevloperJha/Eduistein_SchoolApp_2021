package com.cygnus.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.TestScore

class TestScoreView : BaseView<TestScore> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var studentNameView: TextView
    private var studentRollNoView: TextView
    private var theoryMarksField: EditText
    private var practicalMarksField: EditText

    init {
        LayoutInflater.from(context).inflate(R.layout.view_add_grade, this)
        studentNameView = findViewById(R.id.nameField)
        studentRollNoView = findViewById(R.id.rollNo)
        theoryMarksField = findViewById(R.id.theoryMarks)
        practicalMarksField = findViewById(R.id.practicalMarks)
    }

    override fun updateView(model: TestScore) {
        studentNameView.text = model.studentName
        studentRollNoView.text = model.studentRollNo
        theoryMarksField.setText(model.theoryMarks.toString())
        theoryMarksField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (studentRollNoView.text == model.studentRollNo) {
                    model.theoryMarks = s?.toString()?.toIntOrNull() ?: 0
                }
            }
        })
        practicalMarksField.setText(model.practicalMarks.toString())
        practicalMarksField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (studentRollNoView.text == model.studentRollNo) {
                    model.practicalMarks = s.toString().toIntOrNull() ?: 0
                }
            }
        })
    }

    fun setEditable(editable: Boolean) {
        theoryMarksField.isEnabled = editable
        practicalMarksField.isEnabled = editable
    }

}