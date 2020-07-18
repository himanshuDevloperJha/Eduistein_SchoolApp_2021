package com.cygnus.model

import co.aspirasoft.model.BaseModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TestScore : BaseModel() {

    var studentName: String? = null
    var studentRollNo: String? = null
    var theoryMarks = 0
    var practicalMarks = 0

}

class Test(classId: String, var subjectId: String) {

    constructor() : this("", "")

    var classId = classId

    var name: String = ""

    var date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(System.currentTimeMillis())

    var maxMarksTheory: Int = 0

    var maxMarksPractical: Int = 0

    var obtainedScore = ArrayList<TestScore>()

    fun updateRecord(studentRollNo: String, theoryMarks: Int, practicalMarks: Int) {
        this.obtainedScore.find { it.studentRollNo == studentRollNo }?.apply {
            this.theoryMarks = theoryMarks
            this.practicalMarks = practicalMarks
        }
    }

}