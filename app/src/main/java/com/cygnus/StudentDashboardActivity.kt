package com.cygnus

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.chatstaff.Chat
import com.cygnus.chatstaff.UserDetails
import com.cygnus.core.DashboardActivity
import com.cygnus.coursefeature.SingleCourseActivity
import com.cygnus.dao.NoticeBoardDao
import com.cygnus.dao.SubjectsDao
import com.cygnus.dao.UsersDao
import com.cygnus.feed.FeedActivity
import com.cygnus.feesmanage.PayFees
import com.cygnus.model.*
import com.cygnus.notifications.AlarmReceiverQuizz
import com.cygnus.notifications.GCMRegistrationIntentService
import com.cygnus.notifications.ShowNotification
import com.cygnus.quiz.StartQuizActivity
import com.cygnus.timetable.TimetablePagerAdapter
import com.cygnus.view.SubjectView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.*
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_dashboard_student.*
import kotlinx.android.synthetic.main.view_subject.*
import org.json.JSONObject
import java.text.SimpleDateFormat
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
class StudentDashboardActivity : DashboardActivity(), PaymentResultListener {

    var subjectlist: MutableList<String> = mutableListOf()
    private lateinit var currentStudent: Student
    public var classPosts = ArrayList<NoticeBoardPost>()
    public var coursenamelist = ArrayList<String>()
    private var subjectNames = ArrayList<String>()
    var teacherid: String? = null
    var teachername: String? = null
    var standard: String? = null
    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
    private var mRegistrationBroadcastReceiver: BroadcastReceiver? = null
    var token: String? = null
    var reminderstatus: Boolean = false
    lateinit var reference2: DatabaseReference
    lateinit var reference5: DatabaseReference
    var usertotalpoints = 0


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

        if (currentStudent.classId.contains("NURSERY", ignoreCase = true)) {
            standard = "Class Nursery"
        } else if (currentStudent.classId.contains("LKG", ignoreCase = true)) {
            standard = "Class LKG"
        } else if (currentStudent.classId.contains("UKG", ignoreCase = true)) {
            standard = "Class UKG"

        } else if (currentStudent.classId.contains("1", ignoreCase = true)) {
            standard = "Class 1st"
        } else if (currentStudent.classId.contains("2", ignoreCase = true)) {
            standard = "Class 2nd"

        } else if (currentStudent.classId.contains("3", ignoreCase = true)) {
            standard = "Class 3rd"

        } else if (currentStudent.classId.contains("4", ignoreCase = true)) {
            standard = "Class 4th"

        } else if (currentStudent.classId.contains("5", ignoreCase = true)) {
            standard = "Class 5th"

        } else if (currentStudent.classId.contains("6", ignoreCase = true)) {
            standard = "Class 6th"

        } else if (currentStudent.classId.contains("7", ignoreCase = true)) {
            standard = "Class 7th"

        } else if (currentStudent.classId.contains("8", ignoreCase = true)) {
            standard = "Class 8th"

        } else if (currentStudent.classId.contains("9", ignoreCase = true)) {
            standard = "Class 9th"

        } else if (currentStudent.classId.contains("10", ignoreCase = true)) {
            standard = "Class 10th"

        } else if (currentStudent.classId.contains("11", ignoreCase = true)) {
            standard = "Class 11th"

        } else if (currentStudent.classId.contains("12", ignoreCase = true)) {
            standard = "Class 12th"

        }


        attendanceButton.setOnClickListener { startSecurely(AttendanceActivity::class.java) }
        classAnnouncementsButton.setOnClickListener {
            startSecurely(NoticeActivity::class.java, Intent().apply {
                putParcelableArrayListExtra(CygnusApp.EXTRA_NOTICE_POSTS, classPosts)
            })
        }

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        ed_loginsave.putString("studentclassId", currentStudent.classId)
        ed_loginsave.putString("studentschoolid", schoolId)
        ed_loginsave.putString("user_teacheremail", teacherid)
        ed_loginsave.putString("user_name", currentStudent.name)
        ed_loginsave.putString("user_rollNo", currentStudent.rollNo)
        ed_loginsave.putString("user_standard", standard)
        ed_loginsave.putString("userrrtypee", "Student")

        ed_loginsave.commit()


