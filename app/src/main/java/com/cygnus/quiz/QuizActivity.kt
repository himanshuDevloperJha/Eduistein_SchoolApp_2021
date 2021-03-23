package com.cygnus.quiz

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cygnus.CygnusApp
import com.cygnus.R
import com.cygnus.chatstaff.Sortchatmodel
import com.cygnus.dao.NoticeBoardDao
import com.cygnus.feed.LikePostsmodel
import com.cygnus.feed.PostmodelBoard
import com.cygnus.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_quiz.*
import java.util.*


class QuizActivity : AppCompatActivity() {
    lateinit var standard: String
    lateinit var studentschoolid: String
    lateinit var studentclassId: String
    lateinit var studentname: String
    lateinit var secondname: String
    lateinit var quizkey: String
    lateinit var timestampuser: String
    lateinit var timestampseconduser: String
    lateinit var reference1: DatabaseReference
   var flagindexfour:String="false"
    var quizlist = ArrayList<QuizModel>()
    var no_quizlist = ArrayList<String>()
    var quizstatuslist = ArrayList<String>()
    private val studentsList: ArrayList<Student> = ArrayList()

    var j = 0
    var coinsuser = 0
    var coins2 = 0
    var coinsseconduser = 0
    var usertotalpoints = 0
    var no_correctans = 0
    var no_wrongans = 0
    var selectedanswer = ""
    var correctanswer = ""
    lateinit private var textViewShowTime: TextView
    lateinit private var textView_adtimer: TextView
    lateinit var mProgressBar: ProgressBar
    lateinit var mProgressBar1: ProgressBar
    lateinit var progressbar_adtimer: ProgressBar
    lateinit var progressbar_adtimer1: ProgressBar
    var time = 0
    var i = -1
    var computeranswerstatus = false
    var autosubmitques = false
    var quizwrongans:String = ""
    var singlewrongmarked:String = ""
    var nextbtnclick_lastques = false
    lateinit private var countDownTimer: CountDownTimer
    lateinit private var timer1: CountDownTimer

    lateinit var countDownAdTimer: CountDownTimer
    lateinit var countDownFlagTimer: CountDownTimer
    // lateinit private var quizkey: String
    private var totalTimeCountInMilliseconds: Long = 0
    val randomizer = Random()
    var quizidindex = "1"
    var quizidnext = 1
    lateinit var sp_loginsave: SharedPreferences
    lateinit var ed_loginsave: SharedPreferences.Editor
    private var postslistads = ArrayList<PostmodelBoard>()
    private var postslistads2 = ArrayList<PostmodelBoard>()
    lateinit var quizpost:PostmodelBoard
    var randomquizid:Int=0
    var testflag:String=""
    var rdno_quizlist: Int = 0
    private var adsviewslist = ArrayList<LikePostsmodel>()
    private var adsviewslist2 = ArrayList<LikePostsmodel>()
    var quizanswermarkedkey:String=""

    lateinit var reffquizanswersmarked:DatabaseReference
    lateinit var  refseconduserr:DatabaseReference
    lateinit var  queryanswermarked: com.google.firebase.database.Query
    var markansvalue:String=""
    var keyseconduserr:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        mProgressBar = findViewById(R.id.progressbar_timerview)
        textViewShowTime = findViewById(R.id.textView_timerview_time)
        mProgressBar1 = findViewById(R.id.progressbar1_timerview)


        progressbar_adtimer = findViewById(R.id.progressbar_adtimer)
        textView_adtimer = findViewById(R.id.textView_adtimer)
        progressbar_adtimer1 = findViewById(R.id.progressbar_adtimer1)

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        try {
            if (sp_loginsave.contains("quizid_index")) {
                quizidindex = sp_loginsave.getInt("quizid_index", -1).toString()
                quizidnext=quizidindex.toInt()
            } else {
                quizidindex = "1"
                quizidnext=quizidindex.toInt()
            }
        } catch (e: Exception) {
        }



        standard = intent.getStringExtra("user_standard")
        studentschoolid = intent.getStringExtra("studentschoolid")
        studentclassId = intent.getStringExtra("studentclassId")
        studentname = intent.getStringExtra("studentname")

        btn_nextquiz.isEnabled = false

        tv_ownername.setText(studentname)

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Quiz Pool")
        progressDialog.setMessage("Please wait for 30 seconds.....\nFetching details of other competitor")
        progressDialog.show()
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        tv_secondname.setText("Waiting..")
      //  tv_secondname.setText("Daisy")


        //add ads to list
        postslistads.clear()
        NoticeBoardDao.getAdminQuizAds(
                OnSuccessListener {
                    postslistads = it
                    Collections.shuffle(postslistads)

                    val random: Int = Random().nextInt(postslistads.size)
                        Log.e("msg","QuizIndexAds1::"+random)
                    quizpost = postslistads.get(random)



                })

        // check if quizstatus all false or not,, if false then set quizidindex to 1
                        val reff21 = FirebaseDatabase.getInstance().reference.child("admin-quiz")
                        reff21.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot9: DataSnapshot) {
                                if (dataSnapshot9.exists()) {
                                    for (datas9 in dataSnapshot9.children) {
                                        if (datas9.child("class").getValue().toString().contains(standard, ignoreCase = true)) {

                                            val key9 = datas9.key
                                            Log.e("msg", "VALUESQUIZ1133366688899:" + key9)
                                            val quizstatuss = datas9.child("quizstatus").value.toString()
                                            quizstatuslist.add(quizstatuss)

                                        }

                                    }


                                    val allEqual: Boolean = HashSet<String>(quizstatuslist).size <= 1
                                    Log.e("msg", "VALUESQUIZ11333666888990000000:" + allEqual)
                                    if (allEqual == true) {
                                        quizidindex = "1"
                                        quizidnext = quizidindex.toInt()
                                        quizidnext=quizidnext+1
                                        ed_loginsave.putInt("quizid_index", quizidnext)
                                        ed_loginsave.commit()

                                        //set status false
                                        val reff22 = FirebaseDatabase.getInstance().reference.child("admin-quiz")
                                        reff22.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot66: DataSnapshot) {
                                                if (dataSnapshot66.exists()) {
                                                    for (datas66 in dataSnapshot66.children) {
                                                        if (datas66.child("class").getValue().toString().contains(standard, ignoreCase = true)) {
                                                            val key66 = datas66.key
                                                            Log.e("msg", "VALUESQUIZ1133366688899:" + key66)
                                                            val quizstatus = datas66.child("quizstatus").value.toString()

                                                            reff22.child(key66!!).child("quizstatus").setValue(true)
                                                        }


                                                    }

                                                }
                                            }

                                            override fun onCancelled(p0: DatabaseError) {
                                                Log.e("msg", "VALUESQUIZ11333666:" + p0.toString())

                                            }
                                        })
                                    } else {
                                    }

                                    //  if(allEqual==true)
                                    // val allEqual = quizstatuslist.isEmpty() || quizstatuslist.stream().allMatch(quizstatuslist.get(0)::equals)
                                    // val allEqual:Boolean  = quizstatuslist.stream().distinct().limit(2).count() <= 1

                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.e("msg", "VALUESQUIZ11333666345435:" + p0.toString())

                            }
                        })


                        val ref = FirebaseDatabase.getInstance().reference.child("quizscreen")
                        ref.orderByChild("name").equalTo(studentname).
                                addListenerForSingleValueEvent(object : ValueEventListener {

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (datas in dataSnapshot.children) {
                                        if (datas.child("name").value.toString().equals(studentname)
                                                && datas.child("classid").value.toString().contains(standard)) {
                                            val key = datas.key
                                            val status = datas.child("status").value.toString()
                                            ref.child(key!!).child("status").setValue("true")
                                            ref.child(key!!).child("connect").setValue("free")
                                            ref.child(key!!).child("quizindex").setValue(quizidnext)
                                        } else {
                                            val post = QuizScreen(studentname,standard, "true","free",quizidnext)
                                            val missionsReference = FirebaseDatabase.getInstance().reference.child("quizscreen").push()
                                            missionsReference.setValue(post)
                                        }

                                    }
                                } else {
                                    val post = QuizScreen(studentname,standard,"true","free",quizidnext)
                                    val missionsReference = FirebaseDatabase.getInstance().reference.child("quizscreen").push()
                                    missionsReference.setValue(post)
                                }

                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })


        //get coins of user
        val rootReff = FirebaseDatabase.getInstance().reference.child("coins")
        rootReff.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val ref = FirebaseDatabase.getInstance().reference
                            .child("coins")
                    ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (datas in dataSnapshot.children) {
                                    val key = datas.key
                                    usertotalpoints = (datas.child("usertotalpoints").value.toString()).toInt()
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


        //show competitor

        studentsList.clear()
        getStudentsInClass("3f1a1ITxLlghwvD0SAsNKIOt3fE2", standard,
                OnSuccessListener {
                    studentsList.addAll(it)
                })
        getStudentsInClass("Ry7mtJ1JdwNiQi2K4k3i5ck6qlr1", standard,
                OnSuccessListener {
                    studentsList.addAll(it)
                })
        getStudentsInClass("sXskpsvj8Ua4Du3dBvn5CsLHoRH2", standard,
                OnSuccessListener {
                    studentsList.addAll(it)
                })


        getStudentsInClass(studentschoolid, standard,
                OnSuccessListener {
                    studentsList.addAll(it)
                    Handler().postDelayed({
                        try {
                            reference1 = FirebaseDatabase.getInstance().reference.child("quizscreen")

                            reference1.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (datas1 in dataSnapshot.children) {
                                            if (datas1.child("status").value.toString().equals("true")
                                                    && datas1.child("connect").value.toString().equals("free")
                                                    && datas1.child("classid").value.toString().contains(standard)
                                                    &&  !(datas1.child("name").value.toString().equals(studentname, ignoreCase = true))) {

                                                secondname=datas1.child("name").value.toString()
                                                tv_secondname.setText(datas1.child("name").value.toString())

                                                        var random: String = studentsList.get(randomizer.nextInt(studentsList.size)).name
                                                        mProgressBar.setVisibility(View.INVISIBLE)
                                                        //startTimer()
                                                        mProgressBar1.setVisibility(View.VISIBLE)
                                                        btn_nextquiz.isEnabled = true
//                                                        progressDialog.dismiss()




                                                        //set connect busy of second person
                                                            val reffg = FirebaseDatabase.getInstance().reference.child("quizscreen")
                                                            reffg.orderByChild("name").equalTo(tv_secondname.text.toString()).
                                                                    addListenerForSingleValueEvent(object : ValueEventListener {

                                                                        override fun onDataChange(dataSnapshotbusy: DataSnapshot) {
                                                                            if (dataSnapshotbusy.exists()) {
                                                                                for (datasbusy in dataSnapshotbusy.children) {
                                                                                    val keybusy = datasbusy.key
                                                                                    reffg.child(keybusy!!).child("connect").setValue("busy")
                                                                                }
                                                                            }
                                                                        }

                                                                        override fun onCancelled(p0: DatabaseError) {
                                                                            // Toast.makeText(applicationContext, "" + p0.toException(), Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    })

                                                 break




                                            }

                                        }
                                    }
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                 //   Toast.makeText(applicationContext, "Error444:" + databaseError.toString(), Toast.LENGTH_SHORT).show()
                                }
                            })
                        } catch (e: Exception) {
                        }
                    },4000)

                })


