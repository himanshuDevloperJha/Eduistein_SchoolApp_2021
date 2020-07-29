package com.cygnus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardActivity
import com.cygnus.dao.NoticeBoardDao
import com.cygnus.dao.SubjectsDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.NoticeBoardPost
import com.cygnus.model.Student
import com.cygnus.model.Subject
import com.cygnus.model.User
import com.cygnus.timetable.TimetablePagerAdapter
import com.cygnus.view.SubjectView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dashboard_student.*
import kotlinx.android.synthetic.main.activity_dashboard_student.toolbar
import kotlinx.android.synthetic.main.activity_school.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * StudentDashboardActivity is the students' homepage.
 *
 * This is the dashboard which is first displayed when a [Student]
 * user signs into the app. All actions for students are defined
 * in this activity.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class StudentDashboardActivity : DashboardActivity() {

    var subjectlist: MutableList<String> = mutableListOf()
    private lateinit var currentStudent: Student
    private var classPosts = ArrayList<NoticeBoardPost>()
    private  var subjectNames= ArrayList<String>()
    var teacherid:String?=null
    var standard:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_student)
        setSupportActionBar(toolbar)
        supportActionBar?.title = school

        // Only allow a signed in teacher to access this page
        currentStudent = when (currentUser) {
            is Student -> currentUser as Student
            else -> {
                finish()
                return
            }
        }
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef1 = rootRef.child(schoolId)
                .child("classes")

        val valueEventListener1 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {

                    val c = ds.child("name").getValue().toString()
                    if (currentStudent.classId.equals(c)) {
                        standard = ds.child("standard").getValue().toString()
                        Log.e("msg","msggg333333"+ standard);   //gives the value for given keyname
                    }

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("msg", databaseError.message) //Don't ignore errors!
            }
        }
        uidRef1.addListenerForSingleValueEvent(valueEventListener1)

        attendanceButton.setOnClickListener { startSecurely(AttendanceActivity::class.java) }
        classAnnouncementsButton.setOnClickListener {
            startSecurely(NoticeActivity::class.java, Intent().apply {
                putParcelableArrayListExtra(CygnusApp.EXTRA_NOTICE_POSTS, classPosts)
            })
        }
        class_yt_myvideos.setOnClickListener {
            val intent = Intent(this,St_YoutubeVideos::class.java)
            intent.putExtra("user_classid",currentStudent.classId)
            intent.putExtra("user_teacherid",teacherid)
            intent.putExtra("user_schoolid",schoolId)
            intent.putExtra("user_standard",standard)
            startActivity(intent)
        }


        classschedule_student.setOnClickListener {
            val intent = Intent(this,St_ScheduledClass::class.java)
            intent.putExtra("username",currentUser.credentials.email)
            intent.putExtra("user_classid",currentStudent.classId)
            intent.putExtra("user_teacherid",teacherid)
            intent.putExtra("user_schoolid",schoolId)
            intent.putStringArrayListExtra("subjectlist", subjectNames as ArrayList<String>?)
            startActivity(intent)



        }

        NoticeBoardDao.getPostsByClass(
                schoolId,
                currentStudent.classId,
                OnSuccessListener {
                    classPosts = it
                })

        UsersDao.getTeacherByClass(schoolId, currentStudent.classId, OnSuccessListener {
            it?.let { teacher ->
                classTeacherName.text = teacher.name
                classTeacherEmail.text = teacher.email
                teacherid=teacher.email
            }
        })
    }

    /**
     * Displays the signed in user's details.
     */
    override fun updateUI(currentUser: User) {
        className.text = currentStudent.classId
        getSubjectsList()
    }

    /**
     * Gets a list of courses from database taught by [currentTeacher].
     */
    private fun getSubjectsList() {
        SubjectsDao.getSubjectsByClass(schoolId, currentStudent.classId, OnSuccessListener {
            onSubjectsReceived(it)
        })
    }

    private fun onSubjectsReceived(subjects: List<Subject>) {

        coursesList.adapter = SubjectAdapter(this, subjects)
        timetableView.adapter = TimetablePagerAdapter(supportFragmentManager, subjects, false)
        timetableDay.setupWithViewPager(timetableView)

        var today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2
        if (today < 0) today = 7
        timetableDay.selectTab(timetableDay.getTabAt(today))

        subjectNames = ArrayList<String>()
        subjects.forEach {
            subjectNames.add(it.name)
            Log.e("msg1111224", it.name)
        }
        gradesButton.setOnClickListener {
             subjectNames = ArrayList<String>()
            subjects.forEach {
                subjectNames.add(it.name)
              //  Log.e("msg1111224", it.name)
            }
            startSecurely(ReportCardActivity::class.java, Intent().apply {
                putStringArrayListExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subjectNames)
                putExtra(CygnusApp.EXTRA_STUDENT_CLASS_ID, currentStudent.classId)
            })
        }
    }

    private inner class SubjectAdapter(context: Context, val subjects: List<Subject>)
        : ModelViewAdapter<Subject>(context, subjects, SubjectView::class) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)

            //subjectlist.add(subjects[position].name)
          // Log.e("msg111122",subjects[position].name)
            v.setOnClickListener {
                val subject = subjects[position]
                startSecurely(SubjectActivity::class.java, Intent().apply {
                    putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
                })
            }

            (v as SubjectView).apply {
                updateWithSchool(schoolId)
                setSubjectTeacherVisible(true)
                setSubjectClassVisible(false)
            }
            return v
        }

    }

}