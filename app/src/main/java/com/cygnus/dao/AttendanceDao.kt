package com.cygnus.dao

import com.cygnus.CygnusApp
import com.cygnus.erpfeature.AttTeacher
import com.cygnus.model.Attendance
import com.cygnus.model.AttendanceRecord
import com.cygnus.model.Student
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

public object AttendanceDao {

   public fun add(schoolId: String, attendance: Attendance, listener: OnCompleteListener<Void?>) {
        CygnusApp.refToAttendance(schoolId, attendance.classId)
                .child(attendance.date)
                .setValue(attendance)
                .addOnCompleteListener(listener)
    }

    public fun addTeacher(schoolId: String, attendanceRecordStaff: AttTeacher, listener: OnCompleteListener<Void?>){
        CygnusApp.refToAttTeacher(schoolId)
                .setValue(attendanceRecordStaff)
                .addOnCompleteListener(listener)
    }
    fun getByDateTeacher(schoolId: String, school: String, date: String, listener: OnSuccessListener<AttTeacher?>) {
        CygnusApp.refToAttTeacher(schoolId)
                .child(date)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listener.onSuccess(snapshot.getValue(AttTeacher::class.java))

                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

    /**
     * Gets a class's attendance records of the given day.
     *
     * @param schoolId The id of the class's school.
     * @param classId The name of the class.
     * @param date The date for which to get attendance records.
     * @param listener A listener for receiving response of the request.
     */
    fun getByDate(schoolId: String, classId: String, date: String, listener: OnSuccessListener<Attendance?>) {
        CygnusApp.refToAttendance(schoolId, classId)
                .child(date)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listener.onSuccess(snapshot.getValue(Attendance::class.java))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

    /**
     * Gets a student's attendance records.
     *
     * @param schoolId The id of the class's school.
     * @param student The student whose attendance to get.
     * @param listener A listener for receiving response of the request.
     */
    fun getByStudent(schoolId: String, student: Student, listener: OnSuccessListener<List<AttendanceRecord>?>) {
        CygnusApp.refToAttendance(schoolId, student.classId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, Attendance>>() {}
                        val records = ArrayList<AttendanceRecord>()
                        snapshot.getValue(t)?.let { map ->
                            map.values.forEach { attendance ->
                                records.addAll(attendance.attendanceRecords.filter { it.studentRollNo == student.rollNo })
                            }
                            listener.onSuccess(records)
                        } ?: listener.onSuccess(null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

   /* fun getByTeacher(schoolId: String, teacher: Teacher, listener: OnSuccessListener<List<AttTeachermodel>?>) {
        CygnusApp.refToAttendance(schoolId, teacher.classId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, AttTeachermodel>>() {}
                        val records = ArrayList<AttTeachermodel>()
                        snapshot.getValue(t)?.let { map ->
                            map.values.forEach { attendance ->
                                records.addAll(attendance.attendanceRecords.filter { it.studentRollNo == student.rollNo })
                            }
                            listener.onSuccess(records)
                        } ?: listener.onSuccess(null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }*/



}