//after 30 seconds,play game with computer & start timer and enable button
        Handler().postDelayed({
            try {
                progressDialog.dismiss()
                mProgressBar.setVisibility(View.INVISIBLE)
                mProgressBar1.setVisibility(View.VISIBLE)
            } catch (e: WindowManager.BadTokenException) {
            }
            if (!tv_secondname.text.toString().contains("Waiting..")) {
                startTimer()
                 btn_nextquiz.isEnabled=true
                //get questions
                getQuestions()


               // enable start quiz button and onclick of start quiz button,start timer and get questions qand enable next button
               /* val dialogquizstart = Dialog(this)
                dialogquizstart.setContentView(R.layout.dialog_startquiz)
                dialogquizstart.setCanceledOnTouchOutside(false)
                dialogquizstart.setCancelable(false)
                dialogquizstart.show()

                val btn_start = dialogquizstart.findViewById(R.id.btn_start) as Button
                btn_start.setOnClickListener {
                    try{
                        dialogquizstart.dismiss()
                } catch (e: WindowManager.BadTokenException) {

                }


                }*/


            } else {
                tv_secondname.setText("Aditya")
                startTimer()
                btn_nextquiz.isEnabled = true
                //get questions
                getQuestions()
            }

        }, 30000)


        btn_nextquiz.setOnClickListener {
            if(tv_secondname.text.toString().equals("Aditya")){
                nextQues()
            }
           else if(!tv_secondname.text.toString().equals("Aditya")){
                 refseconduserr = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                refseconduserr.orderByChild("username").equalTo(tv_secondname.text.toString()).
                        addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (datas in dataSnapshot.children) {
                                keyseconduserr = datas.key.toString()
                                markansvalue = datas.child("markanswrong").value.toString()

                            }

                            if(quizwrongans.equals("wrongans") && markansvalue.equals("")){
                                singlewrongmarked="wrongfirst"
                                reffquizanswersmarked.child(quizanswermarkedkey).child("markanswrong").setValue("yes")
                                if(selectedanswer.contains("A.  ",ignoreCase = true)){
                                    rb_optiona.isChecked = true
                                    rb_optiona.setBackgroundColor(Color.parseColor("#F89A9D"))
                                }
                                else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                                    rb_optionb.isChecked = true
                                    rb_optionb.setBackgroundColor(Color.parseColor("#F89A9D"))
                                }
                                else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                                    rb_optionc.isChecked = true
                                    rb_optionc.setBackgroundColor(Color.parseColor("#F89A9D"))
                                }
                                else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                                    rb_optiond.isChecked = true
                                    rb_optiond.setBackgroundColor(Color.parseColor("#F89A9D"))
                                }

                                Snackbar.make(btn_nextquiz,"Oops... Your answer is wrong\n"+"Please wait for "+tv_secondname.text.toString(),Snackbar.LENGTH_LONG).show()
                            }
                            else if(quizwrongans.equals("wrongans") && markansvalue.equals("yes")){
                                reffquizanswersmarked.child(quizanswermarkedkey).child("timestatus").setValue("true")
                                reffquizanswersmarked.child(quizanswermarkedkey).child("ansstatus").setValue("true")
                                reffquizanswersmarked.child(quizanswermarkedkey).child("flagstatus").setValue("false")
                                flagindexfour="wrongg"
                                testflag=""
                                nextQues()
                                startTimer()
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })


               /* else{
                nextQues()
                startTimer()
                }*/
            }

        }

        rb_optiona.setOnClickListener {
            rb_optiona.isChecked = true
            rb_optionb.isChecked = false
            rb_optionc.isChecked = false
            rb_optiond.isChecked = false
            rb_optiona.setBackgroundColor(Color.parseColor("#E3E9FB"))
            rb_optionb.setBackgroundColor(Color.parseColor("#ffffff"))
            rb_optionc.setBackgroundColor(Color.parseColor("#ffffff"))
            rb_optiond.setBackgroundColor(Color.parseColor("#ffffff"))
            selectedanswer = rb_optiona.getText().toString()

           if(!tv_secondname.text.toString().equals("Aditya")){
               getQuizanstimestamp()
           }




        }
        rb_optionb.setOnClickListener {
            rb_optionb.isChecked = true
            rb_optiona.isChecked = false
            rb_optionc.isChecked = false
            rb_optiond.isChecked = false
            rb_optionb.setBackgroundColor(Color.parseColor("#E3E9FB"))
            rb_optiona.setBackgroundColor(Color.parseColor("#ffffff"))
            rb_optionc.setBackgroundColor(Color.parseColor("#ffffff"))
            rb_optiond.setBackgroundColor(Color.parseColor("#ffffff"))
            selectedanswer = rb_optionb.getText().toString()
            if(!tv_secondname.text.toString().equals("Aditya")){
                getQuizanstimestamp()
            }


        }
        rb_optionc.setOnClickListener {
            rb_optionc.isChecked = true
            rb_optionb.isChecked = false
            rb_optiona.isChecked = false
            rb_optiond.isChecked = false
            rb_optionc.setBackgroundColor(Color.parseColor("#E3E9FB"))
            rb_optionb.setBackgroundColor(Color.parseColor("#ffffff"))
            rb_optiona.setBackgroundColor(Color.parseColor("#ffffff"))
            rb_optiond.setBackgroundColor(Color.parseColor("#ffffff"))
            selectedanswer = rb_optionc.getText().toString()
            if(!tv_secondname.text.toString().equals("Aditya")){
                getQuizanstimestamp()
            }


        }
        rb_optiond.setOnClickListener {
            rb_optiond.isChecked = true
            rb_optionb.isChecked = false
            rb_optionc.isChecked = false
            rb_optiona.isChecked = false
            rb_optiond.setBackgroundColor(Color.parseColor("#E3E9FB"))
            rb_optionb.setBackgroundColor(Color.parseColor("#ffffff"))
            rb_optionc.setBackgroundColor(Color.parseColor("#ffffff"))
            rb_optiona.setBackgroundColor(Color.parseColor("#ffffff"))
            selectedanswer = rb_optiond.getText().toString()

            if(!tv_secondname.text.toString().equals("Aditya")){
                getQuizanstimestamp()
            }


        }

    }

    private fun getQuizanstimestamp(){

        var timestamp1=""
        var timestamp2=""
        Log.e("msg", "VALUESQUIZdfdgdfg11333666888998766687698888:$selectedanswer")
        Log.e("msg", "VALUESQUIZdfdgdfg113336668889987666876988881:$correctanswer")
        val reff2 = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
        reff2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshottimee: DataSnapshot) {
                if (dataSnapshottimee.exists()) {

                     reffquizanswersmarked = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                     queryanswermarked = reffquizanswersmarked.orderByChild("username").equalTo(studentname)
                    queryanswermarked.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshotquiz: DataSnapshot) {
                            if (dataSnapshotquiz.exists()) {
                                for(dtq in dataSnapshotquiz.children){
                                val tsLong = System.currentTimeMillis() / 1000
                                val ts = tsLong.toString()
                                 quizanswermarkedkey = dtq.key.toString()
                                Log.e("msg", "VALUESQUIZdfdgdfg113336668889987666876:$quizanswermarkedkey")
                                    reffquizanswersmarked.child(quizanswermarkedkey).child("timestamp").setValue(ts)
                                    reffquizanswersmarked.child(quizanswermarkedkey).child("selectedans").setValue(selectedanswer)

                                 if(selectedanswer.contains(correctanswer)){
                                     reffquizanswersmarked.child(quizanswermarkedkey).child("timestatus").setValue("true")
                                     reffquizanswersmarked.child(quizanswermarkedkey).child("ansstatus").setValue("true")
                                     reffquizanswersmarked.child(quizanswermarkedkey).child("flagstatus").setValue("false")
                                     flagindexfour="currentuser"
                                     testflag=""
                                     highlighQuesUser1()


                                     //flagindexfour="false"
                                  //   Handler().postDelayed({
                                         nextQues()
                                         startTimer()
                                     //btn_nextquiz.isEnabled=false

                                     //  },1000)
                                    /* val reffquizanssssecond = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                                     val queryansssecond: com.google.firebase.database.Query = reffquizanssssecond.
                                             orderByChild("username").equalTo(tv_secondname.text.toString())
                                     queryansssecond.addListenerForSingleValueEvent(object : ValueEventListener {
                                         override fun onDataChange(dataSnapshotquizzzzse1: DataSnapshot) {
                                             if (dataSnapshotquizzzzse1.exists()) {
                                                  for(dtqqqq1 in dataSnapshotquizzzzse1.children){
                                                     val quizanskeyse = dtqqqq1.key.toString()

                                                      reffquizanssssecond.child(quizanskeyse).child("flagstatus").setValue("false")
                                                      flagindexfour="false"
                                                      Log.e("msg", "QuizAnsSnehaaaaaaa0011:"+flagindexfour)
                                                 }

                                             }

                                         }
                                         override fun onCancelled(p0: DatabaseError) {

                                         }
                                     })*/

                                   }
                                  else if(!selectedanswer.contains(correctanswer)){
                                     quizwrongans="wrongans"
                                   //  btn_nextquiz.isEnabled=false

                                 }
                                     /*  val querytimesecond: com.google.firebase.database.Query = reffquizans.
                                               orderByChild("username").equalTo(tv_secondname.text.toString())
                                       querytimesecond.addListenerForSingleValueEvent(object : ValueEventListener {
                                           override fun onDataChange(dataSnapshottimesecond: DataSnapshot) {
                                               if (dataSnapshottimesecond.exists()) {
                                                   for(dtqsecond in dataSnapshottimesecond.children){
                                                       timestamp1=dtq.child("timestamp").value.toString()
                                                       timestamp2=dtqsecond.child("timestamp").value.toString()
                                                       // Log.e("msg", "VALUESQUIZTimestamp2:$timestampseconduser")
                                                       if ( Math.abs(timestamp2.toLong()) > Math.abs(timestamp1.toLong()) ) {

                                                           Toast.makeText(applicationContext,"2nd is greater",Toast.LENGTH_LONG).show()
                                                           reffquizans.child(quizanskey!!).child("timestatus").setValue("true")
                                                           reffquizans.child(quizanskey!!).child("ansstatus").setValue("false")
                                                       }
                                                       else{
                                                           Toast.makeText(applicationContext,"1st is greater",Toast.LENGTH_LONG).show()
                                                           reffquizans.child(quizanskey!!).child("timestatus").setValue("false")
                                                           reffquizans.child(quizanskey!!).child("ansstatus").setValue("false")
                                                       }
                                                   }

                                               }

                                           }

                                           override fun onCancelled(p0: DatabaseError) {

                                           }
                                       })
                                   }*/


                                }

                            }
                            else{
                                val tsLong = System.currentTimeMillis() / 1000
                                val ts = tsLong.toString()


                                if(correctanswer.contains(selectedanswer)){
                                    val post = Selectansmodel(ts,studentname,selectedanswer,"true","true","false","")
                                    val missionsReference = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                                    missionsReference.push().setValue(post)
                                    highlighQuesUser1()
                                 //   Handler().postDelayed({
                                        nextQues()
                                        startTimer()
                                   // btn_nextquiz.isEnabled=false

                                    // },1000)
                                   /* val reffquizansss = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                                    val queryansss: com.google.firebase.database.Query = reffquizansss.
                                            orderByChild("username").equalTo(tv_secondname.text.toString())
                                    queryansss.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshotquizzzz1: DataSnapshot) {
                                            if (dataSnapshotquizzzz1.exists()) {
                                                for(dtqqqq1 in dataSnapshotquizzzz1.children){
                                                    val quizanskey222 = dtqqqq1.key

                                                    reffquizansss.child(quizanskey222!!).child("flagstatus").setValue("false")
                                                    flagindexfour="false"
                                                    Log.e("msg", "QuizAnsSnehaaaaaaa0011:"+flagindexfour)
                                                }
                                            }
                                            nextQues()
                                        }
                                        override fun onCancelled(p0: DatabaseError) {

                                        }
                                    })*/


                                }
                                else if(!correctanswer.contains(selectedanswer)){
                                  //  btn_nextquiz.isEnabled=false

                                    quizwrongans="wrongans"
                                    val post = Selectansmodel(ts,studentname,selectedanswer,"","","","")
                                    val missionsReference = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                                    missionsReference.push().setValue(post)
                                }
                            }

                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })




                }
                else {
                    val tsLong = System.currentTimeMillis() / 1000
                    val ts = tsLong.toString()
                    if(correctanswer.contains(selectedanswer)){
                        val post = Selectansmodel(ts,studentname,selectedanswer,"true","true","false","")
                        val missionsReference = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                        missionsReference.push().setValue(post)
                        highlighQuesUser1()
                        nextQues()
                        startTimer()
                     //   btn_nextquiz.isEnabled=false

//                        Handler().postDelayed({
//
//                        },1000)

                       /* val reffquizansss = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                        val queryansss: com.google.firebase.database.Query = reffquizansss.
                                orderByChild("username").equalTo(tv_secondname.text.toString())
                        queryansss.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshotquizzzz1: DataSnapshot) {
                                if (dataSnapshotquizzzz1.exists()) {
                                    for(dtqqqq1 in dataSnapshotquizzzz1.children){
                                        val quizanskey222 = dtqqqq1.key

                                        reffquizansss.child(quizanskey222!!).child("flagstatus").setValue("false")
                                        flagindexfour="false"
                                        Log.e("msg", "QuizAnsSnehaaaaaaa0011:"+flagindexfour)
                                    }
                                }
                                nextQues()
                            }
                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })*/


                    }
                   else if(!correctanswer.contains(selectedanswer)){
                      //  btn_nextquiz.isEnabled=false
                        quizwrongans="wrongans"
                        val post = Selectansmodel(ts,studentname,selectedanswer,"","","","")
                        val missionsReference = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                        missionsReference.push().setValue(post)
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun highlighQuesUser1(){
        coinsuser = coinsuser + 5
        usertotalpoints = usertotalpoints + 5
        tv_ownerscore.setText(coinsuser.toString())
        //tv_secondscore.setText(((tv_secondscore.text.toString()).toInt() +5).toString())
        Log.e("msg", "VALUESQUIZTimestamp3:$coinsuser")
        val rootRef = FirebaseDatabase.getInstance().reference.child("coins")
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val ref = FirebaseDatabase.getInstance().reference
                            .child("coins")
                    ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (datas in dataSnapshot.children) {
                                    val key = datas.key
                                    val points = datas.child("points").value.toString()
                                    ref.child(key!!).child("points").setValue(coinsuser)
                                    ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)

                                }
                            }

                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })
                } else {
                    val post = PointsModel(studentname, coinsuser, usertotalpoints)
                    val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                    missionsReference.setValue(post)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })


        if(selectedanswer.contains("A.  ",ignoreCase = true)){
            rb_optiona.isChecked = true
            rb_optiona.setBackgroundColor(Color.parseColor("#8EC3A3"))
            //   rb_optiona.setCircleColor(Color.parseColor("#F89A9D"))
        }
        else if(selectedanswer.contains("B.  ",ignoreCase = true)){
            rb_optionb.isChecked = true
            rb_optionb.setBackgroundColor(Color.parseColor("#8EC3A3"))
            // rb_optionb.setCircleColor(Color.parseColor("#F89A9D"));

        }
        else if(selectedanswer.contains("C.  ",ignoreCase = true)){
            rb_optionc.isChecked = true
            rb_optionc.setBackgroundColor(Color.parseColor("#8EC3A3"))
            //    rb_optionc.setCircleColor(Color.parseColor("#F89A9D"));

        }
        else if(selectedanswer.contains("D.  ",ignoreCase = true)){
            rb_optiond.isChecked = true
            rb_optiond.setBackgroundColor(Color.parseColor("#8EC3A3"))
            // rb_optiond.setCircleColor(Color.parseColor("#F89A9D"));

        }
    //get timstamp of user
   /* val refftimestampuser = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
    val queryuser: com.google.firebase.database.Query = refftimestampuser.
            orderByChild("username").equalTo(studentname)
    queryuser.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshotuser: DataSnapshot) {
            if (dataSnapshotuser.exists()) {
                for(dtq1 in dataSnapshotuser.children){
                    timestampuser=dtq1.child("timestamp").value.toString()
                    //  Log.e("msg", "VALUESQUIZTimestamp1:$timestampuser")
                }
//get timstamp of second user

                val queryseconduser: com.google.firebase.database.Query = refftimestampuser.
                        orderByChild("username").equalTo(tv_secondname.text.toString())
                queryseconduser.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshotsecond: DataSnapshot) {
                        if (dataSnapshotsecond.exists()) {
                            for(dtq2 in dataSnapshotsecond.children){
                                timestampseconduser=dtq2.child("timestamp").value.toString()
                                // Log.e("msg", "VALUESQUIZTimestamp2:$timestampseconduser")


                            }
                            Log.e("msg", "VALUESQUIZTANSWERRRRRRR12:$selectedanswer")
                            Log.e("msg", "VALUESQUIZTANSWERRRRRRR13:$correctanswer")
                            if ( Math.abs(timestampseconduser.toLong()) > Math.abs(timestampuser.toLong()) ) {
                                if (!selectedanswer.contains(correctanswer)) {
                                    //computeranswerstatus = true
                                    //disable all radiobuttons
                                    rb_optiona.isEnabled = false
                                    rb_optionb.isEnabled = false
                                    rb_optionc.isEnabled = false
                                    rb_optiond.isEnabled = false


                                    //red colour
                                    if(selectedanswer.contains("A.  ",ignoreCase = true)){
                                        rb_optiona.isChecked = true
                                        rb_optiona.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        //   rb_optiona.setCircleColor(Color.parseColor("#F89A9D"))
                                    }
                                    else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                                        rb_optionb.isChecked = true
                                        rb_optionb.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        // rb_optionb.setCircleColor(Color.parseColor("#F89A9D"));

                                    }
                                    else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                                        rb_optionc.isChecked = true
                                        rb_optionc.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        //    rb_optionc.setCircleColor(Color.parseColor("#F89A9D"));

                                    }
                                    else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                                        rb_optiond.isChecked = true
                                        rb_optiond.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        // rb_optiond.setCircleColor(Color.parseColor("#F89A9D"));

                                    }

                                }
                                //green colour
                                else  if (selectedanswer.contains(correctanswer)) {
                                    if(selectedanswer.contains("A.  ",ignoreCase = true)){
                                        rb_optiona.isChecked = true
                                        rb_optiona.setBackgroundColor(Color.parseColor("#8EC3A3"))
                                        //   rb_optiona.setCircleColor(Color.parseColor("#F89A9D"))
                                    }
                                    else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                                        rb_optionb.isChecked = true
                                        rb_optionb.setBackgroundColor(Color.parseColor("#8EC3A3"))
                                        // rb_optionb.setCircleColor(Color.parseColor("#F89A9D"));

                                    }
                                    else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                                        rb_optionc.isChecked = true
                                        rb_optionc.setBackgroundColor(Color.parseColor("#8EC3A3"))
                                        //    rb_optionc.setCircleColor(Color.parseColor("#F89A9D"));

                                    }
                                    else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                                        rb_optiond.isChecked = true
                                        rb_optiond.setBackgroundColor(Color.parseColor("#8EC3A3"))
                                        // rb_optiond.setCircleColor(Color.parseColor("#F89A9D"));

                                    }

                                }
                            }
                            else{
                                if (!selectedanswer.contains(correctanswer)) {
                                    //computeranswerstatus = true
                                    //disable all radiobuttons
                                    rb_optiona.isEnabled = false
                                    rb_optionb.isEnabled = false
                                    rb_optionc.isEnabled = false
                                    rb_optiond.isEnabled = false


                                    //red colour
                                    if(selectedanswer.contains("A.  ",ignoreCase = true)){
                                        rb_optiona.isChecked = true
                                        rb_optiona.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        //   rb_optiona.setCircleColor(Color.parseColor("#F89A9D"))
                                    }
                                    else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                                        rb_optionb.isChecked = true
                                        rb_optionb.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        // rb_optionb.setCircleColor(Color.parseColor("#F89A9D"));

                                    }
                                    else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                                        rb_optionc.isChecked = true
                                        rb_optionc.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        //    rb_optionc.setCircleColor(Color.parseColor("#F89A9D"));

                                    }
                                    else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                                        rb_optiond.isChecked = true
                                        rb_optiond.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        // rb_optiond.setCircleColor(Color.parseColor("#F89A9D"));

                                    }

                                }
                                else if(selectedanswer.contains(correctanswer)){
                                    //red colour
                                    if(selectedanswer.contains("A.  ",ignoreCase = true)){
                                        rb_optiona.isChecked = true
                                        rb_optiona.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        //   rb_optiona.setCircleColor(Color.parseColor("#F89A9D"))
                                    }
                                    else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                                        rb_optionb.isChecked = true
                                        rb_optionb.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        // rb_optionb.setCircleColor(Color.parseColor("#F89A9D"));

                                    }
                                    else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                                        rb_optionc.isChecked = true
                                        rb_optionc.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        //    rb_optionc.setCircleColor(Color.parseColor("#F89A9D"));

                                    }
                                    else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                                        rb_optiond.isChecked = true
                                        rb_optiond.setBackgroundColor(Color.parseColor("#F89A9D"))
                                        // rb_optiond.setCircleColor(Color.parseColor("#F89A9D"));

                                    }
                                }
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
    })*/
}

    private fun getQuestions() {
        quizlist.clear()
        Log.e("msg", "VALUESQUIZQUIZZZZZZZZ118999:" + quizidindex)


        if(!tv_secondname.text.toString().equals("Aditya")){
            randomquizid  =  3
//                                                        randomquizid  =  Random().nextInt(quizlist.size) + 1
            Log.e("msg", "VALUESQUIZQUIZZZZZZZZ112:" + randomquizid)

            // set quiz index on db
            val reffg1 = FirebaseDatabase.getInstance().reference.child("quizscreen")
            reffg1.orderByChild("name").equalTo(studentname).
                    addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(dataSnapshotquiz: DataSnapshot) {
                            if (dataSnapshotquiz.exists()) {
                                for (datasquiz in dataSnapshotquiz.children) {
                                    val keyquiz = datasquiz.key
                                    reffg1.child(keyquiz!!).child("quizindex").setValue(randomquizid)
                                    //quizidindex=datasquiz.child("quizindex").value.toString()


                                }
                              quizidindex=randomquizid.toString()
                                quizidnext=randomquizid
                                Log.e("msg", "VALUESQUIZQUIZZZZZZZZ113:" + quizidnext)
                                ed_loginsave.putInt("quizid_index", quizidnext)
                                ed_loginsave.commit()
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                             Toast.makeText(applicationContext, "" + p0.toException(), Toast.LENGTH_SHORT).show()
                        }
                    })
        }


        val reference3 = FirebaseDatabase.getInstance().reference.child("admin-quiz")
        reference3.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot1: DataSnapshot) {
                for (datas1 in dataSnapshot1.children) {
                    no_quizlist.add(datas1.child("class").getValue().toString())
                    try {


                            Log.e("msg", "VALUESQUIZQUIZZZZZZZZ119:" + quizidindex)

                            val rootReff3 = FirebaseDatabase.getInstance().reference.child("admin-quiz").child(datas1.key.toString())
                            if (datas1.child("class").getValue().toString().contains(standard, ignoreCase = true)) {
                                // Log.e("msg", "VALUESQUIZQUIZZZZZZZZ114:" + datas1.child("quizid").getValue().toString())
                                Log.e("msg", "VALUESQUIZQUIZZZZZZZZ115:" + quizidindex)


                                //Toast.makeText(applicationContext, "QuizId1:" + datas1.child("quizid").getValue().toString(), Toast.LENGTH_LONG).show()
                                if (datas1.child("quizid").getValue().toString().equals(quizidindex)) {
                                    Log.e("msg", "VALUESQUIZQUIZZZZZZZZ116:" + datas1.child("quizid").getValue().toString())
                                    Log.e("msg", "VALUESQUIZQUIZZZZZZZZ117:" + quizidindex)
                                    //      Toast.makeText(applicationContext, "Index2:" + quizidindex, Toast.LENGTH_LONG).show()
                                    //        Toast.makeText(applicationContext, "QuizId2:" + datas1.child("quizid").getValue().toString(), Toast.LENGTH_LONG).show()
                                    //  Toast.makeText(applicationContext, "Index1:" + quizidindex, Toast.LENGTH_LONG).show()
                                    quizkey = datas1.key.toString()

                                    rootReff3.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot2: DataSnapshot) {

                                            val rootreff4 = rootReff3.child("questions")
                                            rootreff4.addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(datasnapshot3: DataSnapshot) {

                                                    for (datas3 in datasnapshot3.children) {
                                                        val q = QuizModel(datas3.key.toString(),
                                                                datas3.child("description").getValue().toString(),
                                                                datas3.child("optiona").getValue().toString(), datas3.child("optionb").getValue().toString(),
                                                                datas3.child("optionc").getValue().toString(), datas3.child("optiond").getValue().toString(),
                                                                datas3.child("answer").getValue().toString())
                                                        quizlist.add(q)
                                                    }




                                                    try {
                                                        tv_quesno.setText("Q" + (j + 1).toString() + ": ")
                                                        tv_ques.setText(quizlist.get(0).description)
                                                        rb_optiona.setText("A.  " + quizlist.get(0).a)
                                                        rb_optionb.setText("B.  " + quizlist.get(0).b)
                                                        rb_optionc.setText("C.  " + quizlist.get(0).c)
                                                        rb_optiond.setText("D.  " + quizlist.get(0).d)
                                                        correctanswer = quizlist.get(0).answer

                                                        rdno_quizlist = Random().nextInt(quizlist.size-1)
                                                        Log.e("msg","QuizIndexLISTTT333::"+rdno_quizlist)



                                                    } catch (e: Exception) {
                                                        //  Toast.makeText(applicationContext, "QuestionException:" + e.toString(), Toast.LENGTH_SHORT).show()

                                                    }
                                                }

                                                override fun onCancelled(p0: DatabaseError) {
                                                    Toast.makeText(applicationContext, "Error2222:" + p0.toString(), Toast.LENGTH_SHORT).show()
                                                    finish()

                                                }


                                            })


                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            Toast.makeText(applicationContext, "Error333:" + databaseError.toString(), Toast.LENGTH_SHORT).show()
                                            finish()

                                        }
                                    })


