package com.cygnus.erpfeature

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.CygnusApp
import com.cygnus.R
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.AttendanceDao
import com.cygnus.model.*
import com.cygnus.view.AttendanceView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_attendance_teacher.*
import kotlinx.android.synthetic.main.activity_list.*
import java.util.ArrayList
import java.util.HashMap

class AttendanceTeacher : DashboardChildActivity() {
    private val teacherList: ArrayList<Teacher> = ArrayList()
lateinit var schoolname:String
lateinit var schoolid:String
    private lateinit var adapter: AttTeacherAdapter

    private val attendanceRecords = ArrayList<AttTeachermodel>()
    private lateinit var currentTeacher: Teacher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_teacher)

        schoolname=intent.getStringExtra("studentname")
schoolid=intent.getStringExtra("studentschoolid")

        currentTeacher = when (currentUser) {
            is Teacher -> currentUser as Teacher
            else -> {
                finish()
                return
            }
        }
        adapter = AttTeacherAdapter(this, attendanceRecords)
        attList.adapter = adapter


      /*  AttendanceDao.getByTeacher(schoolId,currentTeacher, OnSuccessListener { savedRecords ->
            attendanceRecords.clear()
            savedRecords?.let {
                supportActionBar?.title = String.format("%.1f%% Attendance",
                        calculateAttendancePercentage(it))
                attendanceRecords.addAll(it)
                adapter.notifyDataSetChanged()
                pb_att.visibility=View.GONE
            }

        })
*/
        addButton_at.visibility = View.GONE

        teacherList.clear()
        getStudentsInClass(schoolid,
                OnSuccessListener {
                    teacherList.addAll(it)
                })
    }

    override fun updateUI(currentUser: User) {

    }

    fun getStudentsInClass(schoolId: String,
                           listener: OnSuccessListener<List<Teacher>>) {
        CygnusApp.refToTeachers(schoolId)
                .orderByChild("type")
                .equalTo("Teacher")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val teachers = ArrayList<Teacher>()
                        snapshot.children.forEach {
                            val user = it.toUser()
                            if (user is Teacher) {

                                teachers.add(user)
                                Log.e("msg", "TEACHERNAME:" + user.name)

                            }
                        }

                        listener.onSuccess(teachers)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(ArrayList())
                        Log.e("msg", "NAMESSSSSSSLIST1" + error.toString())

                    }
                })
    }

    private fun DataSnapshot.toUser(): User? {
        return try {
            val t = object : GenericTypeIndicator<HashMap<String, *>>() {}
            this.getValue(t)?.let {
                when (it["type"]) {
                    Student::class.simpleName -> this.getValue(Student::class.java)
                    Teacher::class.simpleName -> this.getValue(Teacher::class.java)
                    School::class.simpleName -> this.getValue(School::class.java)
                    else -> null
                }
            }
        } catch (ex: Exception) {
            null
        }
    }
    private fun calculateAttendancePercentage(list: List<AttTeachermodel>): Float {
        val presences = list.filter { it.attendance }.size
        val total = list.size
        return 100F * presences / total
    }

    private inner class AttTeacherAdapter(context: Context, val records: List<AttTeachermodel>)
        : ModelViewAdapter<AttTeachermodel>(context, attendanceRecords, AttTeacherView::class) {

        override fun notifyDataSetChanged() {
            //records.sortedBy { it.date }
            attendanceRecords.sortByDescending { it.date }
            pb_att.visibility=View.GONE
            super.notifyDataSetChanged()
        }

    }

}
