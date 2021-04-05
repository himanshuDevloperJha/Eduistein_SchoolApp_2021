package com.cygnus

import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import co.aspirasoft.adapter.ModelViewAdapter
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cygnus.chatstaff.StaffList
import com.cygnus.core.DashboardActivity
import com.cygnus.dao.ClassesDao
import com.cygnus.dao.SubjectsDao
import com.cygnus.dao.UsersDao
import com.cygnus.feed.FeedActivity
import com.cygnus.feed.PendingApprovalPosts
import com.cygnus.model.*
import com.cygnus.notifications.GCMRegistrationIntentService
import com.cygnus.timetable.TimetablePagerAdapter
import com.cygnus.view.AccountSwitcher
import com.cygnus.view.SubjectView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dashboard_teacher.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
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
    var tokenlist: ArrayList<String> = ArrayList()

    var chapternoolist: ArrayList<String> = ArrayList()
    var chsplist: ArrayList<ChapterSpinner> = ArrayList()
    // var chapternolist: ArrayList<String> = ArrayList()
    var selectedchapter: String? = null
    private val REQUEST_EXTERNAL_STORAGE = 1
    private var mRegistrationBroadcastReceiver: BroadcastReceiver? = null
    var token: String? = null
    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
    var uniquetokenlist: List<String>? = null
    lateinit var class_st_yt_subject: AutoCompleteTextView;
    lateinit var class_st_yt_class: AutoCompleteTextView;
    lateinit var class_st_yt_units: AutoCompleteTextView;
    lateinit var class_st_yt_unitsno: AutoCompleteTextView;
    lateinit var class_st_yt_title: TextInputEditText;
    lateinit var class_st_yt_youtubelink: TextInputEditText;
    var currentVersion=""
    var noticounter=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_teacher)
       // setSupportActionBar(toolbar)
       // supportActionBar?.title = school
        toolbar_schoolname.setText(school)

        ivt_profile.setOnClickListener {
            AccountSwitcher.Builder(this)
                    .setUser(currentUser)
                    .show()
        }
        ivt_notification.setOnClickListener {
            if(currentUser.type.equals("Student")){
                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
            }
            else if(currentUser.type.equals("Teacher")){
                val intent = Intent(this, NotificationActivity::class.java)
                intent.putExtra("studentname", currentUser.name)
                intent.putExtra("studentschoolid", schoolId)
                intent.putExtra("studentschool_namee", schoolDetails.second)
                intent.putExtra("studenttype","Teacher")
                startActivity(intent)
            }
        }

        forceUpdate()
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


        //fetch token

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        ed_loginsave.putString("SubjectTeacherName", currentTeacher.name)
        ed_loginsave.putString("SubjectTeacherClassId", currentTeacher.classId)
        ed_loginsave.putString("studentclassId", currentTeacher.classId)
        ed_loginsave.putString("studentschoolid", schoolId)
        ed_loginsave.putString("student_email", currentTeacher.email)
        ed_loginsave.putString("userrrtypee", "Teacher")
        ed_loginsave.commit()



        Log.e("msg", "SUBJECT-TEACHERNAMEIDD" + sp_loginsave.getString("SubjectTeacherClassId", ""))

        getSubjectsList()

        classTeacherCard.visibility = View.VISIBLE
        className.text = currentTeacher.classId
        getStudentCount()



