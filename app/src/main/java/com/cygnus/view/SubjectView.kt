package com.cygnus.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.dao.UsersDao
import com.cygnus.model.Subject
import com.google.android.gms.tasks.OnSuccessListener

class SubjectView : BaseView<Subject> {
    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
    lateinit var schoolid: String

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var model: Subject? = null

    private val subjectColor: View
    private val subjectName: TextView
    private val subjectClass: TextView
    private val subjectTeacher: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_subject, this)
        subjectColor = findViewById(R.id.subjectColor)
        subjectName = findViewById(R.id.subjectName)
        subjectClass = findViewById(R.id.subjectClass)
        subjectTeacher = findViewById(R.id.subjectTeacher)
        sp_loginsave = context.getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()
        schoolid=sp_loginsave.getString("studentschoolid","").toString()

    }

    override fun updateView(model: Subject) {
        this.model = model
        subjectName.text = model.name
        subjectClass.text = model.classId
        UsersDao.getUserByEmail(schoolid, model.teacherId, OnSuccessListener { user ->
            subjectTeacher.text = user?.name ?: model.teacherId
        })
       // subjectTeacher.text = model.teacherId
        subjectColor.setBackgroundColor(convertToColor(model))
    }

    fun updateWithSchool(schoolId: String) {
       /* this.model = model
        subjectName.text = model!!.name
        subjectClass.text = model!!.classId
        UsersDao.getUserByEmail(schoolid, model!!.teacherId, OnSuccessListener { user ->
            subjectTeacher.text = user?.name ?: model!!.teacherId
        })
        // subjectTeacher.text = model.teacherId
        subjectColor.setBackgroundColor(convertToColor(model!!))*/
        /*model?.let {
//            subjectTeacher.text = ""
            UsersDao.getUserByEmail(schoolId, it.teacherId, OnSuccessListener { user ->
                subjectTeacher.text = user?.name ?: it.teacherId
            })
        }*/
    }

    fun setSubjectTeacherVisible(visible: Boolean) {
        subjectTeacher.visibility =  View.VISIBLE
      //  subjectTeacher.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setSubjectClassVisible(visible: Boolean) {
//        subjectClass.visibility = if (visible) View.VISIBLE else View.GONE
        subjectClass.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @SuppressLint("NewApi")
    private fun convertToColor(o: Any): Int {
        return try {
            val i = o.hashCode()
            Color.parseColor("#FF" + Integer.toHexString(i shr 16 and 0xFF) +
                    Integer.toHexString(i shr 8 and 0xFF) +
                    Integer.toHexString(i and 0xFF))
        } catch (ignored: Exception) {
            context.getColor(R.color.colorAccent)
        }
    }

}