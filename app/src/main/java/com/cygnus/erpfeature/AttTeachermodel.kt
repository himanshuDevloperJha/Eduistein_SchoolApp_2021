package com.cygnus.erpfeature

import co.aspirasoft.model.BaseModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AttTeachermodel : BaseModel() {

    var name: String? = null
    var id: String?=null
    var classId: String? = null
    var date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(System.currentTimeMillis())
    var monthname: String = SimpleDateFormat("MMMM", Locale.getDefault()).format(System.currentTimeMillis())
    var yearname: String = SimpleDateFormat("yyyy", Locale.getDefault()).format(System.currentTimeMillis())
    var attendance: Boolean = false

}

class AttTeacher() {

    constructor(school: String) : this() {
        this.school = school
    }

    var school: String = ""
    var date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(System.currentTimeMillis())
    var attendanceRecords: ArrayList<AttTeachermodel> = ArrayList()

    fun updateRecord(teacherName : String, attendance: Boolean) {
        attendanceRecords.find { it.classId == teacherName }?.attendance = attendance
    }

}