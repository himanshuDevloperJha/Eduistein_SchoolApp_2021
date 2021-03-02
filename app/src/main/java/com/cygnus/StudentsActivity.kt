package com.cygnus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.UsersDao
import com.cygnus.model.Student
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.view.AddStudentDialog
import com.cygnus.view.StudentView
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_list.*
import java.util.*
import kotlin.collections.ArrayList


class StudentsActivity : DashboardChildActivity() {

    private val studentsList: ArrayList<Student> = ArrayList()

    private lateinit var currentTeacher: Teacher
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Only allow a class teacher to access this page
        currentTeacher = when (currentUser) {
            is Teacher -> {
                val teacher = currentUser as Teacher
                if (teacher.classId == null) {
                    finish()
                    return
                }
                teacher
            }
            else -> {
                finish()
                return
            }
        }


        this.adapter = StudentAdapter(this, studentsList)
        contentList.adapter = adapter

        addButton.setOnClickListener { onAddStudentClicked() }
    }

    override fun updateUI(currentUser: User) {
        currentTeacher.classId?.let { classId ->
            UsersDao.getStudentsInClass(schoolId, classId, OnSuccessListener {
                studentsList.clear()
                studentsList.addAll(it)
                adapter.notifyDataSetChanged()

            })
        }
    }

    private fun onAddStudentClicked() {
        val dialog = AddStudentDialog.newInstance(currentTeacher.classId!!,
                currentTeacher.id, schoolId)
        dialog.show(supportFragmentManager, dialog.toString())
    }

    private inner class StudentAdapter(context: Context, val students: List<Student>)
        : ModelViewAdapter<Student>(context, students, StudentView::class) {

        override fun notifyDataSetChanged() {
           // students.sortedByDescending { it.name }
         //   studentsList.sortByDescending { it.rollNo }
          //  Collections.sort(studentsList, compareRollNo(it.rollNo,it.rollNo))
            for(items in studentsList){
                Log.e("msg","yyyyyyyyyyyyyyy::"+items.rollNo)
            }
            Collections.sort(studentsList, AscendingComparator())
            pb_attendance.visibility=View.GONE
           //Collections.sort(studentsList, Comparator { lhs, rhs -> lhs.rollNo.compareTo(rhs.rollNo) })
            super.notifyDataSetChanged()

        }


             /*fun compareRollNo(p1: Student, p2: Student): java.util.Comparator<in Student>? {
                val pM1 = Math.round(p1.rollNo.toFloat()).toInt()
                val pM2 = Math.round(p2.rollNo.toFloat()).toInt()
                return if (pM1 > pM2) {
                    1
                } else if (pM1 < pM2) {
                    -1
                } else {
                    0
                }
            }*/


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                startSecurely(ProfileActivity::class.java, Intent().apply {
                    putExtra(CygnusApp.EXTRA_PROFILE_USER, studentsList[position])
                })
            }
            return v
        }

    }
     class AscendingComparator : Comparator<Student?> {

         override fun compare(p0: Student?, p1: Student?): Int {
             val pM1 = Math.round(p0!!.rollNo.toFloat()).toInt()
             val pM2 = Math.round(p1!!.rollNo.toFloat()).toInt()
             return if (pM1 > pM2) {
                 1
             } else if (pM1 < pM2) {
                 -1
             } else {
                 0
             }
         }
     }

}