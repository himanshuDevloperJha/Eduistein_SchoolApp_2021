package com.cygnus.model

import co.aspirasoft.model.BaseModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AttendanceRecord : BaseModel() {

    var studentName: String? = null
    var studentRollNo: String? = null
    var classId: String? = null
    var date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(System.currentTimeMillis())
    var attendance: Boolean = false

}

class Attendance() {

    constructor(classId: String) : this() {
        this.classId = classId
    }

    var classId: String = ""
    var date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(System.currentTimeMillis())
    var attendanceRecords: ArrayList<AttendanceRecord> = ArrayList()

    fun updateRecord(studentRollNo: String, attendance: Boolean) {
        attendanceRecords.find { it.studentRollNo == studentRollNo }?.attendance = attendance
    }

}