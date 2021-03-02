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
import kotlinx.android.synthetic.main.activity_fileupload.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.HashMap

class StudentFileUpload  : DashboardChildActivity() {
    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
    var subjectteachername:String?=null
    var teacherClassId:String?=null
    var tokenlist: ArrayList<String> = ArrayList()

    private lateinit var filesManager: FileManager
    private var filesAdapter: MaterialAdapter? = null
    private val files = ArrayList<CourseFile>()
    var student_namee:String?=null
    private var pickRequestCode = RESULT_ACTION_PICK_MATERIAL
    private lateinit var subject: Subject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fileupload)

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        subjectteachername= sp_loginsave.getString("SubjectTeacherName","")
        teacherClassId= sp_loginsave.getString("SubjectTeacherClassId","")

        student_namee=intent.getStringExtra("student_namee");
        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject? ?: return finish()

        filesManager = FileManager.newInstance(this, "$schoolId/courses/${subject.classId}/subjects/${subject.name}$student_namee/files/")


        addButtonfile.setOnClickListener {
            if (PermissionUtils.requestPermissionIfNeeded(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            getString(R.string.permission_storage),
                            RC_WRITE_PERMISSION
                    )) {
                pickFile(RESULT_ACTION_PICK_FILES)
            }
        }

        if (!student_namee.equals(null)) {
            addButtonfile.visibility = View.VISIBLE

        }
    }
    override fun updateUI(currentUser: User) {
        supportActionBar?.title = subject.name
        showFiles()

    }
    private fun showFiles() {
        if (filesAdapter == null) {
            filesAdapter = MaterialAdapter(this, files, filesManager)
            fileListnew.adapter = filesAdapter
        }

        filesManager.listAll().addOnSuccessListener { result ->
            files.clear()
            result?.items?.forEach { reference ->
                reference.metadata.addOnSuccessListener { metadata ->

                    pb_studentfiles.visibility = View.VISIBLE
                    Handler().postDelayed({
                        files.add(CourseFile(reference.name, metadata))
                        filesAdapter?.notifyDataSetChanged()
                        pb_studentfiles.visibility = View.GONE
                    }, 1000)
                }
            }
            if(files.size==0){
                Handler().postDelayed({
                    pb_studentfiles.visibility=View.GONE

                    // Toast.makeText(applicationContext,"No file has uploaded..",Toast.LENGTH_SHORT).show()
                }, 1000)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                RESULT_ACTION_PICK_FILES -> {
                    data?.data?.getLastPathSegmentOnly(this)?.let { filename ->


                        val post = StudentUploadFiles(subject.classId, subject.teacherId,
                                subject.name, student_namee);

                        val missionsReference =
                                FirebaseDatabase.getInstance().reference.child(schoolId).
                                        child("StudentUploadFiles").child(student_namee.toString())
                        missionsReference.setValue(post)

                        val body = "Hey! Your Student has shared a file.\nClick to Check Now !"

                        uploadFile(filesManager, filename, data.data!!, filesAdapter,body)
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


    private fun pickFile(requestCode: Int) {
        this.pickRequestCode = requestCode
        val mimeTypes = arrayOf("image/*", "application/pdf")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*|application/pdf")
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
       // val i = Intent(Intent.ACTION_GET_CONTENT)
       //i.type = "application/*"
        //i.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, requestCode)
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
                        val status = Snackbar.make(fileListnew, getString(R.string.upload_started), Snackbar.LENGTH_INDEFINITE)
                        status.show()
                        fm.upload(filename+formatted, data)
                                .addOnSuccessListener {
                                    it.metadata?.let { metadata ->

                                        adapter?.add(CourseFile(filename, metadata))
                                        adapter?.notifyDataSetChanged()
                                        showFiles()
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
                child("TeacherTokens")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    Log.e("msg", "TOKENCLASS" + datas.child("teacherId").value.toString())
                    //Log.e("msg", "TOKENCLASS1" + datas.child("token").value.toString())
                    if (subject.teacherId.equals(datas.child("teacherId").
                                    value.toString(),ignoreCase = true)) {
                        try {
                            val s = datas.key
                            val studenttoken = datas.child("token").value.toString()
                            tokenlist.add(studenttoken)

                        } catch (e: Exception) {
                           // Toast.makeText(applicationContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
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
                   // Toast.makeText(applicationContext, "SENDPUSH::" + response.toString(), Toast.LENGTH_SHORT).show()
                   /* val post = StoreNotifications(subjectteachername, body);

                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child(schoolId).
                                    child("Notifications").push()

                    missionsReference.setValue(post)*/

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

    companion object {
        private const val RESULT_ACTION_PICK_MATERIAL = 100
        private const val RESULT_ACTION_PICK_HOMEWORK = 150
        private const val RESULT_ACTION_PICK_FILES = 250
        private const val RC_WRITE_PERMISSION = 200
    }

}