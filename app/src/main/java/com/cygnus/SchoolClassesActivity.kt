package com.cygnus

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.ClassesDao
import com.cygnus.dao.Invite
import com.cygnus.model.SchoolClass
import com.cygnus.model.User
import com.cygnus.view.AddClassDialog
import com.cygnus.view.SchoolClassView
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_list.*

class SchoolClassesActivity : DashboardChildActivity() {

    private val teachers: ArrayList<String> = ArrayList()
    private val classes: ArrayList<SchoolClass> = ArrayList()

    private lateinit var adapter: SchoolClassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Read staff list from intent
        val invites =
                intent.getParcelableArrayListExtra<Invite>(CygnusApp.EXTRA_INVITES)
        if (invites == null) {
            finish()
            return
        }
        invites.forEach { teachers.add(it.invitee) }

        adapter = SchoolClassAdapter(this, classes)
        adapter.sort { o1, o2 ->
            (o1 as SchoolClass).name.compareTo((o2 as SchoolClass).name)
        }
        contentList.adapter = adapter

        addButton.setOnClickListener { onAddClassClicked() }
    }

    override fun updateUI(currentUser: User) {
        ClassesDao.getClassesAtSchool(schoolId, OnSuccessListener {
            it?.let {
                classes.clear()
                classes.addAll(it)
                classes.sortBy { schoolClass -> schoolClass.name }
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun onAddClassClicked() {
        val dialog = AddClassDialog.newInstance(teachers, currentUser.id)
        dialog.onOkListener = { schoolClass ->
            if (schoolClass.teacherId.isNotBlank()) {
                classes.filter { it.teacherId == schoolClass.teacherId }
                        .forEach { it.teacherId = "" }
            }
            classes.add(schoolClass)
            adapter.notifyDataSetChanged()
        }
        dialog.show(supportFragmentManager, dialog.toString())
    }

    private inner class SchoolClassAdapter(context: Context, val classes: ArrayList<SchoolClass>)
        : ModelViewAdapter<SchoolClass>(context, classes, SchoolClassView::class) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                val dialog = AddClassDialog.newInstance(teachers,
                        currentUser.id, classes[position])
                dialog.onOkListener = { schoolClass ->
                    if (schoolClass.teacherId.isNotBlank()) {
                        classes.filter { it.teacherId == schoolClass.teacherId }
                                .forEach { it.teacherId = "" }
                    }
                    classes[position] = schoolClass
                    notifyDataSetChanged()
                }
                dialog.show(supportFragmentManager, dialog.toString())
            }
            return v
        }

    }

}