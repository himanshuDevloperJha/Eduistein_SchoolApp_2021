package com.cygnus

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.ClassesDao
import com.cygnus.dao.Invite
import com.cygnus.model.Subject
import com.cygnus.model.User
import com.cygnus.view.AddSubjectDialog
import com.cygnus.view.SubjectView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_course_list.*
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_school.*
import java.lang.reflect.Array

class SchoolSubjectsActivity : DashboardChildActivity() {

    private val classes: ArrayList<String> = ArrayList()
    private val teachers: ArrayList<String> = ArrayList()
    private val subjects: ArrayList<Subject> = ArrayList()

    private lateinit var adapter: SubjectAdapter
    var selected_class:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)



        // Read staff list from intent
        val invites = intent.getParcelableArrayListExtra<Invite>(CygnusApp.EXTRA_INVITES)
        if (invites == null) {
            finish()
            return
        }
        invites.forEach { this.teachers.add(it.invitee) }

        adapter = SubjectAdapter(this, subjects)
        contentList1.adapter = adapter
        contentList1.divider = null

        admin_course_class.setAdapter(ArrayAdapter(applicationContext,
                android.R.layout.select_dialog_item, classes))

        admin_course_class.setOnItemClickListener { adapterView, view, i, l ->
            selected_class = admin_course_class.adapter.getItem(i).toString()
            subjects.clear()
            ClassesDao.getClassesAtSchool(schoolId, OnSuccessListener {
                it?.forEach { schoolClass ->
                    schoolClass.subjects?.values?.forEach { subject ->
                       // if (!subjects.contains(subject))
                            if(selected_class.equals(subject.classId)){
                                subjects.add(subject)
                            }

                    }
                }
                adapter = SubjectAdapter(this, subjects)
                contentList1.adapter = adapter
                contentList1.divider = null
            })
//                for(s in subjects){
//                    if(selected_class.equals(s.classId)){
//                        val sb= Subject(s.name,s.teacherId,s.classId)
//                        subjects.add(sb)
//                    }
//                }
//                adapter = SubjectAdapter(this, subjects)
//                contentList1.adapter = adapter
//                contentList1.divider = null
        }

        addButton1.setOnClickListener { onAddClassClicked() }
    }

    override fun updateUI(currentUser: User) {
        ClassesDao.getClassesAtSchool(schoolId, OnSuccessListener {
            classes.clear()
            it?.forEach { schoolClass ->
                classes.add(schoolClass.name)
                schoolClass.subjects?.values?.forEach { subject ->
                    if (!subjects.contains(subject))
                        subjects.add(subject)
                }
            }
            adapter.notifyDataSetChanged()
        })
    }

    private fun onAddClassClicked() {
        if (classes.isNotEmpty()) {
            val dialog = AddSubjectDialog.newInstance(classes, teachers, currentUser.id)
            dialog.onOkListener = { subject ->
                subjects.add(subject)
                adapter.notifyDataSetChanged()
            }
            dialog.show(supportFragmentManager, dialog.toString())
        } else {
            Toast.makeText(this, "You must add your school classes first.", Toast.LENGTH_LONG).show()
        }
    }

    private inner class SubjectAdapter(context: Context, val subjects: ArrayList<Subject>)
        : ModelViewAdapter<Subject>(context, subjects, SubjectView::class) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.findViewById<View>(R.id.subjectColor)?.apply {
                layoutParams = layoutParams.apply {
                    this.width = 25
                }
            }
            v.findViewById<MaterialCardView>(R.id.subjectCard)?.apply {
                useCompatPadding = false
                cardElevation = 0f
                radius = 0f
                setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT))
            }
            v.setOnClickListener {
                val dialog = AddSubjectDialog.newInstance(classes, teachers, currentUser.id, subjects[position])
                dialog.onOkListener = { subject ->
                    subjects[position] = subject
                    adapter.notifyDataSetChanged()
                }
                dialog.show(supportFragmentManager, dialog.toString())
            }
            (v as SubjectView).setSubjectTeacherVisible(true)
            return v
        }

    }

}