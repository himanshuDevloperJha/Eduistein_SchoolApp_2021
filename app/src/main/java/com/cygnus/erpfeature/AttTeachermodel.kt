package com.cygnus.erpfeature

import co.aspirasoft.model.BaseModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AttTeachermodel : BaseModel() {

    var teacherName: String? = null
    var teacherId: String? = null
    var classId: String? = null
    var date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(System.currentTimeMillis())
    var attendance: Boolean = false

}

class AttTeacher() {

    constructor(classId: String) : this() {
        this.classId = classId
    }

    var classId: String = ""
    var date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(System.currentTimeMillis())
    var attendanceRecords: ArrayList<AttTeachermodel> = ArrayList()

    fun updateRecord(teacherId: String, attendance: Boolean) {
        attendanceRecords.find { it.teacherId == teacherId }?.attendance = attendance
    }

}