        getCoins()
       /* val notificationIntent:Intent  =  Intent(applicationContext, ShowNotification::class.java);
        val contentIntent:PendingIntent  = PendingIntent.getService(applicationContext, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT)
        val am:AlarmManager  = getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        am.cancel(contentIntent)
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), 5, contentIntent)*/
   val calendar: Calendar  = Calendar.getInstance()
val intent1:Intent  =  Intent(this, AlarmReceiverQuizz::class.java)
val pendingIntent:PendingIntent  = PendingIntent.getBroadcast(this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
    val am:AlarmManager  = getSystemService(Context.ALARM_SERVICE) as AlarmManager;
am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 3600*4 , pendingIntent)


        class_yt_myvideos.setOnClickListener {

            //Toast.makeText(this,""+sp_loginsave.getString("user_teacheremail",""),Toast.LENGTH_SHORT).show()
            val intent = Intent(this, St_YoutubeVideos::class.java)
            intent.putExtra("user_teacherid", teacherid)
            intent.putExtra("user_standard", standard)
            /* intent.putExtra("user_classid",currentStudent.classId)
               intent.putExtra("user_teacherid",teacherid)
               intent.putExtra("user_schoolid",schoolId)
               intent.putExtra("user_standard",standard)*/
            startActivity(intent)
        }

               feed.setOnClickListener {
            val intent = Intent(this, FeedActivity::class.java)
            intent.putExtra("studentname", currentStudent.name)
            intent.putExtra("studentschoolid", schoolId)
            intent.putExtra("studentclassId", currentStudent.classId)
                   intent.putExtra("studentschool_namee", school)
                      intent.putExtra("studenttype","Student")
                      intent.putExtra("student_teacheridd",teacherid)


                   startActivity(intent)
        }
        class_yt_notifications.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        classFees.setOnClickListener {
            val intent = Intent(this, PayFees::class.java)
            intent.putExtra("studentschoolid", schoolId)
            intent.putExtra("studentclassId", currentStudent.classId)
            intent.putExtra("studentname", currentStudent.name)
            intent.putExtra("studentrollNoo", currentStudent.rollNo)
            startActivity(intent)
        }

        playquiz.setOnClickListener {
            val intent=Intent(applicationContext,StartQuizActivity::class.java)
            intent.putExtra("user_standard", standard)
            intent.putExtra("studentname", currentStudent.name)
            intent.putExtra("studentschoolid", schoolId)
            intent.putExtra("studentclassId", currentStudent.classId)
            startActivity(intent)
        }
        chatstudent.setOnClickListener {
            val i = Intent(applicationContext, Chat::class.java)
            i.putExtra("schoolid_chat", schoolId)
            i.putExtra("chat_username", currentStudent.name)
            i.putExtra("name_chatwith", teachername)
            i.putExtra("userrtypeeee", "Student")
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            startActivity(i)
        }


        classschedule_student.setOnClickListener {
            val intent = Intent(this, ScheduleCkassOnlineList::class.java)
            /*intent.putExtra("username",currentUser.credentials.email)
            intent.putExtra("user_classid",currentStudent.classId)
            intent.putExtra("user_teacherid",teacherid)
            intent.putExtra("user_schoolid",schoolId)
            intent.putExtra("user_name",currentStudent.name)
            intent.putExtra("user_rollNo",currentStudent.rollNo)
            intent.putStringArrayListExtra("subjectlist", subjectNames as ArrayList<String>?)*/
            startActivity(intent)


        }

        /* payfees.setOnClickListener {
             startRazorPayProcess()
         }*/

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
                teacherid = teacher.email
                teachername = teacher.name
            }
        })




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

                        val post = StudentToken(currentStudent.name,currentStudent.classId, token);
                        val missionsReference =
                                FirebaseDatabase.getInstance().reference.child(schoolId).child("StudentTokens").child(token.toString())
                        missionsReference.setValue(post)


                        //sendFCMPush();
