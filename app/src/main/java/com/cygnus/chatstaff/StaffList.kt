package com.cygnus.chatstaff

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.view.NestedListView
import com.cygnus.CygnusApp
import com.cygnus.R
import com.cygnus.model.*
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.Query
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList


class StaffList : AppCompatActivity() ,Chatmessageinterface{
    var usersList: NestedListView? = null
    var noUsersText: TextView? = null
    var totalUsers = 0
    private val teacherList: ArrayList<Teacher> = ArrayList()
lateinit var schoolid:String
lateinit var teachername:String
lateinit var schoolnameee:String
lateinit var schoolname:String
lateinit var student_teacheridd:String
lateinit var userrtypeeee:String
    var sortlistfinal:ArrayList<Sortchatmodel> = ArrayList<Sortchatmodel>()
    var pd: ProgressDialog? = null
    var al = ArrayList<String>()
    var listmessage = ArrayList<Newchatmodel>()
    var sortchatlist = ArrayList<Sortchatmodel>()
    var timestampnamelist = ArrayList<Sortchatmodel>()
   lateinit var reference1: Firebase
    lateinit var sp_loginsave: SharedPreferences
    lateinit var ed_loginsave: SharedPreferences.Editor
    lateinit var formattedDate:String
    lateinit var time:String
    lateinit var date_time:String
     var sendmessage_successful:String="false"
     var chatclick_position:Int=-1
    lateinit var firebaseuid:String
    private var staffAdapter: NewStaffAdapter? = null
   // val list: ArrayList<NameTimestampmodel> = ArrayList()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stafflist)
        Firebase.setAndroidContext(this)
        usersList = findViewById(R.id.usersList) as NestedListView
        noUsersText = findViewById(R.id.noUsersText) as TextView

        pd = ProgressDialog(this)
        pd!!.setMessage("Loading...")
        pd!!.show()
        firebaseuid = FirebaseAuth.getInstance().currentUser!!.uid

        try{
            schoolid = intent.getStringExtra("studentschoolid")
            teachername = intent.getStringExtra("studentname")
            schoolname = intent.getStringExtra("studentschool_namee")
            student_teacheridd = intent.getStringExtra("student_teacheridd")


        }
        catch (e:Exception){}

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()
        schoolnameee=sp_loginsave.getString("schoolnameee","").toString()


   //     UserDetails.username=teachername
        UserDetails.username=teachername

        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
         formattedDate = df.format(c)

        val calander = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("hh:mm a")
         time = simpleDateFormat.format(calander.time)
        // UserDetails.chatWith="eduistein"

       date_time = formattedDate+" "+time


