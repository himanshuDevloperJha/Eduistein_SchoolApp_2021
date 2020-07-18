package com.cygnus

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

class StudentsActivity : DashboardChildActivity() {

    private val students: ArrayList<Student> = ArrayList()

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

        adapter = StudentAdapter(this, students)
        contentList.adapter = adapter

        addButton.setOnClickListener { onAddStudentClicked() }
    }

    override fun updateUI(currentUser: User) {
        currentTeacher.classId?.let { classId ->
            UsersDao.getStudentsInClass(schoolId, classId, OnSuccessListener {
                students.clear()
                students.addAll(it)
                adapter.notifyDataSetChanged()
            })
        }
    }

    private fun onAddStudentClicked() {
        val dialog = AddStudentDialog.newInstance(currentTeacher.classId!!, currentTeacher.id, schoolId)
        dialog.show(supportFragmentManager, dialog.toString())
    }

    private inner class StudentAdapter(context: Context, val students: List<Student>)
        : ModelViewAdapter<Student>(context, students, StudentView::class) {

        override fun notifyDataSetChanged() {
            students.sortedBy { it.rollNo }
            super.notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                startSecurely(ProfileActivity::class.java, Intent().apply {
                    putExtra(CygnusApp.EXTRA_PROFILE_USER, students[position])
                })
            }
            return v
        }

    }

}