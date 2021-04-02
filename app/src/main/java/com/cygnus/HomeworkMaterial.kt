package com.cygnus

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import co.aspirasoft.util.PermissionUtils
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cygnus.core.DashboardChildActivity
import com.cygnus.model.*
import com.cygnus.storage.FileManager
import com.cygnus.storage.FileUtils.getLastPathSegmentOnly
import com.cygnus.storage.MaterialAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_homeworkmaterial.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.HashMap

class HomeworkMaterial : DashboardChildActivity() {
    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
    var subjectteachername:String?=null
    var teacherClassId:String?=null
    var tokenlist: ArrayList<String> = ArrayList()
    private var editable: Boolean = false
    private lateinit var homeworkManager: FileManager
    private var homeworkAdapter: MaterialAdapter? = null
    private val homework = ArrayList<CourseFile>()
     var student_namee:String?=null
    private lateinit var subject: Subject

    private var pickRequestCode = RESULT_ACTION_PICK_MATERIAL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeworkmaterial)

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        subjectteachername= sp_loginsave.getString("SubjectTeacherName","")
        teacherClassId= sp_loginsave.getString("SubjectTeacherClassId","")

        try{
            student_namee=intent.getStringExtra("student_namee");
            Log.e("msg","NAMEEE::"+student_namee);

        }
        catch (e:Exception){
            Log.e("msg","NAMEEE::11"+e.toString());

        }



        //Toast.makeText(applicationContext,"name1: "+student_namee,Toast.LENGTH_SHORT).show()
        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject? ?: return finish()
        editable = intent.getBooleanExtra(CygnusApp.EXTRA_EDITABLE_MODE, editable)

        homeworkManager = FileManager.newInstance(this, "$schoolId/courses/${subject.classId}/subjects/${subject.name}/exercises/")


        addButtonhomework.setOnClickListener {
            if (PermissionUtils.requestPermissionIfNeeded(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            getString(R.string.permission_storage),
                            RC_WRITE_PERMISSION
                    )) {
                pickFile(RESULT_ACTION_PICK_HOMEWORK)
            }
        }

        if (student_namee.equals(null)) {
            addButtonhomework.visibility = View.VISIBLE

        }

    }

    private fun pickFile(requestCode: Int) {
        this.pickRequestCode = requestCode

        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "application/*"
        i.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(i, requestCode)
    }

    @SuppressLint("NewApi")
    private fun uploadFile(fm: FileManager, filename: String, data: Uri, adapter: MaterialAdapter?,
                           textbody: String) {
        try{



            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            val formatted = current.format(formatter)




            MaterialAlertDialogBuilder(this)
                    .setTitle(String.format(getString(R.string.upload_title), subject.name, subject.classId))
                    .setMessage(String.format(getString(R.string.upload_confirm), filename))
                    .setPositiveButton(android.R.string.yes) { dialog, _ ->
                        dialog.dismiss()
                        val status = Snackbar.make(homeworkListnew, getString(R.string.upload_started), Snackbar.LENGTH_INDEFINITE)
                        status.show()
                        fm.upload(filename+formatted, data)
                                .addOnSuccessListener {
                                    it.metadata?.let { metadata ->

                                        adapter?.add(CourseFile(filename, metadata))
                                        adapter?.notifyDataSetChanged()
                                        showHomeworkExercises()
                                        /* Thread(Runnable {

                                             // try to touch View of UI thread
                                             this.runOnUiThread(java.lang.Runnable {

                                             })
                                         }).start()*/
//                                    try{}
//                                    catch (e:Exception){}

                                        //adapter?.notifyDataSetChanged()
                                        //  listView.invalidateViews();
                                    }

                                    status.setText(getString(R.string.upload_success))
                                    fetchTokens(textbody)
                                    Handler().postDelayed({ status.dismiss() }, 2500L)
                                }
                                .addOnFailureListener {
                                    status.setText(it.message ?: getString(R.string.upload_failure))
                                    Handler().postDelayed({ status.dismiss() }, 2500L)
                                }
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
        }
        catch (e:Exception){
            Toast.makeText(applicationContext,""+e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun fetchTokens(textbody: String) {
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


                sendFCMPush(tokenlist,textbody);
            }

            override fun onCancelled(databaseError: DatabaseError) {
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
                    val post = StoreNotifications(subjectteachername,teacherClassId, body,"unread");

                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child(schoolId).
                                    child("Notifications").push()

                    missionsReference.setValue(post)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {


                RESULT_ACTION_PICK_HOMEWORK -> {
                    data?.data?.getLastPathSegmentOnly(this)?.let { filename ->
                        val homeworkclassbody="Hey! Your Subject Teacher "+subjectteachername+
                                " has assigned a new Homework.\nClick to Solve Now !"
                        uploadFile(homeworkManager, filename, data.data!!, homeworkAdapter,homeworkclassbody)
                    }
                }

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFile(pickRequestCode)
            }
        }
    }
    override fun updateUI(currentUser: User) {
        supportActionBar?.title = subject.name
        showHomeworkExercises()
    }
    private fun showHomeworkExercises() {

        if (homeworkAdapter == null) {

            homeworkAdapter = MaterialAdapter(this, homework, homeworkManager)
            homeworkListnew.adapter = homeworkAdapter
        }

        homeworkManager.listAll().addOnSuccessListener { result ->

            homework.clear()
            result?.items?.forEach { reference ->
                reference.metadata.addOnSuccessListener {
                    metadata ->

                    pb_homematerial.visibility=View.VISIBLE
                    Handler().postDelayed({

                    homework.add(CourseFile(reference.name, metadata))
                    homeworkAdapter?.notifyDataSetChanged()
                    pb_homematerial.visibility=View.GONE
                    }, 1000)
                }

            }
            if(homework.size==0){
                Handler().postDelayed({
                    pb_homematerial.visibility=View.GONE

                    // Toast.makeText(applicationContext,"No file has uploaded..",Toast.LENGTH_SHORT).show()
                }, 1000)
                //Toast.makeText(applicationContext,"No file has uploaded..",Toast.LENGTH_SHORT).show()


            }
        }



    }

    companion object {
        private const val RESULT_ACTION_PICK_MATERIAL = 100
        private const val RESULT_ACTION_PICK_HOMEWORK = 150
        private const val RESULT_ACTION_PICK_FILES = 250
        private const val RC_WRITE_PERMISSION = 200
    }
}