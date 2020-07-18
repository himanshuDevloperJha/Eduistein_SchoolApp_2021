package com.cygnus

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.adapter.PeopleAdapter
import com.cygnus.core.DashboardActivity
import com.cygnus.dao.ClassesDao
import com.cygnus.dao.SubjectsDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.*
import com.cygnus.timetable.TimetablePagerAdapter
import com.cygnus.view.SubjectView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dashboard_teacher.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * TeacherDashboardActivity is the teachers' homepage.
 *
 * This is the dashboard which is first displayed when a [Teacher]
 * user signs into the app. All actions for class teachers and
 * other teachers are defined in this activity.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class TeacherDashboardActivity : DashboardActivity() {
    var classes: ArrayList<String> = ArrayList()
    private lateinit var currentTeacher: Teacher
    private var assignedClass: SchoolClass? = null
    var cls_teacherid: String? = null
    var subject_teacherid: String? = null
    var teacheremail: String? = null
    private lateinit var cal: Calendar
    private var day = 0
    private var month = 0
    private var year = 0
    var cls: String? = null
    var selectedItem: String? = null
    var chapterlist: ArrayList<String> = ArrayList()
    var chsplist: ArrayList<ChapterSpinner> = ArrayList()
    var chapternolist: ArrayList<String> = ArrayList()
    var selectedchapter:String?=null
//    var selected_chno:String?=null
//    var selected_chname:String?=null
    var yourArray: List<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_teacher)
        setSupportActionBar(toolbar)
        supportActionBar?.title = school

        // Only allow a signed in teacher to access this page
        currentTeacher = when (currentUser) {
            is Teacher -> currentUser as Teacher
            else -> {
                finish()
                return
            }
        }


        teacheremail = currentUser.credentials.email
        Log.e("currentemail", teacheremail)

        cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)

        // Set up click listeners
        attendanceButton.setOnClickListener { startSecurely(MarkAttendanceActivity::class.java) }
        classAnnouncementsButton.setOnClickListener {
            val posts = ArrayList<NoticeBoardPost>()
            assignedClass?.notices?.values?.let { posts.addAll(it) }
            startSecurely(NoticeActivity::class.java, Intent().apply {
                putParcelableArrayListExtra(CygnusApp.EXTRA_NOTICE_POSTS, posts)
            })
        }

        classyoutube.setOnClickListener {
            val dialog1 = Dialog(this)
            dialog1.setContentView(R.layout.custom_teacher_youtube_videos)
            dialog1.show()

            val btn_yt_save = dialog1.findViewById(R.id.btn_yt_save) as MaterialButton
            val class_st_yt_subject = dialog1.findViewById(R.id.class_st_yt_subject) as AppCompatAutoCompleteTextView
            val class_st_yt_class = dialog1.findViewById(R.id.class_st_yt_class) as AppCompatAutoCompleteTextView
            var class_st_yt_units = dialog1.findViewById(R.id.class_st_yt_units) as AppCompatAutoCompleteTextView
          //  val class_st_yt_unitsno = dialog1.findViewById(R.id.class_st_yt_unitsno) as AppCompatAutoCompleteTextView
            val class_st_yt_title = dialog1.findViewById(R.id.class_st_yt_title) as TextInputEditText
            val class_st_yt_youtubelink = dialog1.findViewById(R.id.class_st_yt_youtubelink) as TextInputEditText

            val clslist: MutableList<String> = mutableListOf()
            val subjectlist: MutableList<String> = mutableListOf()
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val rootRef = FirebaseDatabase.getInstance().reference
            val uidRef = rootRef.child("eq32pznJQ6MgHFu2jvyFSTmRBY72").child("classes")

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.getChildren()) {
                        //  Log.e("msggg1",""+ ds.getKey()); //displays the key for the node
                        cls = ds.key.toString()

                        cls_teacherid = ds.child("teacherId").getValue().toString()

                        if (cls_teacherid.equals(teacheremail)) {
                            //display classname
                            var classname = ds.child("name").getValue()
                            clslist.add(classname.toString())


                            //for subjects
                            val uidRef1 = rootRef.child("eq32pznJQ6MgHFu2jvyFSTmRBY72").child("classes").child(ds.key.toString()).child("subjects")

                            val valueEventListener1 = object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (ds1 in dataSnapshot.getChildren()) {
                                        subject_teacherid = ds1.child("teacherId").getValue().toString()
                                        Log.e("msgnew4", subject_teacherid)

                                        if (subject_teacherid.equals(teacheremail)) {
                                            subjectlist.add(ds1.child("name").getValue().toString())

                                            class_st_yt_subject.setAdapter(ArrayAdapter(applicationContext,
                                                    android.R.layout.select_dialog_item, subjectlist))

                                        } else {

                                        }

                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.d("msg", databaseError.message) //Don't ignore errors!
                                }
                            }
                            uidRef1.addListenerForSingleValueEvent(valueEventListener1)
                            //  var subjects: Subject? = ds.child("subjects").getValue(Subject::class.java)
                            class_st_yt_class.setAdapter(ArrayAdapter(applicationContext,
                                    android.R.layout.select_dialog_item, clslist))
                        } else {
                        }

                    }
                    //Log.e("msggg3",""+ clslist.size);   //gives the value for given keyname

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("msg", databaseError.message) //Don't ignore errors!
                }
            }
            uidRef.addListenerForSingleValueEvent(valueEventListener)

            class_st_yt_subject.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                selectedItem = class_st_yt_subject.getAdapter().getItem(position).toString()
                val rootRef = FirebaseDatabase.getInstance().reference
                val uidRef = rootRef.child("eq32pznJQ6MgHFu2jvyFSTmRBY72").child("classes")

                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.getChildren()) {
                            cls_teacherid = ds.child("teacherId").getValue().toString()

                            if (cls_teacherid.equals(teacheremail)) {
                                //for subjects
                                val uidRef1 = rootRef.child("eq32pznJQ6MgHFu2jvyFSTmRBY72").child("classes").child(ds.key.toString()).child("subjects")

                                val valueEventListener1 = object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (ds1 in dataSnapshot.getChildren()) {

                                            val uidRef2 = rootRef.child("eq32pznJQ6MgHFu2jvyFSTmRBY72").child("classes").child(ds.key.toString()).child("subjects").child(ds1.key.toString()).child("Chapters")
                                            chapterlist.clear()
                                            chapternolist.clear()
                                            chsplist.clear()

                                            val valueEventListener2 = object : ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                                    for (ds2 in dataSnapshot.getChildren()) {
                                                        if (ds1.key.toString().equals(selectedItem)) {
                                                            Log.e("Chapterss1112", ds2.key + " = " + ds2.getValue().toString())
                                                            chapterlist.add(ds2.getValue().toString())
                                                            chapternolist.add(ds2.key.toString())
                                                            var cs= ChapterSpinner(ds2.key.toString(),ds2.getValue().toString())
                                                            chsplist.add(cs)
                                                        }

                                                    }

                                                    Log.e("Chapterss111", chapterlist.size.toString())

                                                    /*val customDropDownAdapter = CustomDropDownAdapter(applicationContext, chsplist)
                                                    class_st_yt_units.adapter = customDropDownAdapter

                                                    val ch_adapter = ArrayAdapter(applicationContext,
                                                             R.layout.custom_spinner_sub,chsplist.get(position).name+"-"+chsplist.get(position).no) // where array_name consists of the items to show in Spinner
                                                    ch_adapter.setDropDownViewResource(R.layout.custom_spinner_sub)*/

                                                  val peopleAdapter =  PeopleAdapter(applicationContext,
                                                          R.layout.custom_teacher_youtube_videos, R.id.tv_chh, chsplist);
                                                    class_st_yt_units.setAdapter(peopleAdapter);

                                                    class_st_yt_units.setOnItemClickListener { adapterView, view, i, l ->
                                                    selectedchapter = peopleAdapter.getItem(i)!!.no+"-"+peopleAdapter.getItem(i)!!.name
                                                      //  Log.e("msg","UNITSSS"+adapterView.getItemAtPosition(i))
                                                        class_st_yt_units.setText(selectedchapter)
                                                         yourArray = selectedchapter!!.split("-")
                                                      //  selected_chno = yourArray[0]
                                                       // selected_chname = yourArray[1]
                                                         Log.e("msg","UNITSSS"+yourArray[0]+"----"+yourArray[1])


                                                    }
//                                                    class_st_yt_units.setAdapter(ArrayAdapter(applicationContext,
//                                                            android.R.layout.select_dialog_item, chapterlist))
//                                                    class_st_yt_unitsno.setAdapter(ArrayAdapter(applicationContext,
//                                                            android.R.layout.select_dialog_item, chapternolist))
                                                }


                                                override fun onCancelled(databaseError: DatabaseError) {
                                                    Log.d("msg", databaseError.message) //Don't ignore errors!
                                                }
                                            }
                                            uidRef2.addListenerForSingleValueEvent(valueEventListener2)


                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.d("msg", databaseError.message) //Don't ignore errors!
                                    }
                                }
                                uidRef1.addListenerForSingleValueEvent(valueEventListener1)
                            } else {
                            }

                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("msg", databaseError.message) //Don't ignore errors!
                    }
                }
                uidRef.addListenerForSingleValueEvent(valueEventListener)

            })

            btn_yt_save.setOnClickListener {
                if (class_st_yt_subject.text!!.isEmpty() &&
                        class_st_yt_class.text!!.isEmpty() && class_st_yt_title.text!!.isEmpty()
                        && class_st_yt_youtubelink.text!!.isEmpty()) {

                    class_st_yt_subject.setError("Enter subject")
                    class_st_yt_class.setError("Enter class name")
                    class_st_yt_title.setError("Enter youtube title")
                    class_st_yt_youtubelink.setError("Enter youtube url")
                    //Toast.makeText(this,"Please fill mandatory fields",Toast.LENGTH_SHORT).show()
                } else if (class_st_yt_subject.text!!.isEmpty()) {
                    class_st_yt_subject.setError("Enter subject")

                } else if (class_st_yt_class.text!!.isEmpty()) {
                    class_st_yt_class.setError("Enter class name")
                } else if (class_st_yt_title.text!!.isEmpty()) {
                    class_st_yt_title.setError("Enter youtube title")
                } else if (class_st_yt_youtubelink.text!!.isEmpty()) {
                    class_st_yt_youtubelink.setError("Enter youtube url")
                } else {
//                 val uid = FirebaseAuth.getInstance().currentUser!!.uid
//                 val rootRef = FirebaseDatabase.getInstance().reference
//                 val key = rootRef.child("scheduleclass").push()

                    val cn = Chapternumbers(yourArray[1].toString(),
                            class_st_yt_title.text.toString(),
                            class_st_yt_youtubelink.text.toString())


                    val post = YoutubeVideos(teacheremail, class_st_yt_class.text.toString(),
                            class_st_yt_subject.text.toString());

//                 val childUpdates = hashMapOf<String, Any>(
//                         "/youtubevideos/" to post
//                 )
//                 Log.e("msgnew45",childUpdates.toString())
//
//                 rootRef.updateChildren(childUpdates)

//
                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child("youtubevideos")
                                    .child(class_st_yt_subject.text.toString()).push()
                    missionsReference.setValue(post)


//                    val childUpdates1 = hashMapOf<String, Any>(
//                         "/Chapters/1/" to cn)
//                    missionsReference.setValue(childUpdates1);

//                    val missionsReference1= missionsReference.child("Chapters")
//                    val map2: HashMap<String, Chapternumbers> = HashMap()
//                  map2.put(class_st_yt_unitsno.text.toString(),cn)
//                    missionsReference1.setValue(map2);


                    // val sdf: SimpleDateFormat  =  SimpleDateFormat("yyyyMMdd G HH:mm:ss z")
                    // val currenttime:String  = sdf.format(Date());

                    val missionsReference2 = missionsReference.child("Chapters")

                    missionsReference2.child(yourArray[0]).setValue(cn);




                    Toast.makeText(this, "Video has been uploaded", Toast.LENGTH_SHORT).show()
                    class_st_yt_class.setText("")
                    class_st_yt_subject.setText("")
                    class_st_yt_title.setText("")
                    class_st_yt_units.setText("")
                    class_st_yt_youtubelink.setText("")
                    dialog1.dismiss()
                }
            }

        }

        classscheduleButton.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.custom_teacher_schedule_clas)
            dialog.show()

            val btn_schedulesave = dialog.findViewById(R.id.btn_schedulesave) as MaterialButton
            //    val class_title=dialog.findViewById(R.id.class_title) as TextInputEditText
            val class_subject = dialog.findViewById(R.id.class_subject) as AppCompatAutoCompleteTextView
            val class_name = dialog.findViewById(R.id.class_name) as AppCompatAutoCompleteTextView
            val class_zoomlink = dialog.findViewById(R.id.class_zoomlink) as TextInputEditText
            val class_starttime = dialog.findViewById(R.id.class_starttime) as TextInputEditText
            val class_endtime = dialog.findViewById(R.id.class_endtime) as TextInputEditText
            val class_date = dialog.findViewById(R.id.class_date) as TextInputEditText


            val clslist: MutableList<String> = mutableListOf()
            val subjectlist: MutableList<String> = mutableListOf()
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val rootRef = FirebaseDatabase.getInstance().reference
            val uidRef = rootRef.child("eq32pznJQ6MgHFu2jvyFSTmRBY72").child("classes")

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.getChildren()) {
                        //  Log.e("msggg1",""+ ds.getKey()); //displays the key for the node

                        cls_teacherid = ds.child("teacherId").getValue().toString()

                        if (cls_teacherid.equals(teacheremail)) {
                            //display classname
                            var classname = ds.child("name").getValue()
                            clslist.add(classname.toString())


                            //for subjects
                            val uidRef1 = rootRef.child("eq32pznJQ6MgHFu2jvyFSTmRBY72").child("classes").child(ds.key.toString()).child("subjects")

                            val valueEventListener1 = object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (ds in dataSnapshot.getChildren()) {
                                        subject_teacherid = ds.child("teacherId").getValue().toString()
                                        Log.e("msgnew4", subject_teacherid)

                                        if (subject_teacherid.equals(teacheremail)) {
                                            subjectlist.add(ds.child("name").getValue().toString())

                                            class_subject.setAdapter(ArrayAdapter(applicationContext,
                                                    android.R.layout.select_dialog_item, subjectlist))

                                        } else {

                                        }
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.d("msg", databaseError.message) //Don't ignore errors!
                                }
                            }
                            uidRef1.addListenerForSingleValueEvent(valueEventListener1)
                            //  var subjects: Subject? = ds.child("subjects").getValue(Subject::class.java)
                            class_name.setAdapter(ArrayAdapter(applicationContext,
                                    android.R.layout.select_dialog_item, clslist))


                        } else {

                        }


                    }
                    //Log.e("msggg3",""+ clslist.size);   //gives the value for given keyname

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("msg", databaseError.message) //Don't ignore errors!
                }
            }
            uidRef.addListenerForSingleValueEvent(valueEventListener)

            btn_schedulesave.setOnClickListener {
                if (class_subject.text!!.isEmpty() &&
                        class_zoomlink.text!!.isEmpty() && class_name.text!!.isEmpty()
                        && class_date.text!!.isEmpty() &&
                        class_starttime.text!!.isEmpty() && class_endtime.text!!.isEmpty()) {

                    class_subject.setError("Enter subject")
                    class_name.setError("Enter class name")
                    class_zoomlink.setError("Enter zoom link")
                    class_date.setError("Enter date")
                    class_starttime.setError("Enter start time")
                    class_endtime.setError("Enter end time")
                    //Toast.makeText(this,"Please fill mandatory fields",Toast.LENGTH_SHORT).show()
                } else if (class_subject.text!!.isEmpty()) {
                    class_subject.setError("Enter subject")

                } else if (class_name.text!!.isEmpty()) {
                    class_name.setError("Enter class name")
                } else if (class_date.text!!.isEmpty()) {
                    class_date.setError("Enter date")
                } else if (class_starttime.text!!.isEmpty()) {
                    class_starttime.setError("Enter start time")
                } else if (class_endtime.text!!.isEmpty()) {
                    class_endtime.setError("Enter end time")
                } else if (class_zoomlink.text!!.isEmpty()) {
                    class_zoomlink.setError("Enter zoom link")

                } else {
//                 val uid = FirebaseAuth.getInstance().currentUser!!.uid
//                 val rootRef = FirebaseDatabase.getInstance().reference
//                 val key = rootRef.child("scheduleclass").push()


                    val post = Schedule(teacheremail, class_name.text.toString(), class_subject.text.toString(),
                            class_date.text.toString(), class_starttime.text.toString(),
                            class_endtime.text.toString(), class_zoomlink.text.toString())


//                 val childUpdates = hashMapOf<String, Any>(
//                         "/scheduleclass/" to post
//                 )
//                 Log.e("msgnew45",childUpdates.toString())
//
//                 rootRef.updateChildren(childUpdates)
                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child("scheduleclass").child(class_subject.text.toString())
                    missionsReference.setValue(post)

                    Toast.makeText(this, "Class is being scheduled successfully", Toast.LENGTH_SHORT).show()
                    class_name.setText("")
                    class_subject.setText("")
                    class_date.setText("")
                    class_starttime.setText("")
                    class_endtime.setText("")
                    class_zoomlink.setText("")
                    dialog.dismiss()
                }
            }

            class_date.setOnClickListener {
                val listener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val month = monthOfYear + 1
                    class_date?.setText("$dayOfMonth-$month-$year")
                }
                val dpDialog = DatePickerDialog(this, listener, year, month, day)
                dpDialog.show()
            }

            class_starttime.setOnClickListener {
                val mcurrentTime = Calendar.getInstance()
                val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
                val minute = mcurrentTime[Calendar.MINUTE]
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(this, OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    val calendar = Calendar.getInstance()
                    calendar[Calendar.HOUR_OF_DAY] = selectedHour
                    calendar[Calendar.MINUTE] = selectedMinute
                    calendar[Calendar.SECOND] = 0
                    class_starttime.setText("$selectedHour:$selectedMinute")
                }, hour, minute, false) //Yes 24 hour time

                mTimePicker.setTitle("Select Time")
                mTimePicker.show()
            }

            class_endtime.setOnClickListener {
                val mcurrentTime = Calendar.getInstance()
                val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
                val minute = mcurrentTime[Calendar.MINUTE]
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(this, OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    val calendar = Calendar.getInstance()
                    calendar[Calendar.HOUR_OF_DAY] = selectedHour
                    calendar[Calendar.MINUTE] = selectedMinute
                    calendar[Calendar.SECOND] = 0
                    class_endtime.setText("$selectedHour:$selectedMinute")
                }, hour, minute, false) //Yes 24 hour time

                mTimePicker.setTitle("Select Time")
                mTimePicker.show()
            }

            // startActivity(Intent(applicationContext, ScheduleClass::class.java))
        }

        manageStudentsButton.setOnClickListener { startSecurely(StudentsActivity::class.java) }

        if (currentTeacher.isClassTeacher()) {
            ClassesDao.getClassByTeacher(
                    schoolId,
                    currentTeacher.email,
                    OnSuccessListener {
                        assignedClass = it
                    })
        }
    }


    /**
     * Displays the signed in user's details.
     */
    override fun updateUI(currentUser: User) {
        getSubjectsList()
        if (currentTeacher.isClassTeacher()) {
            classTeacherCard.visibility = View.VISIBLE
            className.text = currentTeacher.classId
            getStudentCount()
        } else {
            classTeacherCard.visibility = View.GONE
        }
    }

    private fun getStudentCount() {
        onStudentCountReceived(0)
        currentTeacher.classId?.let { classId ->
            UsersDao.getStudentsInClass(schoolId, classId, OnSuccessListener {
                onStudentCountReceived(it.size)
            })
        }
    }

    private fun onStudentCountReceived(count: Int) {
        studentCount.text = String.format(getString(R.string.ph_student_count), count)
    }

    /**
     * Gets a list of courses from database taught by [currentTeacher].
     */
    private fun getSubjectsList() {
        SubjectsDao.getSubjectsByTeacher(schoolId, currentTeacher.email, OnSuccessListener {
            onSubjectsReceived(it)
        })
    }

    private fun onSubjectsReceived(subjects: List<Subject>) {
        coursesList.adapter = SubjectAdapter(this, subjects)

        timetableView.adapter = TimetablePagerAdapter(supportFragmentManager, subjects, true)
        timetableDay.setupWithViewPager(timetableView)

        var today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2
        if (today < 0) today = 7
        timetableDay.selectTab(timetableDay.getTabAt(today))
    }

    private inner class SubjectAdapter(context: Context, val subjects: List<Subject>)
        : ModelViewAdapter<Subject>(context, subjects, SubjectView::class) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                val subject = subjects[position]
                startSecurely(SubjectActivity::class.java, Intent().apply {
                    putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
                    putExtra(CygnusApp.EXTRA_EDITABLE_MODE, true)
                })
            }
            return v
        }

    }

}