//get notification count
        val  notireference = FirebaseDatabase.getInstance().reference.child(schoolId).child("TeacherNotifications")
        notireference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    if (datas.child("username").value.toString().equals(currentTeacher.name, ignoreCase = true)
                            && datas.child("status").value.toString().equals("unread")) {
                        noticounter++
                    }
                }
                if(noticounter>0){
                    tv_countnoti.visibility=View.VISIBLE
                    tv_countnoti.setText(noticounter.toString())
                }
             //   Toast.makeText(applicationContext,"Counter:"+noticounter,Toast.LENGTH_LONG).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

       /* if (currentTeacher.isClassTeacher()) {
            classTeacherCard.visibility = View.VISIBLE
            className.text = currentTeacher.classId
            getStudentCount()
        } else {
            classTeacherCard.visibility = View.VISIBLE
        }*/


        /* SchoolDao.getSchoolByUser(firebaseUser.uid, OnSuccessListener {
             it?.let { schoolDetails ->
                 onSchoolDetailsReceived(schoolDetails, firebaseUser)
             } ?: onFailure(null)
         })*/

        // Set up click listeners

      /*  if (ContextCompat.checkSelfPermission(applicationContext,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(applicationContext,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) { // Permission is not granted

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.SEND_SMS) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.SEND_SMS)) { // Show an explanation to the user *asynchronously* -- don't block

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS,
                        Manifest.permission.SEND_SMS), REQUEST_EXTERNAL_STORAGE)

            }
        }*/
        attendanceButton.setOnClickListener { startSecurely(MarkAttendanceActivity::class.java) }
        approvalpostss.setOnClickListener {
            val intent = Intent(this, PendingApprovalPosts::class.java)
            intent.putExtra("studentname", currentTeacher.name)
            intent.putExtra("studentschoolid", schoolId)
            intent.putExtra("teacheridd",currentTeacher.classId)
            intent.putExtra("studenttype","Teacher")

            startActivity(intent)
        }
       /* tchr_notifications.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            intent.putExtra("studentname", currentUser.name)
            intent.putExtra("studentschoolid", schoolId)
            intent.putExtra("studentschool_namee", schoolDetails.second)
            intent.putExtra("studenttype","Teacher")
            startActivity(intent)
        }*/
        addChapternoList()

        feedteacher.setOnClickListener {
            val intent = Intent(this, FeedActivity::class.java)
            intent.putExtra("studentname", currentUser.name)
            intent.putExtra("studentschoolid", schoolId)
            intent.putExtra("studentschool_namee", schoolDetails.second)
            intent.putExtra("student_teacheridd", currentTeacher.classId)
            intent.putExtra("studentclassId", currentTeacher.classId)
            intent.putExtra("studenttype","Teacher")
            startActivity(intent)
        }

        chatbutton.setOnClickListener {
            val intent = Intent(this, StaffList::class.java)
            intent.putExtra("studentname", currentUser.name)
            intent.putExtra("studentschoolid", schoolId)
            intent.putExtra("studentschool_namee", schoolDetails.second)
            intent.putExtra("student_teacheridd", currentTeacher.classId)
            intent.putExtra("userrtypeeee", "Teacher")
            intent.putExtra("studentclassId", currentTeacher.classId)
            startActivity(intent)

        }

        classyoutube.setOnClickListener {
            val dialog1 = Dialog(this)
            dialog1.setContentView(R.layout.custom_teacher_youtube_videos)
            dialog1.show()

            val btn_yt_save = dialog1.findViewById(R.id.btn_yt_save) as MaterialButton
            class_st_yt_subject = dialog1.findViewById(R.id.class_st_yt_subject) as AppCompatAutoCompleteTextView
            class_st_yt_class = dialog1.findViewById(R.id.class_st_yt_class) as AppCompatAutoCompleteTextView
            class_st_yt_units = dialog1.findViewById(R.id.class_st_yt_units) as AppCompatAutoCompleteTextView
            class_st_yt_unitsno = dialog1.findViewById(R.id.class_st_yt_unitsno) as AppCompatAutoCompleteTextView
            class_st_yt_title = dialog1.findViewById(R.id.class_st_yt_title) as TextInputEditText
            class_st_yt_youtubelink = dialog1.findViewById(R.id.class_st_yt_youtubelink) as TextInputEditText


            class_st_yt_unitsno.setAdapter(ArrayAdapter(applicationContext,
                    android.R.layout.select_dialog_item, chapternoolist));
            val clslist: MutableList<String> = mutableListOf()
            val subjectlist: MutableList<String> = mutableListOf()
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val rootRef = FirebaseDatabase.getInstance().reference


            val uidRef = rootRef.child(schoolId)
                    .child("classes")

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
                            val uidRef1 = rootRef.child(schoolId)
                                    .child("classes").child(ds.key.toString())
                                    .child("subjects")

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
                val uidRef = rootRef.child(schoolId).child("classes")

                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.getChildren()) {
                            cls_teacherid = ds.child("teacherId").getValue().toString()

                            if (cls_teacherid.equals(teacheremail)) {
                                //for subjects
                                val uidRef1 = rootRef
                                        .child(schoolId)
                                        .child("classes").child(ds.key.toString())
                                        .child("subjects")

                                val valueEventListener1 = object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (ds1 in dataSnapshot.getChildren()) {

                                            val uidRef2 = rootRef
                                                    .child(schoolId)
                                                    .child("classes").child(ds.key.toString())
                                                    .child("subjects").child(ds1.key.toString())
                                                    .child("Chapters")
                                            chapterlist.clear()
//                                            chsplist.clear()

                                            val valueEventListener2 = object : ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                                    for (ds2 in dataSnapshot.getChildren()) {
                                                        if (ds1.key.toString().equals(selectedItem)) {
                                                            Log.e("Chapterss1112", ds2.key + " = " + ds2.getValue().toString())
                                                            chapterlist.add(ds2.getValue().toString())
                                                            //chapternolist.add(ds2.key.toString())
                                                            // var cs = ChapterSpinner(ds2.key.toString(), ds2.getValue().toString())
                                                            // chsplist.add(cs)
                                                        }

                                                    }

                                                    Log.e("Chapterss111", chapterlist.size.toString())

                                                    /*val peopleAdapter = PeopleAdapter(applicationContext,
                                                            R.layout.custom_teacher_youtube_videos, R.id.tv_chh, chsplist);
                                                    class_st_yt_units.setAdapter(peopleAdapter);*/


                                                    class_st_yt_units.setAdapter(ArrayAdapter(applicationContext,
                                                            android.R.layout.select_dialog_item, chapterlist))

                                                    /* class_st_yt_units.setOnItemClickListener { adapterView, view, i, l ->
                                                        // selectedchapter = peopleAdapter.getItem(i)!!.no + "-" + peopleAdapter.getItem(i)!!.name
                                                         selectedchapter = peopleAdapter.getItem(i)!!.name
                                                         //  Log.e("msg","UNITSSS"+adapterView.getItemAtPosition(i))
                                                         class_st_yt_units.setText(selectedchapter)
                                                        // yourArray = selectedchapter!!.split("-")
                                                       //  Log.e("msg", "UNITSSS" + yourArray[0] + "----" + yourArray[1])


                                                     }*/
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
                tokenlist.clear()
                if (class_st_yt_subject.text!!.isEmpty() &&
                        class_st_yt_class.text!!.isEmpty() && class_st_yt_title.text!!.isEmpty()
                        && class_st_yt_youtubelink.text!!.isEmpty()
                        && class_st_yt_unitsno.text!!.isEmpty() && class_st_yt_units.text!!.isEmpty()) {

                    class_st_yt_subject.setError("Enter subject")
                    class_st_yt_class.setError("Enter class name")
                    class_st_yt_title.setError("Enter youtube title")
                    class_st_yt_youtubelink.setError("Enter youtube url")
                    class_st_yt_units.setError("Enter chapter name")
                    class_st_yt_unitsno.setError("Enter chapter no")
                    //Toast.makeText(this,"Please fill mandatory fields",Toast.LENGTH_SHORT).show()
                } else if (class_st_yt_subject.text!!.isEmpty()) {
                    class_st_yt_subject.setError("Enter subject")

                } else if (class_st_yt_class.text!!.isEmpty()) {
                    class_st_yt_class.setError("Enter class name")
                } else if (class_st_yt_title.text!!.isEmpty()) {
                    class_st_yt_title.setError("Enter youtube title")
                } else if (class_st_yt_youtubelink.text!!.isEmpty()) {
                    class_st_yt_youtubelink.setError("Enter youtube url")
                } else if (class_st_yt_units.text!!.isEmpty()) {
                    class_st_yt_units.setError("Enter chapter name")
                } else if (class_st_yt_unitsno.text!!.isEmpty()) {
                    class_st_yt_unitsno.setError("Enter chapter no")
                } else {
//                 val uid = FirebaseAuth.getInstance().currentUser!!.uid
//                 val rootRef = FirebaseDatabase.getInstance().reference
//                 val key = rootRef.child("scheduleclass").push()
                    val cn = Chapternumbers(class_st_yt_units.text.toString(),
                            class_st_yt_title.text.toString(),
                            class_st_yt_youtubelink.text.toString())


                    val post = YoutubeVideos(teacheremail, class_st_yt_class.text.toString(),
                            class_st_yt_subject.text.toString(), token)

                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child(schoolId).child("youtubevideos")
                                    .child(class_st_yt_subject.text.toString()).push()
                    missionsReference.setValue(post)

                    val missionsReference2 = missionsReference.child("Chapters")

                    missionsReference2.child(class_st_yt_unitsno.text.toString()).setValue(cn);


                    //subjectlist = getIntent().getStringArrayListExtra("subjectlist");

                    //get student tokens and add it to list for notifications
                    val reference: DatabaseReference
                    reference = FirebaseDatabase.getInstance().reference.child(schoolId).child("StudentTokens")
                    reference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            Log.e("msg", "TOKENCLASS11111" + class_st_yt_class.text.toString())
                            for (datas in dataSnapshot.children) {
                                //Log.e("msg", "TOKENCLASS" + datas.child("classId").value.toString())

                                if (class_st_yt_class.text.toString().equals(datas.child("classId").value.toString(), ignoreCase = true)) {
                                    try {
                                        val s = datas.key
                                        val studenttoken = datas.child("token").value.toString()
                                        tokenlist.add(studenttoken)


                                    } catch (e: Exception) {
                                        //Toast.makeText(applicationContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Toast.makeText(applicationContext, "No scheduled class", Toast.LENGTH_SHORT).show();
                                }
                            }

                            val body = "Hey! Your Subject Teacher " + currentTeacher.name + " has shared a Video on " + class_st_yt_title.text.toString() + ".\nClick to Watch Now !"
                            sendFCMPush(tokenlist, body);

                            Toast.makeText(applicationContext, "Video has been uploaded", Toast.LENGTH_SHORT).show()
                            class_st_yt_class.setText("")
                            class_st_yt_subject.setText("")
                            class_st_yt_title.setText("")
                            class_st_yt_units.setText("")
                            class_st_yt_youtubelink.setText("")
                            dialog1.dismiss()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            //Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    })


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
            val uidRef = rootRef.child(schoolId).child("classes")

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
                            val uidRef1 = rootRef.child(schoolId).child("classes").child(ds.key.toString()).child("subjects")

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
                tokenlist.clear()
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


                    val post = Schedule(teacheremail, class_name.text.toString(), class_subject.text.toString(),
                            class_date.text.toString(), class_starttime.text.toString(),
                            class_endtime.text.toString(), class_zoomlink.text.toString())

                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child(schoolId).child("scheduleclass")
                                    .child(class_subject.text.toString()).push()
                    missionsReference.setValue(post)


                    val reference: DatabaseReference
                    reference = FirebaseDatabase.getInstance().reference.child(schoolId).child("StudentTokens")
                    reference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (datas in dataSnapshot.children) {
                                Log.e("msg", "TOKENCLASS" + datas.child("classId").value.toString())
                                //Log.e("msg", "TOKENCLASS1" + datas.child("token").value.toString())
                                if (class_name.text.toString().equals(datas.child("classId").value.toString(), ignoreCase = true)) {
                                    try {
                                        val s = datas.key
                                        val studenttoken = datas.child("token").value.toString()
                                        tokenlist.add(studenttoken)


                                    } catch (e: Exception) {
                                        Toast.makeText(applicationContext, "" + e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Toast.makeText(applicationContext, "No scheduled class", Toast.LENGTH_SHORT).show();
                                }
                            }

                            val scheduledclassbody = "Hey! Your Subject Teacher " + currentTeacher.name +
                                    " has scheduled an Online class.\nClick to Check Now !"
                            sendFCMPush(tokenlist, scheduledclassbody);

                            Toast.makeText(applicationContext, "Class is being scheduled successfully", Toast.LENGTH_SHORT).show()
                            class_name.setText("")
                            class_subject.setText("")
                            class_date.setText("")
                            class_starttime.setText("")
                            class_endtime.setText("")
                            class_zoomlink.setText("")
                            dialog.dismiss()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            //Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    })


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

        manageStudentsButton.setOnClickListener {
            startSecurely(StudentsActivity::class.java)
        }
        classAnnouncementsButton.setOnClickListener {
            val posts = ArrayList<NoticeBoardPost>()
            assignedClass?.notices?.values?.let { posts.addAll(it) }
            startSecurely(NoticeActivity::class.java, Intent().apply {
                putParcelableArrayListExtra(CygnusApp.EXTRA_NOTICE_POSTS, posts)
            })
        }


        if (currentTeacher.isClassTeacher()) {
            ClassesDao.getClassByTeacher(
                    schoolId,
                    currentTeacher.email,
                    OnSuccessListener {
                        assignedClass = it
                    })
        }

        mRegistrationBroadcastReceiver = object : BroadcastReceiver() {
            //When the broadcast received
//We are sending the broadcast from GCMRegistrationIntentService
            override fun onReceive(context: Context, intent: Intent) { //If the broadcast has received with success
//that means device is registered successfully
                if (intent.action == GCMRegistrationIntentService.REGISTRATION_SUCCESS) { //Getting the registration token from the intent
                    token = intent.getStringExtra("token")
                    ed_loginsave.putString("SAVETOKEN", token)
                    ed_loginsave.commit()
                    Handler().postDelayed({
                        // Toast.makeText(applicationContext,token, Toast.LENGTH_LONG).show()

                        val post = TeacherToken(currentTeacher.name,currentTeacher.email, token)
                        val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolId).
                                        child("TeacherTokens").child(token.toString())

                        missionsReference.setValue(post)

                        val post1 = ChatTokens(currentTeacher.name,currentTeacher.email, token,"")
                        val missionsReference1 = FirebaseDatabase.getInstance().reference.child(schoolId).
                                child("ChatTokens").child(currentTeacher.name)

                        missionsReference1.setValue(post1)
                        Log.d("msg", "onReceiveToken: $token")
                        //if the intent is not with success then displaying error messages
                    }, 2000)
                } else if (intent.action == GCMRegistrationIntentService.REGISTRATION_ERROR) { // Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                }
            }
        }

        //Checking play service is available or not
        //Checking play service is available or not
        val resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(applicationContext)

        //if play service is not available
        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) { //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) { //Displaying message that play service is not installed
//  Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, applicationContext)
                //If play service is not supported
//Displaying an error message
            } else { // Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
            //If play service is available
        } else {
            //Starting intent to register device
            val itent = Intent(applicationContext, GCMRegistrationIntentService::class.java)
            startService(itent)
        }


    }
    public fun forceUpdate(){
        val packageManager: PackageManager = this.getPackageManager()
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(getPackageName(),0)
           // currentVersion  = packageInfo.versionName
            currentVersion  ="50+"
            ForceUpdateAsync(currentVersion,this).execute()
        } catch (e: PackageManager.NameNotFoundException ) {
            Log.e("msg","Crashhhhh:"+e.toString())
            e.printStackTrace()
        }

    }
    companion object {
        private var latestVersion: String? = null
    }
    class ForceUpdateAsync(private val currentVersion: String, private val context: Context) :
            AsyncTask<String?, String?, JSONObject>() {


        override fun onPostExecute(jsonObject: JSONObject) {
            if (latestVersion != null) {
                if (!currentVersion.equals(latestVersion, ignoreCase = true)) {
                    val dialogupdate = Dialog(context)
                    dialogupdate.setContentView(R.layout.dialog_updateinfo)
                    dialogupdate.show()
                    //  dialogupdate.setCancelable(false)
                    // dialogupdate.setCanceledOnTouchOutside(false)
                    val tv_updateinfo = dialogupdate.findViewById(R.id.tv_updateinfo) as TextView
                    tv_updateinfo.setOnClickListener {
                        context.startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + context.packageName)))
                        dialogupdate.dismiss()
                    }

                    // Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();
/*  if(!(context instanceof SchoolDashboardActivity)) {
                    if(!((Activity)context).isFinishing()){

                    }
                }*/
                }
            }
            super.onPostExecute(jsonObject)
        }


        override fun doInBackground(vararg params: String?): JSONObject {
            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.packageName + "&hl=en")
                        .timeout(50000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(3) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText()
                Log.e("msg","latestversionNo: " +latestVersion+" , Current Version: "+currentVersion)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return JSONObject()
        }

    }
    private fun sendFCMPush(tokenlist: ArrayList<String>, bodyy: String) {
        val SERVER_KEY = "AAAAav-QFhw:APA91bG7ChbWR2kwz_FBMKgaDV8IZ_PMmED0Rp_sy7f0PtlZm37t-uAJRnUwyLYSM4Z-kSg_Jj9Xv9O8x4r_L5iQC9JAKhhTPt-ga5nmEqCBMcqgaUMtDnF5ponwXi8mD31k481DWHoF"
        var obj: JSONObject? = null
        var objData: JSONObject? = null
        var dataobjData: JSONObject? = null
        val jsonArray = JSONArray()

        var regId: JSONArray? = null;


        try {
            obj = JSONObject()
            objData = JSONObject()
            regId = JSONArray()

            for (item in tokenlist) {
                regId.put(item);
            }

            objData.put("body", bodyy)
            objData.put("title", "Eduistein")
            objData.put("sound", "default")
            objData.put("icon", "icon_name") //   icon_name
            // objData.put("tag", token)
            objData.put("priority", "high")
            dataobjData = JSONObject()
            dataobjData.put("title", "Eduistein")
            dataobjData.put("text", bodyy)

            // obj.put("to", token)
            obj.put("registration_ids", regId);
            obj.put("notification", objData)
            obj.put("data", dataobjData)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsObjRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
                "https://fcm.googleapis.com/fcm/send", obj,
                Response.Listener { response ->
                    Log.e("msg", "onResponse111111: $response")
                    val post = StoreNotifications(currentTeacher.name,currentTeacher.classId, bodyy,"unread");

                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child(schoolId).
                                    child("Notifications").push()

                    missionsReference.setValue(post)
                    //Toast.makeText(applicationContext, "1:" + response.toString(), Toast.LENGTH_SHORT).show()


                },
                Response.ErrorListener { error ->
                    Log.e("msg", "onResponse1111112: $error")
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "key=$SERVER_KEY"
                params["Content-Type"] = "application/json"
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsObjRequest)
    }

    override fun onResume() {
        super.onResume()
        Log.w("MainActivity", "onResume")
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver!!,
                IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS))
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver!!,
                IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR))
    }


    //Unregistering receiver on activity paused
    override fun onPause() {
        super.onPause()
        Log.w("MainActivity", "onPause")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver!!)
    }


    private fun addChapternoList() {
        chapternoolist.add("Chapter 1")
        chapternoolist.add("Chapter 2")
        chapternoolist.add("Chapter 3")
        chapternoolist.add("Chapter 4")
        chapternoolist.add("Chapter 5")
        chapternoolist.add("Chapter 6")
        chapternoolist.add("Chapter 7")
        chapternoolist.add("Chapter 8")
        chapternoolist.add("Chapter 9")
        chapternoolist.add("Chapter 10")
        chapternoolist.add("Chapter 11")
        chapternoolist.add("Chapter 12")
        chapternoolist.add("Chapter 13")
        chapternoolist.add("Chapter 14")
        chapternoolist.add("Chapter 15")
        chapternoolist.add("Chapter 16")
        chapternoolist.add("Chapter 17")
        chapternoolist.add("Chapter 18")
        chapternoolist.add("Chapter 19")
        chapternoolist.add("Chapter 20")
        chapternoolist.add("Chapter 21")
        chapternoolist.add("Chapter 22")
        chapternoolist.add("Chapter 23")
        chapternoolist.add("Chapter 24")
        chapternoolist.add("Chapter 25")
        chapternoolist.add("Chapter 26")
        chapternoolist.add("Chapter 27")
        chapternoolist.add("Chapter 28")
        chapternoolist.add("Chapter 29")
        chapternoolist.add("Chapter 30")
        chapternoolist.add("Chapter 31")
        chapternoolist.add("Chapter 32")
        chapternoolist.add("Chapter 33")
        chapternoolist.add("Chapter 34")
        chapternoolist.add("Chapter 35")
        chapternoolist.add("Chapter 36")
        chapternoolist.add("Chapter 37")
        chapternoolist.add("Chapter 38")
        chapternoolist.add("Chapter 39")
        chapternoolist.add("Chapter 40")
        chapternoolist.add("Chapter 41")
        chapternoolist.add("Chapter 42")
        chapternoolist.add("Chapter 43")
        chapternoolist.add("Chapter 44")
        chapternoolist.add("Chapter 45")
        chapternoolist.add("Chapter 46")
        chapternoolist.add("Chapter 47")
        chapternoolist.add("Chapter 48")
        chapternoolist.add("Chapter 49")
        chapternoolist.add("Chapter 50")
        chapternoolist.add("Chapter 51")
        chapternoolist.add("Chapter 52")
        chapternoolist.add("Chapter 53")
        chapternoolist.add("Chapter 54")
        chapternoolist.add("Chapter 55")
        chapternoolist.add("Chapter 56")
        chapternoolist.add("Chapter 57")
        chapternoolist.add("Chapter 58")
        chapternoolist.add("Chapter 59")
        chapternoolist.add("Chapter 60")
        chapternoolist.add("Chapter 61")
        chapternoolist.add("Chapter 62")
        chapternoolist.add("Chapter 63")
        chapternoolist.add("Chapter 64")
        chapternoolist.add("Chapter 65")
        chapternoolist.add("Chapter 66")
        chapternoolist.add("Chapter 67")
        chapternoolist.add("Chapter 68")
        chapternoolist.add("Chapter 69")
        chapternoolist.add("Chapter 70")
        chapternoolist.add("Chapter 71")
        chapternoolist.add("Chapter 72")
        chapternoolist.add("Chapter 73")
        chapternoolist.add("Chapter 74")
        chapternoolist.add("Chapter 75")
        chapternoolist.add("Chapter 76")
        chapternoolist.add("Chapter 77")
        chapternoolist.add("Chapter 78")
        chapternoolist.add("Chapter 79")
        chapternoolist.add("Chapter 80")
        chapternoolist.add("Chapter 81")
        chapternoolist.add("Chapter 82")
        chapternoolist.add("Chapter 83")
        chapternoolist.add("Chapter 84")
        chapternoolist.add("Chapter 85")
        chapternoolist.add("Chapter 86")
        chapternoolist.add("Chapter 87")
        chapternoolist.add("Chapter 88")
        chapternoolist.add("Chapter 89")
        chapternoolist.add("Chapter 90")
        chapternoolist.add("Chapter 91")
        chapternoolist.add("Chapter 92")
        chapternoolist.add("Chapter 93")
        chapternoolist.add("Chapter 94")
        chapternoolist.add("Chapter 95")
        chapternoolist.add("Chapter 96")
        chapternoolist.add("Chapter 97")
        chapternoolist.add("Chapter 98")
        chapternoolist.add("Chapter 99")
        chapternoolist.add("Chapter 100")
    }


    /**
     * Displays the signed in user's details.
     */
    override fun updateUI(currentUser: User) {

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

    override fun onRestart() {
        super.onRestart()
        noticounter=0
        val  notireference = FirebaseDatabase.getInstance().reference.child(schoolId).child("TeacherNotifications")
        notireference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    if (datas.child("username").value.toString().equals(currentTeacher.name, ignoreCase = true)
                            && datas.child("status").value.toString().equals("unread")) {
                        noticounter++
                    }
                }
                if(noticounter>0){
                    tv_countnoti.visibility=View.VISIBLE
                    tv_countnoti.setText(noticounter.toString())
                }
                else{
                    tv_countnoti.visibility=View.GONE

                }
                //   Toast.makeText(applicationContext,"Counter:"+noticounter,Toast.LENGTH_LONG).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        if(sp_loginsave.getString("notice_back","").equals("true")){
          //  val intent = getIntent()
          //  finish()
          //  startActivity(intent)

            if (currentTeacher.isClassTeacher()) {
                ClassesDao.getClassByTeacher(
                        schoolId,
                        currentTeacher.email,
                        OnSuccessListener {
                            assignedClass = it
                        })
            }
            ed_loginsave.putString("notice_back","false")
            ed_loginsave.commit()
        }
        else{

        }

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
                    putExtra("user_name", currentTeacher.name)
                    putExtra("user_tokennn", token)
                    putExtra("subjectTeacher_namee", subjects[position].teacherId)
                    putExtra("userrtypeeee", "Teacher")
                })
            }
            return v
        }

    }

}