//store quiz id in shared preference
                                    if(quizidindex.toInt()== quizlist.size){
                                        quizidindex="1"
                                        quizidnext=quizidindex.toInt()
                                        ed_loginsave.putInt("quizid_index", quizidnext)
                                        ed_loginsave.commit()
                                    }
                                    else{
                                        quizidnext = quizidindex.toInt()
                                        quizidnext=quizidnext+1
                                        ed_loginsave.putInt("quizid_index", quizidnext)
                                        ed_loginsave.commit()

                                    }

                                    //set status false
                                    val reff20 = FirebaseDatabase.getInstance().reference.child("admin-quiz")
                                    reff20.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot8: DataSnapshot) {
                                            if (dataSnapshot8.exists()) {
                                                for (datas8 in dataSnapshot8.children) {
                                                    if (datas8.child("class").getValue().toString().contains(standard, ignoreCase = true)) {
                                                        if (datas8.key.toString().equals(quizkey)) {
                                                            val key8 = datas8.key
                                                            Log.e("msg", "VALUESQUIZ1133366688899:" + key8)
                                                            val quizstatus = datas8.child("quizstatus").value.toString()

                                                            reff20.child(key8!!).child("quizstatus").setValue(false)

                                                        }

                                                    }

                                                }

                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {
                                            Log.e("msg", "VALUESQUIZ11333666:" + p0.toString())

                                        }
                                    })


                                }
                                else{
                                    /* if(quizidindex.toInt()>= quizlist.size){
                                         quizidindex="1"
                                         quizidnext=quizidindex.toInt()
                                         ed_loginsave.putInt("quizid_index", quizidnext)
                                         ed_loginsave.commit()
                                         getQuestions()

                                     }
                                     else{}
                                     break*/


                                }
                                //     break

                            } else {
                                finish()
                                Toast.makeText(applicationContext, "No Quiz Available for " + standard, Toast.LENGTH_LONG).show()
                            }




                        //pb_videos.setVisibility(View.GONE)
                    } catch (e: Exception) {
                        Log.e("msg", "VALUESQUIZ:" + e.toString())
                    }
                    // }


                }

                if(quizidindex.toInt()> no_quizlist.size){

                    quizidindex="1"
                    quizidnext=quizidindex.toInt()
                    ed_loginsave.putInt("quizid_index", quizidnext)
                    ed_loginsave.commit()
                    Log.e("msg", "VALUESQUIZQUIZZZZZZZZ118:" + quizidindex)

                    getQuestions()
                   
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, "Error4:" + databaseError.toString(), Toast.LENGTH_SHORT).show()
                finish()
            }
        })


    }


    private fun nextQues() {
        try{
            if(quizwrongans.equals("wrongans")){
                refseconduserr.child(keyseconduserr).child("markanswrong").setValue("")
            }
        }
        catch (e:Exception){}


        var checklikedposts2:Boolean=false
            autosubmitques = false
            //   Toast.makeText(applicationContext,"Correct Answer: "+quizlist.get(j).answer,Toast.LENGTH_SHORT).show()


        if (j < (quizlist.size - 1)) {

                //finish previous timer
                try {
                    countDownTimer.cancel()
                    timer1.cancel()
                    countDownFlagTimer.cancel()

                } catch (e: Exception) {
                }

                //start timer
                mProgressBar.setVisibility(View.INVISIBLE)
            Log.e("msg", "QuizAnsSnehaaaaaaa0000000343:"+testflag+"   ,  "+flagindexfour)
            if(flagindexfour.equals("cancelled") && !tv_secondname.text.toString().equals("Aditya")){
                flagindexfour="renew"
            }

                if(tv_secondname.text.toString().equals("Aditya")){
                    startTimer()
                }
                else if(!tv_secondname.text.toString().equals("Aditya")){
                    countDownFlagTimer = object: CountDownTimer(30000, 1) {
                        override fun onTick(millisUntilFinished: Long) {
                            if(!tv_secondname.text.toString().equals("Aditya")){

                                val reffquizans1 = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                                val queryans1: com.google.firebase.database.Query = reffquizans1.
                                        orderByChild("username").equalTo(tv_secondname.text.toString())
                                queryans1.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshotquiz1: DataSnapshot) {
                                        if (dataSnapshotquiz1.exists()) {
                                           for(dtqq1 in dataSnapshotquiz1.children){
                                                val quizanskey2 = dtqq1.key
                                                //  Handler().postDelayed()
                                                //Log.e("msg", "QuizAnsSnehaaaaaaa0000000:"+flagindexfour)

                                                if(dtqq1.child("flagstatus").value.toString().equals("false")
                                                        && !testflag.equals("yes")){
                                                    flagindexfour="false"
                                                    testflag="yes"
                                                    countDownFlagTimer.cancel()
                                                    startTimer()
                                                    /*if(testflag.equals("yes")){
                                                        testflag=""
                                                        countDownFlagTimer.cancel()
                                                    }*/
                                                    Handler().postDelayed({
                                                        testflag=""
                                                        countDownFlagTimer.cancel()
                                                    },4000)
                                                    Log.e("msg", "QuizAnsSnehaaaaaaa0000000341:"+flagindexfour)
                                                    break
                                                }
                                                else if(flagindexfour.equals("renew") && !testflag.equals("yes")){
                                                    flagindexfour="false"
                                                    testflag=""
                                                    Log.e("msg", "QuizAnsSnehaaaaaaa0000000342:"+flagindexfour)
                                                    countDownFlagTimer.cancel()
                                                    startTimer()
                                                    break
                                                }
                                                break
                                            }
                                        }
                                    }
                                    override fun onCancelled(p0: DatabaseError) {
                                    }
                                })

                            }
                        }

                        override fun onFinish() {

                        }
                    }
                    countDownFlagTimer.start()
                }


                //Log.e("msg", "QuizAnsSnehaaaaaaa00111:"+flagindexfour)

                mProgressBar1.setVisibility(View.VISIBLE)

                //uncheck previous checkbox

                rb_optiona.isChecked = false
                rb_optionb.isChecked = false
                rb_optionc.isChecked = false
                rb_optiond.isChecked = false

                //enable all radiobutton
                rb_optiona.isEnabled = true
                rb_optionb.isEnabled = true
                rb_optionc.isEnabled = true
                rb_optiond.isEnabled = true

                rb_optiona.setBackgroundColor(Color.WHITE)
                rb_optionb.setBackgroundColor(Color.WHITE)
                rb_optionc.setBackgroundColor(Color.WHITE)
                rb_optiond.setBackgroundColor(Color.WHITE)