       teacherList.clear()
        getStudentsInClass(schoolid,
                OnSuccessListener {
               //     doOnSuccess(it)
                    teacherList.addAll(it)

                    if(teachername.contains("School",ignoreCase = true)){
                    }
                    else{
                        teacherList.add(Teacher("",schoolname ,Credentials("","")))
                      //   teacherList.add(Teacher(student_teacheridd,"Group Chat" ,Credentials(student_teacheridd,"")))
//                        getChat(schoolnameee)
                    }


//store all names with current timestamp so that we can arrange list according to most recent chat
                    val reff2 = FirebaseDatabase.getInstance().reference.child(schoolid).
                                child("NamewithTimestampMessages")
                        reff2.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshottimee: DataSnapshot) {
                                if (dataSnapshottimee.exists()) {

                                        val reffexist = FirebaseDatabase.getInstance().reference.child(schoolid).
                                                child("NamewithTimestampMessages")
                                        val query: com.google.firebase.database.Query = reffexist.
                                                orderByChild("chatusername").equalTo(teachername)
                                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshottimee: DataSnapshot) {
                                                if (!dataSnapshottimee.exists()) {

                                                    Log.e("msg","UserMessagewtryretyrywwwe1:"+teachername)
                                                    val tsLong = System.currentTimeMillis() / 1000
                                                    val ts = tsLong.toString()
                                                    val post = Sortchatmodel(ts,teachername,date_time,time,"")
                                                    val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).
                                                            child("NamewithTimestampMessages")
                                                    missionsReference.push().setValue(post)
                                                }

                                            }

                                            override fun onCancelled(p0: DatabaseError) {

                                            }
                                        })


                                }
                                else {
                                    for( x in teacherList){
                                        Log.e("msg","UserMessagewtryretyrywwwe1:"+x.name)
                                        val tsLong = System.currentTimeMillis() / 1000
                                        val ts = tsLong.toString()
                                        val post = Sortchatmodel(ts,x.name,date_time,time,"")
                                        val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolid).child("NamewithTimestampMessages")
                                        missionsReference.push().setValue(post)
                                    }

                                   
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })


                    userrtypeeee = intent.getStringExtra("userrtypeeee")

                    Log.e("msg","UserMessagewwwwe5464645641:"+userrtypeeee)

                    getTimestampList()



                 /*   .getTimestampUsers(
                            schoolid,
                            OnSuccessListener {
                                timestampnamelist = it
                                staffAdapter?.notifyDataSetChanged()
                                for(ds in timestampnamelist){
                                    Log.e("msg", "TEACHERNAMEeeeee:" + ds.chatusername)

                                }


                             //   Log.e("msg", "TEACHERNAMEeeeee:" + timestampnamelist.size)
                             //   Log.e("msg", "TEACHERNAMEeeeee1:" + timestampnamelist.get(0).chatusername)
                             //   usersList!!.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL ,false)
                                staffAdapter = NewStaffAdapter(this, timestampnamelist,schoolid,teachername,
                                        this@StaffList)
                                usersList!!.adapter = staffAdapter
                            })*/


                })




    }

    private fun getTimestampList() {
        timestampnamelist.clear()
        val reff2 = FirebaseDatabase.getInstance().reference.child(schoolid).
                child("NamewithTimestampMessages")
        val chatQuery: com.google.firebase.database.Query = reff2.orderByChild("postdate").limitToLast(teacherList.size+1)

        chatQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshottimee: DataSnapshot) {
                if (dataSnapshottimee.exists()) {
                    for(ds in dataSnapshottimee.children){

                        if(!ds.child("chatusername").getValue().toString().equals(teachername)){
                           /* if(userrtypeeee.equals("School")){
                                if(!ds.child("chatusername").getValue().toString().equals("Group Chat"))
                                timestampnamelist.add(Sortchatmodel(ds.child("postdate").getValue().toString(),
                                        ds.child("chatusername").getValue().toString(),ds.child("date").getValue().toString(),
                                        ds.child("time").getValue().toString(),ds.child("message").getValue().toString()))
                            }*/
                            // if(!userrtypeeee.equals("School")){
                                timestampnamelist.add(Sortchatmodel(ds.child("postdate").getValue().toString(),
                                        ds.child("chatusername").getValue().toString(),ds.child("date").getValue().toString(),
                                        ds.child("time").getValue().toString(),ds.child("message").getValue().toString()))
                            //}

                        }



                    }
                    //timestampnamelist.sortedBy { it.date.toDate() }
                    Collections.reverse(timestampnamelist)
                    staffAdapter = NewStaffAdapter(this@StaffList, timestampnamelist,schoolid,teachername,userrtypeeee,
                            this@StaffList)
                    usersList!!.adapter = staffAdapter

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
    fun String.toDate(): Date{
        return SimpleDateFormat("dd MMM yyyy HH:mm a", Locale.getDefault()).parse(this)
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
                            if (user!!.name != UserDetails.username) {
                                if (user is Teacher) {

                                    teachers.add(user)
                                    //getChat(user.name)
                                    Log.e("msg", "TEACHERNAME:" + user.name)
                                    totalUsers++
                                }
                            }

                        }

                        listener.onSuccess(teachers)


                        if (totalUsers <= 1) {
                            noUsersText!!.visibility = View.VISIBLE
                            usersList!!.visibility = View.GONE
                        } else {
                            noUsersText!!.visibility = View.GONE
                            usersList!!.visibility = View.VISIBLE



                        }
                        pd!!.dismiss()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(ArrayList())
                        Log.e("msg", "NAMESSSSSSSLIST1" + error.toString())

                    }
                })
    }

    fun getChat(user: String) {
        reference1 = Firebase("https://cygnus-3554a.firebaseio.com/" + schoolid + "/messages/" +
                UserDetails.username + "_" + user)
        val query: Query = reference1.orderByKey().limitToLast(1)
        query.addListenerForSingleValueEvent(object :
                com.firebase.client.ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {

            }

            override fun onDataChange(p0: com.firebase.client.DataSnapshot?) {
                for(d in p0!!.children){
                    val message =d?.child("message")?.value.toString()
                    val postdate =d?.child("postdate")?.value.toString()
                    val date =d?.child("date")?.value.toString()
                    val userName =d?.child("user")?.value.toString()
                    Log.e("msg","UserMessagee:"+message)
                    listmessage.add(Newchatmodel(Date(System.currentTimeMillis()),
                            formattedDate,time,message,user,firebaseuid,"unread","0","0"))
                   /* for(x in teacherList){
                        sortchatlist.add(Sortchatmodel(postdate,x.name,date,message))
                        sortchatlist.sortByDescending { it.postdate }
                    }*/


                }


              /*  val hashSet: HashSet<Sortchatmodel>  =  HashSet(sortchatlist)   // create has set. Set will contains only unique objects
       for (z  in hashSet) {
           Log.e("msg","UserMessageeSortListMessage1:"+z.postdate+", : "+z.teachername+" ,: "+z.date+" ,: "+z.message)
       }*/



                Log.e("msg","UserMessageeSortList1:"+sortchatlist.size)
                //  addMessageBox( message, 1);
            }

        })


    }

    override fun onRestart() {
        super.onRestart()
        getTimestampList()

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

    override fun chat(position: Int, name: String, tv_mesg: TextView,tv_date:TextView, tv_msgcounter:TextView) {

/*
        val reff2 = FirebaseDatabase.getInstance().reference.child(schoolid).
                child("NamewithTimestampMessages")
        val chatQuery: com.google.firebase.database.Query = reff2.orderByChild("chatusername").equalTo(name)
        chatQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshottimee: DataSnapshot) {
                if (dataSnapshottimee.exists()) {
                    for(ds in dataSnapshottimee.children){
                        val timekey = ds.key.toString()
                        val tsLong = System.currentTimeMillis() / 1000
                        val ts = tsLong.toString()
                        reff2.child(timekey).child("postdate").setValue(ts)
                    }

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })*/

        reference1 = Firebase("https://cygnus-3554a.firebaseio.com/" + schoolid + "/messages/" +
                UserDetails.username + "_" + name)
        val query: Query = reference1.orderByKey().limitToLast(1)
        query.addListenerForSingleValueEvent(object :
                com.firebase.client.ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {

            }

            override fun onDataChange(p0: com.firebase.client.DataSnapshot?) {

                for(d in p0!!.children){
                    val message =d?.child("message")?.value.toString()
                    if(d?.child("type")?.value.toString().equals("2")){
                          if(d?.child("msgstatus")?.value.toString().equals("unread")){
                              val msgcounter =d?.child("countunread")?.value.toString()
                              val intmsgcount:Int=msgcounter.toInt()
                              if(intmsgcount>=1){
                                  tv_msgcounter.visibility=View.VISIBLE
                                  tv_msgcounter.setText(msgcounter)
                              }
                          }
                      }


                    val userName =d?.child("user")?.value.toString()
                    val time =d?.child("time")?.value.toString()
                    val postdate =d?.child("postdate")?.value.toString()
                    Log.e("msg","UserMessagee:"+message)

                   // listmessage.add(Chatmodel(message,name,formatted,0))
                   // if(name.equals(x.user)){
                    tv_mesg.setText(message)
                    tv_date.setText(time)

                  //  }
                }
                Log.e("msg","UserMessagee1:"+listmessage.size)

            }

        })


    }

}
