package com.cygnus

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import co.aspirasoft.adapter.ModelViewAdapter
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.NoticeBoardDao
import com.cygnus.model.NoticeBoardPost
import com.cygnus.model.Subject
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.view.MessageInputDialog
import com.cygnus.view.NoticeBoardPostView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class NoticeActivity : DashboardChildActivity() {
    var tokenlist: ArrayList<String> = ArrayList()
    private lateinit var posts: ArrayList<NoticeBoardPost>
    private lateinit var adapter: PostAdapter

    private var subject: Subject? = null
    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
     var subjectteachername:String?=null
     var teacherClassId:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)



        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        subjectteachername= sp_loginsave.getString("SubjectTeacherName","")
        teacherClassId= sp_loginsave.getString("SubjectTeacherClassId","")

        val subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject?
        val posts = intent.getParcelableArrayListExtra<NoticeBoardPost>(CygnusApp.EXTRA_NOTICE_POSTS)
        if (subject == null && posts == null) {
            finish()
            return
        }

        this.posts = posts ?: ArrayList()
        this.adapter = PostAdapter(this, this.posts)

        if (subject != null) {
            this.posts.clear()
            this.subject = subject
            NoticeBoardDao.getPostsBySubject(schoolId, subject, OnSuccessListener {
                this.posts.addAll(it)
                this.adapter.notifyDataSetChanged()
                pb_attendance.visibility=View.GONE

            })
        }
        else{
            this.adapter.notifyDataSetChanged()
        }

        addButton.visibility = when (currentUser) {
            is Teacher -> {
                addButton.setOnClickListener {
                    onAddNoticeClicked() }
                View.VISIBLE
            }
            else -> View.GONE
        }
    }

    override fun updateUI(currentUser: User) {
        contentList.adapter = this.adapter
    }

    private fun onAddNoticeClicked() {
        MessageInputDialog(this)
                .setOnMessageReceivedListener { message ->
                    try {
                        val status = Snackbar.make(contentList, "Sending...", Snackbar.LENGTH_INDEFINITE)
                        status.show()

                        val post = NoticeBoardPost(postContent = message, postAuthor = currentUser.name)
                        val onCompleteListener = OnCompleteListener<Void?> {
                            if (it.isSuccessful) {
                                status.setText("Message sent!")
                                posts.add(post)
                                adapter.notifyDataSetChanged()
                                pb_attendance.visibility=View.GONE

                                fetchTokens();

                            } else {
                                pb_attendance.visibility=View.GONE

                                status.setText(it.exception?.message ?: "Could not send the message at this time.")
                            }

                            Handler().postDelayed({
                                status.dismiss()
                            }, 1500L)
                        }

                        if (subject != null) {
                            NoticeBoardDao.add(schoolId, subject!!, post, onCompleteListener)
                        } else {
                            NoticeBoardDao.add(schoolId, (currentUser as Teacher).classId!!, post, onCompleteListener)
                        }
                    } catch (ex: Exception) {
                        pb_attendance.visibility=View.GONE

                        ex.printStackTrace()
                    }
                }
                .show()
    }

    private fun fetchTokens() {
        tokenlist.clear()
        val reference: DatabaseReference
        reference = FirebaseDatabase.getInstance().reference.child(schoolId).
                child("StudentTokens")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    Log.e("msg", "TOKENCLASS" + datas.child("classId").value.toString())
                    //Log.e("msg", "TOKENCLASS1" + datas.child("token").value.toString())
                    if (teacherClassId.equals(datas.child("classId").
                                    value.toString(),ignoreCase = true)) {
                        try {
                            val s = datas.key
                            val studenttoken = datas.child("token").value.toString()
                            tokenlist.add(studenttoken)




                        } catch (e: Exception) {
                            Toast.makeText(applicationContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Toast.makeText(applicationContext, "No scheduled class", Toast.LENGTH_SHORT).show();
                    }
                }

                val scheduledclassbody="Hey! Your Class Teacher has made an Important announcement.\nClick to Check Now !"
                sendFCMPush(tokenlist,scheduledclassbody);
            }

            override fun onCancelled(databaseError: DatabaseError) {
                pb_attendance.visibility=View.GONE
                //Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })
    }
    private fun sendFCMPush(tokenlist:  ArrayList<String>, body: String) {
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

            objData.put("body", body)
            objData.put("title", "Eduistein")
            objData.put("sound", "default")
            objData.put("icon", "icon_name") //   icon_name
            // objData.put("tag", token)
            objData.put("priority", "high")
            dataobjData = JSONObject()
            dataobjData.put("title", "Eduistein")
            dataobjData.put("text", body)

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
                    //Toast.makeText(applicationContext, "1:" + response.toString(), Toast.LENGTH_SHORT).show()


                },
                Response.ErrorListener { error ->
                    Log.e("msg", "onResponse1111112: $error") }) {
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


    private inner class PostAdapter(context: Context, val posts: ArrayList<NoticeBoardPost>)
        : ModelViewAdapter<NoticeBoardPost>(context, posts, NoticeBoardPostView::class) {

        override fun notifyDataSetChanged() {
            posts.sortByDescending { it.postDate }
            pb_attendance.visibility=View.GONE
            super.notifyDataSetChanged()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        ed_loginsave.putString("notice_back","true")
        ed_loginsave.commit()
    }
}