//check whether no ans given by user ,then at 10 seconds computer will ans
                if (computeranswerstatus.equals(true)) {
                    computeranswerstatus = false
                    coins2 = coins2 + 5
                    tv_secondscore.setText(coins2.toString())

                    val rootRef1 = FirebaseDatabase.getInstance().reference.child("computercoins")
                    rootRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val ref = FirebaseDatabase.getInstance().reference
                                        .child("computercoins")
                                ref.orderByChild("name").equalTo("Aditya").addListenerForSingleValueEvent(object : ValueEventListener {

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (datas in dataSnapshot.children) {
                                                val key = datas.key
                                                val points = datas.child("points").value.toString()
                                                ref.child(key!!).child("points").setValue(coins2)
                                            }
                                        }

                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }
                                })
                            } else {
                                val post = PointsModel("Aditya", coins2, 0)
                                val missionsReference = FirebaseDatabase.getInstance().reference.child("computercoins").child("Aditya")
                                missionsReference.setValue(post)
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }


                    })

                } else {
                    //match correct answer with user's selected answer
                    if (selectedanswer.contains(quizlist.get(j).answer, ignoreCase = true)) {
                        no_correctans++


                        //get timstamp of user
                        val refftimestampuser = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                        val queryuser: com.google.firebase.database.Query = refftimestampuser.
                                orderByChild("username").equalTo(studentname)
                        queryuser.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshotuser: DataSnapshot) {
                                if (dataSnapshotuser.exists()) {
                                    for(dtq1 in dataSnapshotuser.children){
                                        timestampuser=dtq1.child("timestamp").value.toString()
                                        //  Log.e("msg", "VALUESQUIZTimestamp1:$timestampuser")
                                    }
//get timstamp of second user

                                    val queryseconduser: com.google.firebase.database.Query = refftimestampuser.
                                            orderByChild("username").equalTo(tv_secondname.text.toString())
                                    queryseconduser.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshotsecond: DataSnapshot) {
                                            if (dataSnapshotsecond.exists()) {
                                                for(dtq2 in dataSnapshotsecond.children){
                                                    timestampseconduser=dtq2.child("timestamp").value.toString()
                                                    // Log.e("msg", "VALUESQUIZTimestamp2:$timestampseconduser")


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


                        if(tv_secondname.text.toString().equals("Aditya")){
                            coinsuser = coinsuser + 5
                            usertotalpoints = usertotalpoints + 5
                            tv_ownerscore.setText(coinsuser.toString())

                            val rootRef = FirebaseDatabase.getInstance().reference.child("coins")
                            rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val ref = FirebaseDatabase.getInstance().reference
                                                .child("coins")
                                        ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {

                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (datas in dataSnapshot.children) {
                                                        val key = datas.key
                                                        val points = datas.child("points").value.toString()
                                                        ref.child(key!!).child("points").setValue(coinsuser)
                                                        ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)

                                                    }
                                                }

                                            }

                                            override fun onCancelled(p0: DatabaseError) {

                                            }
                                        })
                                    } else {
                                        val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                        val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                        missionsReference.setValue(post)
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }


                            })
                        }
                       // else{

                           /* Handler().postDelayed({

                                if ( Math.abs(timestampseconduser.toLong()) > Math.abs(timestampuser.toLong()) ) {
                                    //timestamp2 is later than timestamp1
                                    //red colour

                                    coinsuser = coinsuser + 5
                                    usertotalpoints = usertotalpoints + 5
                                    tv_ownerscore.setText(coinsuser.toString())
                                    //tv_secondscore.setText(((tv_secondscore.text.toString()).toInt() +5).toString())
                                    Log.e("msg", "VALUESQUIZTimestamp3:$coinsuser")
                                    val rootRef = FirebaseDatabase.getInstance().reference.child("coins")
                                    rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val ref = FirebaseDatabase.getInstance().reference
                                                        .child("coins")
                                                ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {

                                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            for (datas in dataSnapshot.children) {
                                                                val key = datas.key
                                                                val points = datas.child("points").value.toString()
                                                                ref.child(key!!).child("points").setValue(coinsuser)
                                                                ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)

                                                            }
                                                        }

                                                    }

                                                    override fun onCancelled(p0: DatabaseError) {

                                                    }
                                                })
                                            } else {
                                                val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                missionsReference.setValue(post)
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {

                                        }


                                    })
                                }
                            }, 2000)*/
                        //}



                        val uidRef1 = FirebaseDatabase.getInstance().reference.child("coins")
                        val valueEventListener1 = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (ds in dataSnapshot.getChildren()) {

                                    if (tv_secondname.text.toString().equals(ds.key, ignoreCase = true)) {
                                        // Toast.makeText(applicationContext,"CoinsSeconduser:"+coinsseconduser,Toast.LENGTH_LONG).show()
                                        coinsseconduser=0
                                        coinsseconduser = (ds.child("points").getValue() as Long).toInt()
                                        if(flagindexfour.equals("currentuser")){}
                                        else{
                                            coinsseconduser=coinsseconduser+5
                                        }

                                        tv_secondscore.setText(coinsseconduser.toString())


                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d("msg", databaseError.message) //Don't ignore errors!
                            }
                        }
                        uidRef1.addListenerForSingleValueEvent(valueEventListener1)

                        if (tv_secondname.text.toString().equals("Aditya")) {
                            coins2 = coins2 + 0
                            tv_secondscore.setText(coins2.toString())
                            val rootRef1 = FirebaseDatabase.getInstance().reference.child("computercoins")
                            rootRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val ref = FirebaseDatabase.getInstance().reference
                                                .child("computercoins")
                                        ref.orderByChild("name").equalTo("Aditya").addListenerForSingleValueEvent(object : ValueEventListener {

                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (datas in dataSnapshot.children) {
                                                        val key = datas.key
                                                        val points = datas.child("points").value.toString()
                                                        ref.child(key!!).child("points").setValue(coins2)
                                                    }
                                                }

                                            }

                                            override fun onCancelled(p0: DatabaseError) {

                                            }
                                        })
                                    } else {
                                        val post = PointsModel("Aditya", coins2, 0)
                                        val missionsReference = FirebaseDatabase.getInstance().reference.child("computercoins").child("Aditya")
                                        missionsReference.setValue(post)
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }


                            })
                        }



                    }
                    else {

                        no_wrongans++
                        tv_ownerscore.setText(coinsuser.toString())


                        val rootRef = FirebaseDatabase.getInstance().reference.child("coins")
                        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val ref = FirebaseDatabase.getInstance().reference
                                            .child("coins")
                                    ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {

                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (datas in dataSnapshot.children) {
                                                    val key = datas.key
                                                    val points = datas.child("points").value.toString()
                                                    ref.child(key!!).child("points").setValue(coinsuser)
                                                    ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)

                                                }
                                            }

                                        }

                                        override fun onCancelled(p0: DatabaseError) {

                                        }
                                    })
                                } else {
                                    val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                    val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                    missionsReference.setValue(post)
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }


                        })



                        if (tv_secondname.text.toString().equals("Aditya")) {
                            coins2 = coins2 + 5
                            tv_secondscore.setText(coins2.toString())
                            val rootRef1 = FirebaseDatabase.getInstance().reference.child("computercoins")
                            rootRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val ref = FirebaseDatabase.getInstance().reference
                                                .child("computercoins")
                                        ref.orderByChild("name").equalTo("Aditya").addListenerForSingleValueEvent(object : ValueEventListener {

                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (datas in dataSnapshot.children) {
                                                        val key = datas.key
                                                        val points = datas.child("points").value.toString()
                                                        ref.child(key!!).child("points").setValue(coins2)
                                                    }
                                                }

                                            }

                                            override fun onCancelled(p0: DatabaseError) {

                                            }
                                        })
                                    } else {
                                        val post = PointsModel("Aditya", coins2, 0)
                                        val missionsReference = FirebaseDatabase.getInstance().reference.child("computercoins").child("Aditya")
                                        missionsReference.setValue(post)
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }


                            })
                        }

                        val uidRef1 = FirebaseDatabase.getInstance().reference.child("coins")
                        val valueEventListener1 = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (ds in dataSnapshot.getChildren()) {

                                    if (tv_secondname.text.toString().equals(ds.key, ignoreCase = true)) {
                                        coinsseconduser=0
                                        coinsseconduser = (ds.child("points").getValue() as Long).toInt()
                                        tv_secondscore.setText(coinsseconduser.toString())


                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d("msg", databaseError.message) //Don't ignore errors!
                            }
                        }
                        uidRef1.addListenerForSingleValueEvent(valueEventListener1)
                    }
                }


                //show next question
                j++
                tv_quesno.setText("Q" + (j + 1).toString() + ": ")
                tv_ques.setText(quizlist.get(j).description)
                rb_optiona.setText("A.  " + quizlist.get(j).a)
                rb_optionb.setText("B.  " + quizlist.get(j).b)
                rb_optionc.setText("C.  " + quizlist.get(j).c)
                rb_optiond.setText("D.  " + quizlist.get(j).d)
                correctanswer = quizlist.get(j).answer
                selectedanswer = ""





            } else {

                //check last question
                val progressDialog = ProgressDialog(this)
                try{

                    progressDialog.setTitle("Submit Quiz")
                    progressDialog.setMessage("Please wait....")
                    progressDialog.show()
                    progressDialog.setCancelable(false)
                    progressDialog.setCanceledOnTouchOutside(false)
                }

                catch (e:WindowManager.BadTokenException ) {
                    //use a log message
                }
                btn_nextquiz.isEnabled = false
                // if last ques ,then display ad and onclick continue btn,goto result screen
                     ll_quiz.visibility=View.GONE
                     fm_quizpost.visibility=View.VISIBLE
                     if(quizpost.urlpath.contains("jpg",ignoreCase = true) ||
                             quizpost.urlpath.contains("png",ignoreCase = true)
                             || quizpost.urlpath.contains("jpeg",ignoreCase = true)){
                         iv_quizfeed.visibility= View.VISIBLE
                         vdd_quizfeed.visibility= View.GONE
                         Picasso.get().load(quizpost.urlpath).into(iv_quizfeed)
                         progressDialog.dismiss()

                         //show timer of 20 seconds and show cross and continue button after that
                        showadTimer()


                         val reff2 = FirebaseDatabase.getInstance().reference.child("ViewAdsCount")

                         reff2.addListenerForSingleValueEvent(object : ValueEventListener {
                             override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                                 if (dataSnapshotliked.exists()) {
                                     for (datasliked in dataSnapshotliked.children) {

                                         //check whether same ad viewed by same student exists in database
                                         if (datasliked.child("username").value.toString().equals(studentname)
                                                 && datasliked.child("postname").value.toString().equals(quizpost.urlpath))
                                         {
                                             checklikedposts2=true

                                         }

                                     }

                                     object : CountDownTimer(100, 100) {
                                         override fun onTick(millisUntilFinished: Long) { // You don't need to use this.
                                         }

                                         override fun onFinish() {
                                             if(checklikedposts2==false){
                                                 val post = LikePostsmodel(studentname,quizpost.urlpath,quizpost.filename)
                                                 val missionsReference = FirebaseDatabase.getInstance().reference.child("ViewAdsCount").push()
                                                 missionsReference.setValue(post)

                                                 getadsviews( quizpost.urlpath,quizpost.filename)
                                               /*  liketotalinterface.getadsposition(position,
                                                         postslist.get(position).urlpath,postslist.get(position).filename)*/




                                             }
                                         }
                                     }.start()



                                 }
                                 else {
                                     val post = LikePostsmodel(studentname,quizpost.urlpath,quizpost.filename)
                                     val missionsReference = FirebaseDatabase.getInstance().reference.child("ViewAdsCount").push()
                                     missionsReference.setValue(post)
                                     getadsviews( quizpost.urlpath,quizpost.filename)

                                     /*  liketotalinterface.getadsposition(position,
                                               postslist.get(position).urlpath,postslist.get(position).filename)*/

                                 }
                             }

                             override fun onCancelled(p0: DatabaseError) {
                             }
                         })
                     }
                     else if(quizpost.urlpath.contains("mp4",ignoreCase = true) ||
                             quizpost.urlpath.contains("3gp",ignoreCase = true)
                             || quizpost.urlpath.contains("gif",ignoreCase = true)
                             || quizpost.urlpath.contains("avi",ignoreCase = true)){

                         iv_quizfeed.setImageResource(0)
                         if(null!=vdd_quizfeed){
                             vdd_quizfeed.stopPlayback()
                             vdd_quizfeed.resume()
                         }
                         Glide.with(applicationContext)
                                 .load(quizpost.urlpath)
                                 .dontAnimate()
                                 .diskCacheStrategy(DiskCacheStrategy.ALL)
                                 .thumbnail(0.5f)
                                 .centerInside()
                                 .into(iv_quizfeed)
                         vdd_quizfeed.visibility= View.VISIBLE

                         vdd_quizfeed.setVideoURI(Uri.parse(quizpost.urlpath))
                         vdd_quizfeed.requestFocus()

                         vdd_quizfeed.setOnPreparedListener {
                             iv_quizfeed.visibility= View.GONE
                             vdd_quizfeed.start()
                             it.isLooping=true
                             // pb_videopost.visibility=View.GONE
                         }
                         progressDialog.dismiss()

                         //show timer of 20 seconds and show cross and continue button after that
                        showadTimer()

                         val reff2 = FirebaseDatabase.getInstance().reference.child("ViewAdsCount")

                         reff2.addListenerForSingleValueEvent(object : ValueEventListener {
                             override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                                 if (dataSnapshotliked.exists()) {
                                     for (datasliked in dataSnapshotliked.children) {

                                         //check whether same ad viewed by same student exists in database
                                         if (datasliked.child("username").value.toString().equals(studentname)
                                                 && datasliked.child("postname").value.toString().equals(quizpost.urlpath))
                                         {
                                             checklikedposts2=true

                                         }

                                     }

                                     object : CountDownTimer(100, 100) {
                                         override fun onTick(millisUntilFinished: Long) { // You don't need to use this.
                                         }

                                         override fun onFinish() {
                                             if(checklikedposts2==false){
                                                 val post = LikePostsmodel(studentname,quizpost.urlpath,quizpost.filename)
                                                 val missionsReference = FirebaseDatabase.getInstance().reference.child("ViewAdsCount").push()
                                                 missionsReference.setValue(post)

                                                 getadsviews( quizpost.urlpath,quizpost.filename)
                                                 /*  liketotalinterface.getadsposition(position,
                                                           postslist.get(position).urlpath,postslist.get(position).filename)*/




                                             }
                                         }
                                     }.start()



                                 }
                                 else {
                                     val post = LikePostsmodel(studentname,quizpost.urlpath,quizpost.filename)
                                     val missionsReference = FirebaseDatabase.getInstance().reference.child("ViewAdsCount").push()
                                     missionsReference.setValue(post)
                                     getadsviews( quizpost.urlpath,quizpost.filename)

                                     /*  liketotalinterface.getadsposition(position,
                                               postslist.get(position).urlpath,postslist.get(position).filename)*/

                                 }
                             }

                             override fun onCancelled(p0: DatabaseError) {
                             }
                         })

                     }
                     Log.e("msg","QuizIndexLISTTT3334::"+rdno_quizlist)



                iv_cross.setOnClickListener {
                    nextbtnclick_lastques = true
                    iv_cross.isEnabled = false
                    //check last question
                    try {

                        //check whether no ans given by user ,then at 10 seconds computer will ans
                        if (computeranswerstatus.equals(true)) {
                            computeranswerstatus = false
                            coins2 = coins2 + 5
                            tv_secondscore.setText(coins2.toString())

                            val rootRef1 = FirebaseDatabase.getInstance().reference.child("computercoins")
                            rootRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val ref = FirebaseDatabase.getInstance().reference
                                                .child("computercoins")
                                        ref.orderByChild("name").equalTo("Aditya").addListenerForSingleValueEvent(object : ValueEventListener {

                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (datas in dataSnapshot.children) {
                                                        val key = datas.key
                                                        val points = datas.child("points").value.toString()
                                                        ref.child(key!!).child("points").setValue(coins2)
                                                        val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                        intent.putExtra("correctans", no_correctans.toString())
                                                        intent.putExtra("wrongans", no_wrongans.toString())
                                                        intent.putExtra("username1", studentname)
                                                        intent.putExtra("username2", tv_secondname.text.toString())
                                                        intent.putExtra("coinsuser", coinsuser.toString())
                                                        intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                        intent.putExtra("coinscomputer", coins2.toString())
                                                        intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }

                                            }

                                            override fun onCancelled(p0: DatabaseError) {

                                            }
                                        })
                                    } else {
                                        val post = PointsModel("Aditya", coins2, 0)
                                        val missionsReference = FirebaseDatabase.getInstance().reference.child("computercoins").child("Aditya")
                                        missionsReference.setValue(post)
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }


                            })

                        } else {
                            if (selectedanswer.contains(quizlist.get(j).answer, ignoreCase = true)) {
                                no_correctans++

                                //get timstamp of user
                                val refftimestampuser = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                                val queryuser: com.google.firebase.database.Query = refftimestampuser.
                                        orderByChild("username").equalTo(studentname)
                                queryuser.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshotuser: DataSnapshot) {
                                        if (dataSnapshotuser.exists()) {
                                            for(dtq1 in dataSnapshotuser.children){
                                                timestampuser=dtq1.child("timestamp").value.toString()
                                                //  Log.e("msg", "VALUESQUIZTimestamp1:$timestampuser")
                                            }
//get timstamp of second user

                                            val queryseconduser: com.google.firebase.database.Query = refftimestampuser.
                                                    orderByChild("username").equalTo(tv_secondname.text.toString())
                                            queryseconduser.addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(dataSnapshotsecond: DataSnapshot) {
                                                    if (dataSnapshotsecond.exists()) {
                                                        for(dtq2 in dataSnapshotsecond.children){
                                                            timestampseconduser=dtq2.child("timestamp").value.toString()
                                                            // Log.e("msg", "VALUESQUIZTimestamp2:$timestampseconduser")


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

                                if(tv_secondname.text.toString().equals("Aditya")) {
                                    coinsuser = coinsuser + 5
                                    usertotalpoints = usertotalpoints + 5
                                    tv_ownerscore.setText(coinsuser.toString())
                                    val ref = FirebaseDatabase.getInstance().reference.child("coins")
                                    ref.orderByChild("name").equalTo(studentname).
                                            addListenerForSingleValueEvent(object : ValueEventListener {

                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (datas in dataSnapshot.children) {
                                                            if (datas.key.toString().equals(studentname)) {
                                                                val key = datas.key
                                                                val points = datas.child("points").value.toString()
                                                                ref.child(key!!).child("points").setValue(coinsuser)
                                                                ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)
                                                                val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                                intent.putExtra("correctans", no_correctans.toString())
                                                                intent.putExtra("wrongans", no_wrongans.toString())
                                                                intent.putExtra("username1", studentname)
                                                                intent.putExtra("username2", tv_secondname.text.toString())
                                                                intent.putExtra("coinsuser", coinsuser.toString())
                                                                intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                                intent.putExtra("coinscomputer", coins2.toString())
                                                                intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                                startActivity(intent)
                                                                finish()
                                                            } else {
                                                                val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                                val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                                missionsReference.setValue(post)
                                                                val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                                intent.putExtra("correctans", no_correctans.toString())
                                                                intent.putExtra("wrongans", no_wrongans.toString())
                                                                intent.putExtra("username1", studentname)
                                                                intent.putExtra("username2", tv_secondname.text.toString())
                                                                intent.putExtra("coinsuser", coinsuser.toString())
                                                                intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                                intent.putExtra("coinscomputer", coins2.toString())
                                                                intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                                startActivity(intent)
                                                                finish()
                                                            }


                                                        }

                                                    } else {
                                                        val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                        val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                        missionsReference.setValue(post)
                                                        val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                        intent.putExtra("correctans", no_correctans.toString())
                                                        intent.putExtra("wrongans", no_wrongans.toString())
                                                        intent.putExtra("username1", studentname)
                                                        intent.putExtra("username2", tv_secondname.text.toString())
                                                        intent.putExtra("coinsuser", coinsuser.toString())
                                                        intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                        intent.putExtra("coinscomputer", coins2.toString())
                                                        intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                        startActivity(intent)
                                                        finish()
                                                    }

                                                }

                                                override fun onCancelled(p0: DatabaseError) {
                                                    //  Toast.makeText(applicationContext, "SUBMIT TEST7:"+p0.toString(), Toast.LENGTH_SHORT).show()

                                                }
                                            })


                                }
                                else{
                                    //if (Math.abs(timestampseconduser.toLong()) > Math.abs(timestampuser.toLong())) {
                                        //timestamp2 is later than timestamp1
                                        coinsuser = coinsuser + 5
                                        usertotalpoints = usertotalpoints + 5
                                        tv_ownerscore.setText(coinsuser.toString())
                                    //}


                                            val ref = FirebaseDatabase.getInstance().reference.child("coins")
                                            ref.orderByChild("name").equalTo(studentname).
                                                    addListenerForSingleValueEvent(object : ValueEventListener {

                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                for (datas in dataSnapshot.children) {
                                                                    if (datas.key.toString().equals(studentname)) {
                                                                        val key = datas.key
                                                                        val points = datas.child("points").value.toString()
                                                                        ref.child(key!!).child("points").setValue(coinsuser)
                                                                        ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)
                                                                        val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                                        intent.putExtra("correctans", no_correctans.toString())
                                                                        intent.putExtra("wrongans", no_wrongans.toString())
                                                                        intent.putExtra("username1", studentname)
                                                                        intent.putExtra("username2", tv_secondname.text.toString())
                                                                        intent.putExtra("coinsuser", coinsuser.toString())
                                                                        intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                                        intent.putExtra("coinscomputer", coins2.toString())
                                                                        intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                                        startActivity(intent)
                                                                        finish()
                                                                    } else {
                                                                        val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                                        val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                                        missionsReference.setValue(post)
                                                                        val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                                        intent.putExtra("correctans", no_correctans.toString())
                                                                        intent.putExtra("wrongans", no_wrongans.toString())
                                                                        intent.putExtra("username1", studentname)
                                                                        intent.putExtra("username2", tv_secondname.text.toString())
                                                                        intent.putExtra("coinsuser", coinsuser.toString())
                                                                        intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                                        intent.putExtra("coinscomputer", coins2.toString())
                                                                        intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                                        startActivity(intent)
                                                                        finish()
                                                                    }


                                                                }

                                                            } else {
                                                                val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                                val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                                missionsReference.setValue(post)
                                                                val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                                intent.putExtra("correctans", no_correctans.toString())
                                                                intent.putExtra("wrongans", no_wrongans.toString())
                                                                intent.putExtra("username1", studentname)
                                                                intent.putExtra("username2", tv_secondname.text.toString())
                                                                intent.putExtra("coinsuser", coinsuser.toString())
                                                                intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                                intent.putExtra("coinscomputer", coins2.toString())
                                                                intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                                startActivity(intent)
                                                                finish()
                                                            }

                                                        }

                                                        override fun onCancelled(p0: DatabaseError) {
                                                            //  Toast.makeText(applicationContext, "SUBMIT TEST7:"+p0.toString(), Toast.LENGTH_SHORT).show()

                                                        }
                                                    })








                                }
                                //get coins of second user
                                val uidRef1 = FirebaseDatabase.getInstance().reference.child("coins")
                                val valueEventListener1 = object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (ds in dataSnapshot.getChildren()) {

                                            if (tv_secondname.text.toString().equals(ds.key, ignoreCase = true)) {
                                                coinsseconduser=0
                                                coinsseconduser = (ds.child("points").getValue() as Long).toInt()
                                                tv_secondscore.setText(coinsseconduser.toString())


                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.d("msg", databaseError.message) //Don't ignore errors!
                                    }
                                }
                                uidRef1.addListenerForSingleValueEvent(valueEventListener1)

                                if (tv_secondname.text.toString().equals("Aditya")) {
                                    coins2 = coins2 + 0
                                    tv_secondscore.setText(coins2.toString())
                                    val rootRef1 = FirebaseDatabase.getInstance().reference.child("computercoins")
                                    rootRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val ref = FirebaseDatabase.getInstance().reference
                                                        .child("computercoins")
                                                ref.orderByChild("name").equalTo("Aditya").addListenerForSingleValueEvent(object : ValueEventListener {

                                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            for (datas in dataSnapshot.children) {
                                                                val key = datas.key
                                                                val points = datas.child("points").value.toString()
                                                                ref.child(key!!).child("points").setValue(coins2)
                                                            }
                                                        }

                                                    }

                                                    override fun onCancelled(p0: DatabaseError) {

                                                    }
                                                })
                                            } else {
                                                val post = PointsModel("Aditya", coins2, 0)
                                                val missionsReference = FirebaseDatabase.getInstance().reference.child("computercoins").child("Aditya")
                                                missionsReference.setValue(post)
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {

                                        }


                                    })
                                }


                            } else {
                                no_wrongans++
                                tv_ownerscore.setText(coinsuser.toString())

                                //get coins of second user
                                val uidRef1 = FirebaseDatabase.getInstance().reference.child("coins")
                                val valueEventListener1 = object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (ds in dataSnapshot.getChildren()) {

                                            if (tv_secondname.text.toString().equals(ds.key, ignoreCase = true)) {
                                                coinsseconduser=0
                                                coinsseconduser = (ds.child("points").getValue() as Long).toInt()
                                                tv_secondscore.setText(coinsseconduser.toString())


                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.d("msg", databaseError.message) //Don't ignore errors!
                                    }
                                }
                                uidRef1.addListenerForSingleValueEvent(valueEventListener1)

                                if (tv_secondname.text.toString().equals("Aditya")) {
                                    coins2 = coins2 + 5
                                    tv_secondscore.setText(coins2.toString())
                                    val rootRef1 = FirebaseDatabase.getInstance().reference.child("computercoins")
                                    rootRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val ref = FirebaseDatabase.getInstance().reference
                                                        .child("computercoins")
                                                ref.orderByChild("name").equalTo("Aditya").addListenerForSingleValueEvent(object : ValueEventListener {

                                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            for (datas in dataSnapshot.children) {
                                                                val key = datas.key
                                                                val points = datas.child("points").value.toString()
                                                                ref.child(key!!).child("points").setValue(coins2)
                                                            }
                                                        }

                                                    }

                                                    override fun onCancelled(p0: DatabaseError) {

                                                    }
                                                })
                                            } else {
                                                val post = PointsModel("Aditya", coins2, 0)
                                                val missionsReference = FirebaseDatabase.getInstance().reference.child("computercoins").child("Aditya")
                                                missionsReference.setValue(post)
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {

                                        }


                                    })
                                }

                                val ref = FirebaseDatabase.getInstance().reference
                                        .child("coins")
                                ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (datas in dataSnapshot.children) {
                                                if (datas.key.toString().equals(studentname)) {
                                                    val key = datas.key
                                                    val points = datas.child("points").value.toString()
                                                    ref.child(key!!).child("points").setValue(coinsuser)
                                                    ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)
                                                    val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                    intent.putExtra("correctans", no_correctans.toString())
                                                    intent.putExtra("wrongans", no_wrongans.toString())
                                                    intent.putExtra("username1", studentname)
                                                    intent.putExtra("username2", tv_secondname.text.toString())
                                                    intent.putExtra("coinsuser", coinsuser.toString())
                                                    intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                    intent.putExtra("coinscomputer", coins2.toString())
                                                    intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                    startActivity(intent)
                                                    finish()
                                                } else {
                                                    val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                    val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                    missionsReference.setValue(post)
                                                    val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                    intent.putExtra("correctans", no_correctans.toString())
                                                    intent.putExtra("wrongans", no_wrongans.toString())
                                                    intent.putExtra("username1", studentname)
                                                    intent.putExtra("username2", tv_secondname.text.toString())
                                                    intent.putExtra("coinsuser", coinsuser.toString())
                                                    intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                    intent.putExtra("coinscomputer", coins2.toString())
                                                    intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                    startActivity(intent)
                                                    finish()
                                                }


                                            }

                                        } else {
                                            val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                            val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                            missionsReference.setValue(post)
                                            val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                            intent.putExtra("correctans", no_correctans.toString())
                                            intent.putExtra("wrongans", no_wrongans.toString())
                                            intent.putExtra("username1", studentname)
                                            intent.putExtra("username2", tv_secondname.text.toString())
                                            intent.putExtra("coinsuser", coinsuser.toString())
                                            intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                            intent.putExtra("coinscomputer", coins2.toString())
                                            intent.putExtra("usertotalpts", usertotalpoints.toString())
                                            startActivity(intent)
                                            finish()
                                        }

                                    }

                                    override fun onCancelled(p0: DatabaseError) {
                                        //    Toast.makeText(applicationContext, "SUBMIT TEST8:"+p0.toString(), Toast.LENGTH_SHORT).show()

                                    }
                                })


                            }
                        }


                    } catch (e: WindowManager.BadTokenException) {
                        //use a log message
                    }
                }

                iv_continue.setOnClickListener {
                    iv_continue.isEnabled = false
                    nextbtnclick_lastques = true

                    try {
                        //check whether no ans given by user ,then at 10 seconds computer will ans
                        if (computeranswerstatus.equals(true)) {
                            computeranswerstatus = false
                            coins2 = coins2 + 5
                            tv_secondscore.setText(coins2.toString())

                            val rootRef1 = FirebaseDatabase.getInstance().reference.child("computercoins")
                            rootRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val ref = FirebaseDatabase.getInstance().reference
                                                .child("computercoins")
                                        ref.orderByChild("name").equalTo("Aditya").addListenerForSingleValueEvent(object : ValueEventListener {

                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (datas in dataSnapshot.children) {
                                                        val key = datas.key
                                                        val points = datas.child("points").value.toString()
                                                        ref.child(key!!).child("points").setValue(coins2)
                                                        val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                        intent.putExtra("correctans", no_correctans.toString())
                                                        intent.putExtra("wrongans", no_wrongans.toString())
                                                        intent.putExtra("username1", studentname)
                                                        intent.putExtra("username2", tv_secondname.text.toString())
                                                        intent.putExtra("coinsuser", coinsuser.toString())
                                                        intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                        intent.putExtra("coinscomputer", coins2.toString())
                                                        intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }

                                            }

                                            override fun onCancelled(p0: DatabaseError) {

                                            }
                                        })
                                    } else {
                                        val post = PointsModel("Aditya", coins2, 0)
                                        val missionsReference = FirebaseDatabase.getInstance().reference.child("computercoins").child("Aditya")
                                        missionsReference.setValue(post)
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }


                            })

                        } else {
                            if (selectedanswer.contains(quizlist.get(j).answer, ignoreCase = true)) {
                                no_correctans++


                                //get timstamp of user
                                val refftimestampuser = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                                val queryuser: com.google.firebase.database.Query = refftimestampuser.
                                        orderByChild("username").equalTo(studentname)
                                queryuser.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshotuser: DataSnapshot) {
                                        if (dataSnapshotuser.exists()) {
                                            for(dtq1 in dataSnapshotuser.children){
                                                timestampuser=dtq1.child("timestamp").value.toString()
                                                //  Log.e("msg", "VALUESQUIZTimestamp1:$timestampuser")
                                            }
//get timstamp of second user

                                            val queryseconduser: com.google.firebase.database.Query = refftimestampuser.
                                                    orderByChild("username").equalTo(tv_secondname.text.toString())
                                            queryseconduser.addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(dataSnapshotsecond: DataSnapshot) {
                                                    if (dataSnapshotsecond.exists()) {
                                                        for(dtq2 in dataSnapshotsecond.children){
                                                            timestampseconduser=dtq2.child("timestamp").value.toString()
                                                            // Log.e("msg", "VALUESQUIZTimestamp2:$timestampseconduser")


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

                                if(tv_secondname.text.toString().equals("Aditya")) {
                                    coinsuser = coinsuser + 5
                                    usertotalpoints = usertotalpoints + 5
                                    tv_ownerscore.setText(coinsuser.toString())
                                    val ref = FirebaseDatabase.getInstance().reference.child("coins")
                                    ref.orderByChild("name").equalTo(studentname).
                                            addListenerForSingleValueEvent(object : ValueEventListener {

                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (datas in dataSnapshot.children) {
                                                            if (datas.key.toString().equals(studentname)) {
                                                                val key = datas.key
                                                                val points = datas.child("points").value.toString()
                                                                ref.child(key!!).child("points").setValue(coinsuser)
                                                                ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)
                                                                val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                                intent.putExtra("correctans", no_correctans.toString())
                                                                intent.putExtra("wrongans", no_wrongans.toString())
                                                                intent.putExtra("username1", studentname)
                                                                intent.putExtra("username2", tv_secondname.text.toString())
                                                                intent.putExtra("coinsuser", coinsuser.toString())
                                                                intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                                intent.putExtra("coinscomputer", coins2.toString())
                                                                intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                                startActivity(intent)
                                                                finish()
                                                            } else {
                                                                val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                                val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                                missionsReference.setValue(post)
                                                                val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                                intent.putExtra("correctans", no_correctans.toString())
                                                                intent.putExtra("wrongans", no_wrongans.toString())
                                                                intent.putExtra("username1", studentname)
                                                                intent.putExtra("username2", tv_secondname.text.toString())
                                                                intent.putExtra("coinsuser", coinsuser.toString())
                                                                intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                                intent.putExtra("coinscomputer", coins2.toString())
                                                                intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                                startActivity(intent)
                                                                finish()
                                                            }


                                                        }

                                                    } else {
                                                        val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                        val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                        missionsReference.setValue(post)
                                                        val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                        intent.putExtra("correctans", no_correctans.toString())
                                                        intent.putExtra("wrongans", no_wrongans.toString())
                                                        intent.putExtra("username1", studentname)
                                                        intent.putExtra("username2", tv_secondname.text.toString())
                                                        intent.putExtra("coinsuser", coinsuser.toString())
                                                        intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                        intent.putExtra("coinscomputer", coins2.toString())
                                                        intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                        startActivity(intent)
                                                        finish()
                                                    }

                                                }

                                                override fun onCancelled(p0: DatabaseError) {
                                                    //  Toast.makeText(applicationContext, "SUBMIT TEST7:"+p0.toString(), Toast.LENGTH_SHORT).show()

                                                }
                                            })


                                }
                                else{

                                    //    if (Math.abs(timestampseconduser.toLong()) > Math.abs(timestampuser.toLong())) {
                                            //timestamp2 is later than timestamp1
                                            coinsuser = coinsuser + 5
                                            usertotalpoints = usertotalpoints + 5
                                            tv_ownerscore.setText(coinsuser.toString())
                                            val ref = FirebaseDatabase.getInstance().reference.child("coins")
                                            ref.orderByChild("name").equalTo(studentname).
                                                    addListenerForSingleValueEvent(object : ValueEventListener {

                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                for (datas in dataSnapshot.children) {
                                                                    if (datas.key.toString().equals(studentname)) {
                                                                        val key = datas.key
                                                                        val points = datas.child("points").value.toString()
                                                                        ref.child(key!!).child("points").setValue(coinsuser)
                                                                        ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)
                                                                        val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                                        intent.putExtra("correctans", no_correctans.toString())
                                                                        intent.putExtra("wrongans", no_wrongans.toString())
                                                                        intent.putExtra("username1", studentname)
                                                                        intent.putExtra("username2", tv_secondname.text.toString())
                                                                        intent.putExtra("coinsuser", coinsuser.toString())
                                                                        intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                                        intent.putExtra("coinscomputer", coins2.toString())
                                                                        intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                                        startActivity(intent)
                                                                        finish()
                                                                    } else {
                                                                        val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                                        val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                                        missionsReference.setValue(post)
                                                                        val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                                        intent.putExtra("correctans", no_correctans.toString())
                                                                        intent.putExtra("wrongans", no_wrongans.toString())
                                                                        intent.putExtra("username1", studentname)
                                                                        intent.putExtra("username2", tv_secondname.text.toString())
                                                                        intent.putExtra("coinsuser", coinsuser.toString())
                                                                        intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                                        intent.putExtra("coinscomputer", coins2.toString())
                                                                        intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                                        startActivity(intent)
                                                                        finish()
                                                                    }


                                                                }

                                                            } else {
                                                                val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                                val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                                missionsReference.setValue(post)
                                                                val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                                intent.putExtra("correctans", no_correctans.toString())
                                                                intent.putExtra("wrongans", no_wrongans.toString())
                                                                intent.putExtra("username1", studentname)
                                                                intent.putExtra("username2", tv_secondname.text.toString())
                                                                intent.putExtra("coinsuser", coinsuser.toString())
                                                                intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                                intent.putExtra("coinscomputer", coins2.toString())
                                                                intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                                startActivity(intent)
                                                                finish()
                                                            }

                                                        }

                                                        override fun onCancelled(p0: DatabaseError) {
                                                            //  Toast.makeText(applicationContext, "SUBMIT TEST7:"+p0.toString(), Toast.LENGTH_SHORT).show()

                                                        }
                                                    })

                                     //   }





                                }
                                //get coins of second user
                                val uidRef1 = FirebaseDatabase.getInstance().reference.child("coins")
                                val valueEventListener1 = object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (ds in dataSnapshot.getChildren()) {

                                            if (tv_secondname.text.toString().equals(ds.key, ignoreCase = true)) {
                                                coinsseconduser=0
                                                coinsseconduser = (ds.child("points").getValue() as Long).toInt()
                                                tv_secondscore.setText(coinsseconduser.toString())


                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.d("msg", databaseError.message) //Don't ignore errors!
                                    }
                                }
                                uidRef1.addListenerForSingleValueEvent(valueEventListener1)

                                if (tv_secondname.text.toString().equals("Aditya")) {
                                    coins2 = coins2 + 0
                                    tv_secondscore.setText(coins2.toString())
                                    val rootRef1 = FirebaseDatabase.getInstance().reference.child("computercoins")
                                    rootRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val ref = FirebaseDatabase.getInstance().reference
                                                        .child("computercoins")
                                                ref.orderByChild("name").equalTo("Aditya").addListenerForSingleValueEvent(object : ValueEventListener {

                                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            for (datas in dataSnapshot.children) {
                                                                val key = datas.key
                                                                val points = datas.child("points").value.toString()
                                                                ref.child(key!!).child("points").setValue(coins2)
                                                            }
                                                        }

                                                    }

                                                    override fun onCancelled(p0: DatabaseError) {

                                                    }
                                                })
                                            } else {
                                                val post = PointsModel("Aditya", coins2, 0)
                                                val missionsReference = FirebaseDatabase.getInstance().reference.child("computercoins").child("Aditya")
                                                missionsReference.setValue(post)
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {

                                        }


                                    })
                                }







                            } else {
                                no_wrongans++
                                tv_ownerscore.setText(coinsuser.toString())

                                //get coins of second user
                                val uidRef1 = FirebaseDatabase.getInstance().reference.child("coins")
                                val valueEventListener1 = object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (ds in dataSnapshot.getChildren()) {

                                            if (tv_secondname.text.toString().equals(ds.key, ignoreCase = true)) {
                                                coinsseconduser=0
                                                coinsseconduser = (ds.child("points").getValue() as Long).toInt()
                                                tv_secondscore.setText(coinsseconduser.toString())


                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.d("msg", databaseError.message) //Don't ignore errors!
                                    }
                                }
                                uidRef1.addListenerForSingleValueEvent(valueEventListener1)

                                if (tv_secondname.text.toString().equals("Aditya")) {
                                    coins2 = coins2 + 5
                                    tv_secondscore.setText(coins2.toString())
                                    val rootRef1 = FirebaseDatabase.getInstance().reference.child("computercoins")
                                    rootRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val ref = FirebaseDatabase.getInstance().reference
                                                        .child("computercoins")
                                                ref.orderByChild("name").equalTo("Aditya").addListenerForSingleValueEvent(object : ValueEventListener {

                                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            for (datas in dataSnapshot.children) {
                                                                val key = datas.key
                                                                val points = datas.child("points").value.toString()
                                                                ref.child(key!!).child("points").setValue(coins2)
                                                            }
                                                        }

                                                    }

                                                    override fun onCancelled(p0: DatabaseError) {

                                                    }
                                                })
                                            } else {
                                                val post = PointsModel("Aditya", coins2, 0)
                                                val missionsReference = FirebaseDatabase.getInstance().reference.child("computercoins").child("Aditya")
                                                missionsReference.setValue(post)
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {

                                        }


                                    })
                                }

                                val ref = FirebaseDatabase.getInstance().reference
                                        .child("coins")
                                ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (datas in dataSnapshot.children) {
                                                if (datas.key.toString().equals(studentname)) {
                                                    val key = datas.key
                                                    val points = datas.child("points").value.toString()
                                                    ref.child(key!!).child("points").setValue(coinsuser)
                                                    ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)
                                                    val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                    intent.putExtra("correctans", no_correctans.toString())
                                                    intent.putExtra("wrongans", no_wrongans.toString())
                                                    intent.putExtra("username1", studentname)
                                                    intent.putExtra("username2", tv_secondname.text.toString())
                                                    intent.putExtra("coinsuser", coinsuser.toString())
                                                    intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                    intent.putExtra("coinscomputer", coins2.toString())
                                                    intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                    startActivity(intent)
                                                    finish()
                                                } else {
                                                    val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                                    val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                                    missionsReference.setValue(post)
                                                    val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                                    intent.putExtra("correctans", no_correctans.toString())
                                                    intent.putExtra("wrongans", no_wrongans.toString())
                                                    intent.putExtra("username1", studentname)
                                                    intent.putExtra("username2", tv_secondname.text.toString())
                                                    intent.putExtra("coinsuser", coinsuser.toString())
                                                    intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                                    intent.putExtra("coinscomputer", coins2.toString())
                                                    intent.putExtra("usertotalpts", usertotalpoints.toString())
                                                    startActivity(intent)
                                                    finish()
                                                }


                                            }

                                        } else {
                                            val post = PointsModel(studentname, coinsuser, usertotalpoints)
                                            val missionsReference = FirebaseDatabase.getInstance().reference.child("coins").child(studentname)
                                            missionsReference.setValue(post)
                                            val intent = Intent(applicationContext, ScorecardActivity::class.java)
                                            intent.putExtra("correctans", no_correctans.toString())
                                            intent.putExtra("wrongans", no_wrongans.toString())
                                            intent.putExtra("username1", studentname)
                                            intent.putExtra("username2", tv_secondname.text.toString())
                                            intent.putExtra("coinsuser", coinsuser.toString())
                                            intent.putExtra("coinsseconduser", coinsseconduser.toString())
                                            intent.putExtra("coinscomputer", coins2.toString())
                                            intent.putExtra("usertotalpts", usertotalpoints.toString())
                                            startActivity(intent)
                                            finish()
                                        }

                                    }

                                    override fun onCancelled(p0: DatabaseError) {
                                        //    Toast.makeText(applicationContext, "SUBMIT TEST8:"+p0.toString(), Toast.LENGTH_SHORT).show()

                                    }
                                })


                            }
                        }


                    } catch (e: WindowManager.BadTokenException) {
                        //use a log message
                    }
                }


            }
    }

    private fun showadTimer() {

      countDownAdTimer = object: CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                textView_adtimer.text = (millisUntilFinished/1000).toLong().toString()
                if(textView_adtimer.text.equals("1")){
                    textView_adtimer.visibility = View.GONE
                    progressbar_adtimer.visibility = View.GONE
                    progressbar_adtimer1.visibility = View.GONE


                    iv_continue.visibility=View.VISIBLE
                    iv_cross.visibility=View.VISIBLE
                }
                if(textView_adtimer.text.equals("2")){
                    textView_adtimer.visibility = View.GONE
                    progressbar_adtimer.visibility = View.GONE
                    progressbar_adtimer1.visibility = View.GONE


                    iv_continue.visibility=View.VISIBLE
                    iv_cross.visibility=View.VISIBLE
                }
               /* if(textView_adtimer.text.equals("3")){
                    countDownAdTimer.cancel()
                    textView_adtimer.visibility = View.GONE
                    progressbar_adtimer.visibility = View.GONE
                    progressbar_adtimer1.visibility = View.GONE


                    iv_continue.visibility=View.VISIBLE
                    iv_cross.visibility=View.VISIBLE
                }*/

            }

            override fun onFinish() {
                textView_adtimer.setText("0")
                if(textView_adtimer.text.equals("0")){
                    textView_adtimer.visibility = View.GONE
                    progressbar_adtimer.visibility = View.GONE
                    progressbar_adtimer1.visibility = View.GONE


                    iv_continue.visibility=View.VISIBLE
                    iv_cross.visibility=View.VISIBLE
                }
            }
        }
        countDownAdTimer.start()
    }

    private fun getadsviews(urlpath: String, filename: String) {
       val referencequizads: DatabaseReference  = FirebaseDatabase.getInstance().reference.
               child("admin").child("quizads")

        adsviewslist.clear()
        adsviewslist2.clear()
        val refflikeposts = FirebaseDatabase.getInstance().reference.child("ViewAdsCount")

        refflikeposts.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshotliked: DataSnapshot) {
                if (dataSnapshotliked.exists()) {
                    for (datasliked in dataSnapshotliked.children) {

                        adsviewslist.add(LikePostsmodel(datasliked.child("username").value.toString(),
                                datasliked.child("postname").value.toString(),
                                datasliked.child("filename").value.toString()))

                    }

                    val filter: HashSet<LikePostsmodel> =  HashSet(adsviewslist)
                    adsviewslist2 =  ArrayList<LikePostsmodel>(filter)
                    Log.e( ":msg", "DATABASEKEYidddd8899991:" + adsviewslist2.size)

                    var countposts=0
                    for(adsviews in adsviewslist2){
                        try{
                            Log.e( ":msg", "DATABASEKEYidddd1:" + adsviews.username+",   "+studentname )
                            //Log.e( ":msg", "DATABASEKEYidddd2:" + adsviews.postname+",   "+urlpath )
                            Log.e( ":msg", "DATABASEKEYidddd3:" + adsviews.filename+",   "+filename )
                            if(adsviews.username.equals(studentname) && adsviews.postname.equals(urlpath) &&
                                    adsviews.filename.equals(filename)){
                                countposts++
                                referencequizads.orderByChild("urlpath").equalTo(urlpath).
                                        addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot1: DataSnapshot) {
                                                for (snapshot1 in dataSnapshot1.children) {
                                                    val idKey = snapshot1.key
                                                    val viewss = snapshot1.child("views").value.toString()
                                                    val v = viewss.toInt() + 1
                                                    referencequizads.child(idKey!!).child("views").setValue(v.toString())

                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Toast.makeText(this@QuizActivity, "error$error", Toast.LENGTH_LONG).show()
                                            }
                                        })

                            }
                        }catch (e:Exception){
                            Log.e("msg","FeedError:"+e.toString())

                        }
                    }
                }
                else {
                    Log.e("msg","FeedError:")

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("msg","FeedError:"+p0.toString())

            }
        })
    }


    fun getStudentsInClass(schoolId: String, classId: String,
                           listener: OnSuccessListener<List<Student>>) {
        CygnusApp.refToUsers(schoolId)
                .orderByChild("classId")
                .equalTo(classId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val students = ArrayList<Student>()
                        snapshot.children.forEach {
                            val user = it.toUser()
                            if (user is Student && user.classId == classId) {

                                if (user.name.equals(studentname, ignoreCase = true)) {
                                } else {
                                    students.add(user)
                                    //  Log.e("msg", "NAMESSSSSSS" + user.name)
                                }

                            }
                        }
                        //Toast.makeText(applicationContext, "Random: " + students.size, Toast.LENGTH_SHORT).show()
                        //  Log.e("msg", "NAMESSSSSSSLIST" + students.size)


                        listener.onSuccess(students)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(ArrayList())
                        Log.e("msg", "NAMESSSSSSSLIST1" + error.toString())

                    }
                })
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

    private fun startTimer() {
        time = 30
        totalTimeCountInMilliseconds = time * 1000.toLong()
        mProgressBar1.max = time * 1000
        countDownTimer = object : CountDownTimer(totalTimeCountInMilliseconds, 1) {
            override fun onTick(leftTimeInMilliseconds: Long) {
                val seconds = leftTimeInMilliseconds / 1000
                mProgressBar1.progress = leftTimeInMilliseconds.toInt()
                textViewShowTime.text = String.format("%02d", seconds % 60)

                // if not coputer then orderby second user , get ans status and time status--- if time and ans status true then show nextques--
                // if timestatus false or true and ans status false then show next ques




                if(!tv_secondname.text.toString().equals("Aditya")){
                        val reffquizans1 = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                        val queryans1: com.google.firebase.database.Query = reffquizans1.
                                orderByChild("username").equalTo(tv_secondname.text.toString())
                        queryans1.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshotquiz1: DataSnapshot) {
                                if (dataSnapshotquiz1.exists()) {
                                    loop@ for(dtqq1 in dataSnapshotquiz1.children){
                                        val quizanskey2 = dtqq1.key
                                        //  Handler().postDelayed()
                                      //  Log.e("msg", "QuizAnsSnehaaaaaaa0:"+dtqq1.child("timestatus").value.toString())
                                        //flagindexfour=dtqq1.child("flagstatus").value.toString()
                                        Log.e("msg", "QuizAnsSnehaaaaaaa0014:"+flagindexfour+" ,   "+dtqq1.child("timestatus").value.toString())

                                        if(dtqq1.child("timestatus").value.toString().equals("true") &&
                                                dtqq1.child("ansstatus").value.toString().equals("true") &&
                                                dtqq1.child("username").value.toString().equals(tv_secondname.text.toString())
                                                && flagindexfour.equals("false")){
                                            //selectedanswer=dtqq1.child("selectedans").value.toString()
                                            val qanskey11: String = dtqq1.key.toString()
                                            //Log.e("msg", "QuizAnsSnehaaaaaaa000000034671:"+flagindexfour)

                                            reffquizans1.child(qanskey11).child("timestatus").setValue("")
                                            reffquizans1.child(qanskey11).child("ansstatus").setValue("")
                                            reffquizans1.child(qanskey11).child("flagstatus").setValue("")
                                            flagindexfour="true"
                                            break@loop
                                        }
                                        if(dtqq1.child("timestatus").value.toString().equals("false") &&
                                                dtqq1.child("ansstatus").value.toString().equals("false")
                                                && dtqq1.child("username").value.toString().equals(tv_secondname.text.toString())
                                                && flagindexfour.equals("false")){
                                            val qanskey11: String = dtqq1.key.toString()
                                            Log.e("msg", "QuizAnsSnehaaaaaaa1:"+dtqq1.child("ansstatus").value.toString())

                                            reffquizans1.child(qanskey11).child("timestatus").setValue("")
                                            reffquizans1.child(qanskey11).child("ansstatus").setValue("")
                                            reffquizans1.child(qanskey11).child("flagstatus").setValue("")
                                            flagindexfour="true"
                                            break@loop

                                        }

                                        break@loop



                                    }
                                    if(flagindexfour.equals("true")){
                                        //red colour
                                        if(selectedanswer.contains("A.  ",ignoreCase = true)){
                                            rb_optiona.isChecked = true
                                            rb_optiona.setBackgroundColor(Color.parseColor("#F89A9D"))
                                            //   rb_optiona.setCircleColor(Color.parseColor("#F89A9D"))
                                        }
                                        else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                                            rb_optionb.isChecked = true
                                            rb_optionb.setBackgroundColor(Color.parseColor("#F89A9D"))
                                            // rb_optionb.setCircleColor(Color.parseColor("#F89A9D"));

                                        }
                                        else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                                            rb_optionc.isChecked = true
                                            rb_optionc.setBackgroundColor(Color.parseColor("#F89A9D"))
                                            //    rb_optionc.setCircleColor(Color.parseColor("#F89A9D"));

                                        }
                                        else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                                            rb_optiond.isChecked = true
                                            rb_optiond.setBackgroundColor(Color.parseColor("#F89A9D"))
                                            // rb_optiond.setCircleColor(Color.parseColor("#F89A9D"));

                                        }

                                        if(quizwrongans.equals("wrongans") && !singlewrongmarked.equals("wrongfirst")){
                                            quizwrongans=""
                                            singlewrongmarked=""
                                            Snackbar.make(btn_nextquiz,"Your answer is wrong\n"+tv_secondname.text.toString()+" has marked correct answer",Snackbar.LENGTH_LONG).show()

                                        }
                                        else if(quizwrongans.equals("wrongans") && singlewrongmarked.equals("wrongfirst")){

                                        }
                                        else
                                        {
                                            Snackbar.make(btn_nextquiz, "" + tv_secondname.text.toString() + " has marked answer first\nPoints has been awarded to " + tv_secondname.text.toString(), Snackbar.LENGTH_LONG).show()
                                        }
                                        //set timestamp if not answered yet and answered by another user fast
                                                    val reffquizans = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                                                    val queryans: com.google.firebase.database.Query = reffquizans.
                                                            orderByChild("username").equalTo(studentname)
                                                    queryans.addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onDataChange(dataSnapshotquizans: DataSnapshot) {
                                                            if (dataSnapshotquizans.exists()) {
                                                                for(dtq1 in dataSnapshotquizans.children){
                                                                        val qanskey: String = dtq1.key.toString()
                                                                        val tsLong = System.currentTimeMillis() / 1000
                                                                        val ts = tsLong.toString()
                                                                        reffquizans.child(qanskey).child("timestamp").setValue(ts)

                                                                }

                                                            }
                                                        }
                                                        override fun onCancelled(p0: DatabaseError) {
                                                        }
                                                    })


                                        //Log.e("msg", "QuizAnsSnehaaaaaaa00000003467:"+flagindexfour)
                                        flagindexfour="cancelled"
                                      //  Handler().postDelayed({
                                            if(flagindexfour.equals("cancelled")){
                                                if (j < (quizlist.size - 1)) {
                                                    nextQues()
                                                  //  btn_nextquiz.isEnabled=false

                                                }
                                                else{
                                                    nextQues()
                                                    startTimer()
                                                }


                                            }
                                      //  },3000)

                                    }

                                    // Log.e("msg", "VALUESQUIZdfdgdfg113336668889987666876988887661:"+dataSnapshotquiz1.child("ansstatus").value.toString())
                                    /* if(selectedanswer.equals(correctanswer)){

                                         reffquizans1.child(quizanskey2!!).child("timestatus").setValue(ts)
                                         reffquizans1.child(quizanskey2!!).child("ansstatus").setValue(selectedanswer)
                                     }
                                     if(!selectedanswer.equals(correctanswer)){

                                     }*/




                                }

                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })







                }


                if (selectedanswer.equals("") && textViewShowTime.text.equals("14") && tv_secondname.text.toString().equals("Aditya")) {
                    computeranswerstatus = true
                    //disable all radiobuttons
                    rb_optiona.isEnabled = false
                    rb_optionb.isEnabled = false
                    rb_optionc.isEnabled = false
                    rb_optiond.isEnabled = false
                    autosubmitques = true

                }
                //highlight correct ans by computer at 14 seconds
                if (textViewShowTime.text.equals("14") && tv_secondname.text.toString().equals("Aditya")) {
                    if (!selectedanswer.equals(correctanswer)) {
                        //computeranswerstatus = true
                        //disable all radiobuttons
                        rb_optiona.isEnabled = false
                        rb_optionb.isEnabled = false
                        rb_optionc.isEnabled = false
                        rb_optiond.isEnabled = false

                        autosubmitques = true

                        if(selectedanswer.contains("A.  ",ignoreCase = true)){
                            rb_optiona.isChecked = true
                        rb_optiona.setBackgroundColor(Color.parseColor("#F89A9D"))
                        }
                        else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                            rb_optionb.isChecked = true
                            rb_optionb.setBackgroundColor(Color.parseColor("#F89A9D"))

                        }
                       else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                            rb_optionc.isChecked = true
                            rb_optionc.setBackgroundColor(Color.parseColor("#F89A9D"))
                        }
                        else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                            rb_optiond.isChecked = true
                            rb_optiond.setBackgroundColor(Color.parseColor("#F89A9D"))
                        }

                    }

                    /*else if(!tv_secondname.text.toString().equals("Computer")){
                       autosubmitques = false

                   }
                   else if(selectedanswer.equals(correctanswer)){
                       autosubmitques = false

                   }*/
                    if (rb_optiona.text.toString().contains(correctanswer)) {
                        rb_optiona.isChecked = true
                        rb_optiona.setBackgroundColor(Color.parseColor("#8EC3A3"))

                    } else if (rb_optionb.text.toString().contains(correctanswer)) {

                        rb_optionb.isChecked = true
                        rb_optionb.setBackgroundColor(Color.parseColor("#8EC3A3"))
                    } else if (rb_optionc.text.toString().contains(correctanswer)) {

                        rb_optionc.isChecked = true
                        rb_optionc.setBackgroundColor(Color.parseColor("#8EC3A3"))
                    } else if (rb_optiond.text.toString().contains(correctanswer)) {

                        rb_optiond.isChecked = true
                        rb_optiond.setBackgroundColor(Color.parseColor("#8EC3A3"))
                    }


                    Handler().postDelayed({
                        if (autosubmitques == true) {
                            nextQues()
                        }

                    }, 3000)
                    /* if (textViewShowTime.text.equals("12")) {
                         if (!selectedanswer.equals(correctanswer)) {
                             autosubmitques = true
                             nextQues()
                        }
                     }*/

                }

                //highlight correct ans by user at 5 seconds

                if (textViewShowTime.text.equals("04") && !tv_secondname.text.toString().equals("Aditya")) {

                    //get timstamp of user
                    val refftimestampuser = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                    val queryuser: com.google.firebase.database.Query = refftimestampuser.
                            orderByChild("username").equalTo(studentname)
                    queryuser.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshotuser: DataSnapshot) {
                            if (dataSnapshotuser.exists()) {
                                for(dtq1 in dataSnapshotuser.children){
                                    timestampuser=dtq1.child("timestamp").value.toString()
                                    //  Log.e("msg", "VALUESQUIZTimestamp1:$timestampuser")
                                }
//get timstamp of second user

                                val queryseconduser: com.google.firebase.database.Query = refftimestampuser.
                                        orderByChild("username").equalTo(tv_secondname.text.toString())
                                queryseconduser.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshotsecond: DataSnapshot) {
                                        if (dataSnapshotsecond.exists()) {
                                            for(dtq2 in dataSnapshotsecond.children){
                                                timestampseconduser=dtq2.child("timestamp").value.toString()
                                                // Log.e("msg", "VALUESQUIZTimestamp2:$timestampseconduser")


                                            }
                                            Log.e("msg", "VALUESQUIZTANSWERRRRRRR12:$selectedanswer")
                                            Log.e("msg", "VALUESQUIZTANSWERRRRRRR13:$correctanswer")
                                            if ( Math.abs(timestampseconduser.toLong()) > Math.abs(timestampuser.toLong()) ) {
                                                if (!selectedanswer.contains(correctanswer)) {
                                                    //computeranswerstatus = true
                                                    //disable all radiobuttons
                                                    rb_optiona.isEnabled = false
                                                    rb_optionb.isEnabled = false
                                                    rb_optionc.isEnabled = false
                                                    rb_optiond.isEnabled = false


                                                    //red colour
                                                    if(selectedanswer.contains("A.  ",ignoreCase = true)){
                                                        rb_optiona.isChecked = true
                                                        rb_optiona.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        //   rb_optiona.setCircleColor(Color.parseColor("#F89A9D"))
                                                    }
                                                    else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                                                        rb_optionb.isChecked = true
                                                        rb_optionb.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        // rb_optionb.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }
                                                    else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                                                        rb_optionc.isChecked = true
                                                        rb_optionc.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        //    rb_optionc.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }
                                                    else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                                                        rb_optiond.isChecked = true
                                                        rb_optiond.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        // rb_optiond.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }

                                                }
                                                //green colour
                                              else  if (selectedanswer.contains(correctanswer)) {
                                                    if(selectedanswer.contains("A.  ",ignoreCase = true)){
                                                        rb_optiona.isChecked = true
                                                        rb_optiona.setBackgroundColor(Color.parseColor("#8EC3A3"))
                                                        //   rb_optiona.setCircleColor(Color.parseColor("#F89A9D"))
                                                    }
                                                    else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                                                        rb_optionb.isChecked = true
                                                        rb_optionb.setBackgroundColor(Color.parseColor("#8EC3A3"))
                                                        // rb_optionb.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }
                                                    else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                                                        rb_optionc.isChecked = true
                                                        rb_optionc.setBackgroundColor(Color.parseColor("#8EC3A3"))
                                                        //    rb_optionc.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }
                                                    else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                                                        rb_optiond.isChecked = true
                                                        rb_optiond.setBackgroundColor(Color.parseColor("#8EC3A3"))
                                                        // rb_optiond.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }

                                                }
                                            }
                                            else{
                                                if (!selectedanswer.contains(correctanswer)) {
                                                    //computeranswerstatus = true
                                                    //disable all radiobuttons
                                                    rb_optiona.isEnabled = false
                                                    rb_optionb.isEnabled = false
                                                    rb_optionc.isEnabled = false
                                                    rb_optiond.isEnabled = false


                                                    //red colour
                                                    if(selectedanswer.contains("A.  ",ignoreCase = true)){
                                                        rb_optiona.isChecked = true
                                                        rb_optiona.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        //   rb_optiona.setCircleColor(Color.parseColor("#F89A9D"))
                                                    }
                                                    else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                                                        rb_optionb.isChecked = true
                                                        rb_optionb.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        // rb_optionb.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }
                                                    else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                                                        rb_optionc.isChecked = true
                                                        rb_optionc.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        //    rb_optionc.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }
                                                    else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                                                        rb_optiond.isChecked = true
                                                        rb_optiond.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        // rb_optiond.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }

                                                }
                                                else if(selectedanswer.contains(correctanswer)){
                                                    //red colour
                                                    if(selectedanswer.contains("A.  ",ignoreCase = true)){
                                                        rb_optiona.isChecked = true
                                                        rb_optiona.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        //   rb_optiona.setCircleColor(Color.parseColor("#F89A9D"))
                                                    }
                                                    else if(selectedanswer.contains("B.  ",ignoreCase = true)){
                                                        rb_optionb.isChecked = true
                                                        rb_optionb.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        // rb_optionb.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }
                                                    else if(selectedanswer.contains("C.  ",ignoreCase = true)){
                                                        rb_optionc.isChecked = true
                                                        rb_optionc.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        //    rb_optionc.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }
                                                    else if(selectedanswer.contains("D.  ",ignoreCase = true)){
                                                        rb_optiond.isChecked = true
                                                        rb_optiond.setBackgroundColor(Color.parseColor("#F89A9D"))
                                                        // rb_optiond.setCircleColor(Color.parseColor("#F89A9D"));

                                                    }
                                                }
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


            }

            override fun onFinish() {

                textViewShowTime.text = "00"
                textViewShowTime.visibility = View.VISIBLE
                mProgressBar.visibility = View.VISIBLE
                mProgressBar1.visibility = View.GONE

                secondname = tv_secondname.text.toString()

                if (textViewShowTime.text.equals("00")) {
                    if (nextbtnclick_lastques == true) {
                        nextbtnclick_lastques = false
                    } else {
                        if(tv_secondname.text.toString().equals("Aditya")){
                            nextQues()
                        }
                       else if(!tv_secondname.text.toString().equals("Aditya")){
                            testflag=""
                            nextQues()
                            countDownTimer.cancel()
                            var time = 30

                            var totalTimeCountInMilliseconds = time * 1000.toLong()
                            mProgressBar1.max = time * 1000
                             timer1 = object: CountDownTimer(totalTimeCountInMilliseconds, 1) {
                                override fun onTick(millisUntilFinished: Long) {
                                    val seconds = millisUntilFinished / 1000
                                    mProgressBar1.progress = millisUntilFinished.toInt()
                                    textViewShowTime.text = String.format("%02d", seconds % 60)
                                }

                                override fun onFinish() {
                                    nextQues()
                                    timer1.start()
                                    textViewShowTime.text = "00"
                                    textViewShowTime.visibility = View.VISIBLE
                                    //mProgressBar.visibility = View.VISIBLE
                                   // mProgressBar1.visibility = View.GONE
                                }
                            }
                            timer1.start()
                           // flagindexfour="false"
                           // if(!flagindexfour.equals("false")){
                                //startTimer()



                        }


                    }


                }

            }
        }.start()
    }
    fun RadioButton.setCircleColor(color: Int){
        val colorStateList = ColorStateList(
                arrayOf(
                        intArrayOf(-android.R.attr.state_checked), // unchecked
                        intArrayOf(android.R.attr.state_checked) // checked
                ), intArrayOf(
                R.color.colorPrimary, // unchecked color
                color // checked color
        )
        )

        // finally, set the radio button's button tint list
        buttonTintList = colorStateList

        // optionally set the button tint mode or tint blend mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            buttonTintBlendMode = BlendMode.SRC_IN
        }else{
            buttonTintMode = PorterDuff.Mode.SRC_IN
        }

        invalidate() //could not be necessary
    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            countDownTimer.cancel()
            countDownFlagTimer.cancel()
            timer1.cancel()


        } catch (e: Exception) {
        }

        val reffquizanss = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
        reffquizanss.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshottimee: DataSnapshot) {
                if (dataSnapshottimee.exists()) {

                    val reffquizans = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                    val queryans: com.google.firebase.database.Query = reffquizans.
                            orderByChild("username").equalTo(studentname)
                    queryans.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshotquizans: DataSnapshot) {
                            if (dataSnapshotquizans.exists()) {
                                for(dtq1 in dataSnapshotquizans.children){
                                    val qanskey: String = dtq1.key.toString()
                                    reffquizans.child(qanskey).child("timestatus").setValue("")
                                    reffquizans.child(qanskey).child("ansstatus").setValue("")
                                    reffquizans.child(qanskey).child("flagstatus").setValue("")

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

        val ref = FirebaseDatabase.getInstance().reference.child("quizscreen")
        ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (datas in dataSnapshot.children) {
                        val key = datas.key
                      //  quizidnext=quizidnext-1
                        val status = datas.child("status").value.toString()
                        ref.child(key!!).child("status").setValue("false")
                        ref.child(key!!).child("connect").setValue("notconnected")
                       // ref.child(key!!).child("quizindex").setValue(quizidnext-1)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        val uidRef1 = FirebaseDatabase.getInstance().reference.child("coins")
        val valueEventListener1 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {

                    if (tv_secondname.text.toString().equals(ds.key, ignoreCase = true)) {
                        // Toast.makeText(applicationContext,"CoinsSeconduser:"+coinsseconduser,Toast.LENGTH_LONG).show()

                        uidRef1.child(ds.key!!).child("points").setValue(0)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("msg", databaseError.message) //Don't ignore errors!
            }
        }
        uidRef1.addListenerForSingleValueEvent(valueEventListener1)

        val uidRef2 = FirebaseDatabase.getInstance().reference.child("coins")
        val valueEventListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {

                    if (studentname.equals(ds.key, ignoreCase = true)) {
                        // Toast.makeText(applicationContext,"CoinsSeconduser:"+coinsseconduser,Toast.LENGTH_LONG).show()

                        uidRef1.child(ds.key!!).child("points").setValue(0)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("msg", databaseError.message) //Don't ignore errors!
            }
        }
        uidRef2.addListenerForSingleValueEvent(valueEventListener1)

    }

    override fun onPause() {
        try {
            countDownTimer.cancel()
            timer1.cancel()

            countDownFlagTimer.cancel()

        } catch (e: Exception) {
        }


        val reffquizanss = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
        reffquizanss.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshottimee: DataSnapshot) {
                if (dataSnapshottimee.exists()) {

                    val reffquizans = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                    val queryans: com.google.firebase.database.Query = reffquizans.
                            orderByChild("username").equalTo(studentname)
                    queryans.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshotquizans: DataSnapshot) {
                            if (dataSnapshotquizans.exists()) {
                                for(dtq1 in dataSnapshotquizans.children){
                                    val qanskey: String = dtq1.key.toString()
                                    reffquizans.child(qanskey).child("timestatus").setValue("")
                                    reffquizans.child(qanskey).child("ansstatus").setValue("")
                                    reffquizans.child(qanskey).child("flagstatus").setValue("")

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

        val ref = FirebaseDatabase.getInstance().reference.child("quizscreen")
        ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (datas in dataSnapshot.children) {
                        val key = datas.key
                       // quizidnext=quizidnext-1
                        val status = datas.child("status").value.toString()
                        ref.child(key!!).child("status").setValue("false")
                        ref.child(key!!).child("connect").setValue("notconnected")
                       // ref.child(key!!).child("quizindex").setValue(quizidnext-1)

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // Toast.makeText(applicationContext, "" + p0.toException(), Toast.LENGTH_SHORT).show()
            }
        })

        val uidRef1 = FirebaseDatabase.getInstance().reference.child("coins")
        val valueEventListener1 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {

                    if (tv_secondname.text.toString().equals(ds.key, ignoreCase = true)) {
                        // Toast.makeText(applicationContext,"CoinsSeconduser:"+coinsseconduser,Toast.LENGTH_LONG).show()

                        uidRef1.child(ds.key!!).child("points").setValue(0)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("msg", databaseError.message) //Don't ignore errors!
            }
        }
        uidRef1.addListenerForSingleValueEvent(valueEventListener1)

        val uidRef2 = FirebaseDatabase.getInstance().reference.child("coins")
        val valueEventListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {

                    if (studentname.equals(ds.key, ignoreCase = true)) {
                        // Toast.makeText(applicationContext,"CoinsSeconduser:"+coinsseconduser,Toast.LENGTH_LONG).show()

                        uidRef1.child(ds.key!!).child("points").setValue(0)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("msg", databaseError.message) //Don't ignore errors!
            }
        }
        uidRef2.addListenerForSingleValueEvent(valueEventListener1)
        super.onPause()

    }

    override fun onStop() {
        super.onStop()
        try {
            countDownTimer.cancel()
            timer1.cancel()

            countDownFlagTimer.cancel()

        } catch (e: Exception) {
        }

        val reffquizanss = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
        reffquizanss.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshottimee: DataSnapshot) {
                if (dataSnapshottimee.exists()) {

                    val reffquizans = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                    val queryans: com.google.firebase.database.Query = reffquizans.
                            orderByChild("username").equalTo(studentname)
                    queryans.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshotquizans: DataSnapshot) {
                            if (dataSnapshotquizans.exists()) {
                                for(dtq1 in dataSnapshotquizans.children){
                                    val qanskey: String = dtq1.key.toString()
                                    reffquizans.child(qanskey).child("timestatus").setValue("")
                                    reffquizans.child(qanskey).child("ansstatus").setValue("")
                                    reffquizans.child(qanskey).child("flagstatus").setValue("")

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

        val ref = FirebaseDatabase.getInstance().reference.child("quizscreen")
        ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (datas in dataSnapshot.children) {
                        val key = datas.key
                        //quizidnext=quizidnext-1
                        val status = datas.child("status").value.toString()
                        ref.child(key!!).child("status").setValue("false")
                        ref.child(key!!).child("connect").setValue("notconnected")
                     //   ref.child(key!!).child("quizindex").setValue(quizidnext-1)

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        val uidRef1 = FirebaseDatabase.getInstance().reference.child("coins")
        val valueEventListener1 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {

                    if (tv_secondname.text.toString().equals(ds.key, ignoreCase = true)) {
                        // Toast.makeText(applicationContext,"CoinsSeconduser:"+coinsseconduser,Toast.LENGTH_LONG).show()

                        uidRef1.child(ds.key!!).child("points").setValue(0)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("msg", databaseError.message) //Don't ignore errors!
            }
        }
        uidRef1.addListenerForSingleValueEvent(valueEventListener1)

        val uidRef2 = FirebaseDatabase.getInstance().reference.child("coins")
        val valueEventListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {

                    if (studentname.equals(ds.key, ignoreCase = true)) {
                        // Toast.makeText(applicationContext,"CoinsSeconduser:"+coinsseconduser,Toast.LENGTH_LONG).show()

                        uidRef1.child(ds.key!!).child("points").setValue(0)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("msg", databaseError.message) //Don't ignore errors!
            }
        }
        uidRef2.addListenerForSingleValueEvent(valueEventListener1)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            countDownTimer.cancel()
            timer1.cancel()

            countDownFlagTimer.cancel()
        } catch (e: Exception) {
        }
        val reffquizanss = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
        reffquizanss.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshottimee: DataSnapshot) {
                if (dataSnapshottimee.exists()) {

                    val reffquizans = FirebaseDatabase.getInstance().reference.child("quiz-ansusers")
                    val queryans: com.google.firebase.database.Query = reffquizans.
                            orderByChild("username").equalTo(studentname)
                    queryans.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshotquizans: DataSnapshot) {
                            if (dataSnapshotquizans.exists()) {
                                for(dtq1 in dataSnapshotquizans.children){
                                    val qanskey: String = dtq1.key.toString()
                                    reffquizans.child(qanskey).child("timestatus").setValue("")
                                    reffquizans.child(qanskey).child("ansstatus").setValue("")
                                    reffquizans.child(qanskey).child("flagstatus").setValue("")
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


        val ref = FirebaseDatabase.getInstance().reference.child("quizscreen")
        ref.orderByChild("name").equalTo(studentname).addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (datas in dataSnapshot.children) {
                        val key = datas.key
                       // quizidnext=quizidnext-1
                        val status = datas.child("status").value.toString()
                        ref.child(key!!).child("status").setValue("false")
                        ref.child(key!!).child("connect").setValue("notconnected")
                      //  ref.child(key!!).child("quizindex").setValue(quizidnext-1)

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        val uidRef1 = FirebaseDatabase.getInstance().reference.child("coins")
        val valueEventListener1 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {

                    if (tv_secondname.text.toString().equals(ds.key, ignoreCase = true)) {
                        // Toast.makeText(applicationContext,"CoinsSeconduser:"+coinsseconduser,Toast.LENGTH_LONG).show()

                        uidRef1.child(ds.key!!).child("points").setValue(0)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("msg", databaseError.message) //Don't ignore errors!
            }
        }
        uidRef1.addListenerForSingleValueEvent(valueEventListener1)

        val uidRef2 = FirebaseDatabase.getInstance().reference.child("coins")
        val valueEventListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {

                    if (studentname.equals(ds.key, ignoreCase = true)) {
                        // Toast.makeText(applicationContext,"CoinsSeconduser:"+coinsseconduser,Toast.LENGTH_LONG).show()

                        uidRef1.child(ds.key!!).child("points").setValue(0)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("msg", databaseError.message) //Don't ignore errors!
            }
        }
        uidRef2.addListenerForSingleValueEvent(valueEventListener1)
    }
}