//Displaying the token as toast
// Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();
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
        } else { //Starting intent to register device
            val itent = Intent(applicationContext, GCMRegistrationIntentService::class.java)
            startService(itent)
        }


        //set reminder
        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMMM")
        val month_name = month_date.format(cal.time)
        reference2 = FirebaseDatabase.getInstance().reference.child(schoolId).
                child("FeeHistory")
        reference2!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    if (currentStudent.classId.equals(datas.child("classId").value.toString())
                            && currentStudent.name.equals(datas.child("studentName").value.toString())
                            && month_name.equals(datas.child("feeMonth").value.toString())
                            && currentStudent.rollNo.equals(datas.child("studentRollno").value.toString())) {
                        reminderstatus = true
                    }

                }

                if (reminderstatus == true) {
                   Log.e("Receiver", "Broadcast receiveddd44:")

               } else {
                   val calendar = Calendar.getInstance()
                   val myIntent = Intent(applicationContext, AlarmReceiver::class.java)
                   val pendingIntent = PendingIntent.getBroadcast(applicationContext,
                           0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                   val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                   alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                           AlarmManager.INTERVAL_DAY * 30, pendingIntent)
               }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                //pb_feehistory.visibility= View.GONE
                Toast.makeText(applicationContext, "" + databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })







    }

    override fun onRestart() {
        super.onRestart()
        getCoins()
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

        val calendar: Calendar  = Calendar.getInstance()
        val intent1:Intent  =  Intent(this, AlarmReceiverQuizz::class.java)
        val pendingIntent:PendingIntent  = PendingIntent.getBroadcast(this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        val am:AlarmManager  = getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 3600*4 , pendingIntent)
//        3600 * 1000 * 4
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver!!)
        val calendar: Calendar  = Calendar.getInstance()
        val intent1:Intent  =  Intent(this, AlarmReceiverQuizz::class.java)
        val pendingIntent:PendingIntent  = PendingIntent.getBroadcast(this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        val am:AlarmManager  = getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 3600*4 , pendingIntent)
      //  startService( Intent(this, NotificationService::class.java))
    }

    /**
     * Displays the signed in user's details.
     */
    override fun updateUI(currentUser: User) {
        className.text = currentStudent.classId
        getSubjectsList()
        coursenamelist.clear()
        getCourseOfferedList()
    }


    private fun getCoins(){
        //get coins of user
        val rootReff = FirebaseDatabase.getInstance().reference.
                child("coins")
        rootReff.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val ref = FirebaseDatabase.getInstance().reference
                            .child("coins")
                    ref.orderByChild("name").equalTo(currentStudent.name).
                            addListenerForSingleValueEvent(object : ValueEventListener {

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (datas in dataSnapshot.children) {
                                            val key = datas.key
                                            usertotalpoints = (datas.child("usertotalpoints").value.toString()).toInt()
                                            mtv_totalpts.setText(usertotalpoints.toString()+" Points")
                                       //  Toast.makeText(applicationContext,"Coinss: "+usertotalpoints,Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {
                                }
                            })
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    /**
     * Gets a list of courses from database taught by [currentTeacher].
     */
    private fun getSubjectsList() {
        SubjectsDao.getSubjectsByClass(schoolId, currentStudent.classId, OnSuccessListener {
            onSubjectsReceived(it)
        })
    }
    private fun getCourseOfferedList() {
        val rootReff = FirebaseDatabase.getInstance().reference.child("coursesoffered")
                    rootReff.orderByChild("classid").equalTo(standard).
                            addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (datas in dataSnapshot.children) {
                                                val rootReff5=rootReff.child(datas.key.toString()).child("courselist")
                                                rootReff5.addListenerForSingleValueEvent(object : ValueEventListener {
                                                    override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                                                        if (dataSnapshotliked.exists()) {
                                                            for (datasliked in dataSnapshotliked.children) {
                                                               // val coursename= datasliked.child("name").value.toString()
                                                                coursenamelist.add(datasliked.key.toString())

                                                            }
                                                            coursesofferedList.layoutManager = GridLayoutManager(this@StudentDashboardActivity, 2)
                                                            coursesofferedList.adapter = CourseAdapter(this@StudentDashboardActivity,
                                                                    coursenamelist,datas.key.toString(), standard.toString())

                                                        }

                                                    }

                                                    override fun onCancelled(p0: DatabaseError) {
                                                        Log.e("msg","Courseerror 2:"+p0.toString())

                                                    }
                                                })



                                        }
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {
                                    Log.e("msg","Courseerror 3:"+p0.toString())

                                }
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


             val subjectColor: View
             val subjectName: TextView
             val subjectClass: TextView
             val subjectTeacher: TextView
            val v:View=LayoutInflater.from(context).inflate(R.layout.view_subject, parent,false)


            subjectColor = v.findViewById(R.id.subjectColor)
            subjectName = v.findViewById(R.id.subjectName)
            subjectClass = v.findViewById(R.id.subjectClass)
            subjectTeacher = v.findViewById(R.id.subjectTeacher)
            sp_loginsave = context.getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
            ed_loginsave = sp_loginsave.edit()

            subjectName.text = subjects.get(position).name
            subjectClass.text = subjects.get(position).classId
            UsersDao.getUserByEmail(schoolId, subjects.get(position).teacherId, OnSuccessListener { user ->
                subjectTeacher.text = user?.name ?: subjects.get(position).teacherId
            })
            // subjectTeacher.text = model.teacherId
            subjectColor.setBackgroundColor(convertToColor(subjects.get(position)))
            //subjectlist.add(subjects[position].name)
            // Log.e("msg111122",subjects[position].name)
            v.setOnClickListener {
                val subject = subjects[position]
                startSecurely(SubjectActivity::class.java, Intent().apply {
                    putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
                    putExtra("user_name", currentStudent.name)
                    putExtra("user_tokennn", token)
                    putExtra("subjectTeacher_namee", subjects[position].teacherId)
                    putExtra("userrtypeeee", "Student")

                })
            }
           // if(currentUser.type.equals("Teacher")){
                subjectTeacher.visibility =  View.VISIBLE
           // }
            if(currentUser.type.equals("Student")){
                subjectClass.visibility =  View.GONE
            }
            else{
                subjectClass.visibility =  View.VISIBLE
            }
//            setSubjectTeacherVisible(true)
         //   setSubjectClassVisible(false)

           /* (v as SubjectView).apply {
                updateWithSchool(schoolId)
                setSubjectTeacherVisible(true)
                setSubjectClassVisible(false)
            }*/
            return v
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



    internal class CourseAdapter(val context: Activity,
                                 val coursenamelist: ArrayList<String>,val datakey:String,val standard:String) :
            RecyclerView.Adapter<CourseAdapter.MyViewHolder>() {
        internal inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {

            val coursename: MaterialTextView = v.findViewById(R.id.coursename)
            val courseColor: View = v.findViewById(R.id.courseColor)
            val courseCard: MaterialCardView = v.findViewById(R.id.courseCard)
        }
        @NonNull
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: View = LayoutInflater.from(context).inflate(R.layout.custom_courseoffered, parent,false)
            return MyViewHolder(v)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                holder.coursename.setText(coursenamelist.get(position))
            holder.courseColor.setBackgroundColor(convertToColor(coursenamelist.get(position)))
            holder.courseCard.setOnClickListener {


                try{
                    val intent=Intent(context,SingleCourseActivity::class.java)
                    intent.putExtra("datakeycourse",datakey)
                    intent.putExtra("course_name",holder.coursename.text.toString())
                    intent.putExtra("course_std",standard)
                    context.startActivity(intent)
                }
                catch (e:Exception){
                    Log.e("msg111122","StudentDashboard66:"+e.toString())

                }

            }
        }
        override fun getItemCount(): Int {
            return coursenamelist.size
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

    private fun startRazorPayProcess() {
        val co = Checkout()
        co.setKeyID("rzp_test_lgUsWAE1gLkaLz")
        try {
            val options = JSONObject()
            options.put("name", currentStudent.name)
            options.put("description", "Pay Fees")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency", "INR")
            options.put("amount", (2000 * 100).toString())
            val preFill = JSONObject()
            preFill.put("email", currentStudent.email)
            preFill.put("contact", currentStudent.phone)
            options.put("prefill", preFill)
            co.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message,
                    Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentError(p0: Int, s: String?) {
        Toast.makeText(applicationContext, "Payment Failure", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        Toast.makeText(applicationContext, "Payment Successful", Toast.LENGTH_SHORT).show()

    }

    class AlarmReceiver : BroadcastReceiver() {
        var monthname: String? = null
        override fun onReceive(context: Context, intent: Intent) { // TODO Auto-generated method stub
            val cal = Calendar.getInstance()
            val month_date = SimpleDateFormat("MMMM")
            monthname = month_date.format(cal.time)
            Log.e("Receiver", "Broadcast receiveddd33:" + monthname)


            val mNotificationManager: NotificationManager
            val mBuilder = NotificationCompat.Builder(context, "notify_001")
            /*long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, StudentDashboardActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.applogo)
                .setContentTitle("Alarm Fired")
                .setContentText("Events to be Performed").setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(( int ) System. currentTimeMillis (), mNotifyBuilder.build());*/
// MID++;
            val ii: Intent
            ii = Intent(context, StudentDashboardActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context,
                    0, ii, 0)
            val bigText = NotificationCompat.BigTextStyle()
            bigText.bigText("Fee Due for " + monthname)
            bigText.setBigContentTitle("Eduistein")
            mBuilder.setContentIntent(pendingIntent)
            mBuilder.setSmallIcon(R.drawable.applogo)
            mBuilder.priority = Notification.PRIORITY_MAX
            mBuilder.setStyle(bigText)
            mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // === Removed some obsoletes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "Your_channel_id"
                val channel = NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH)
                mNotificationManager.createNotificationChannel(channel)
                mBuilder.setChannelId(channelId)
            }
            mNotificationManager.notify(0, mBuilder.build())
        }
    }



}


