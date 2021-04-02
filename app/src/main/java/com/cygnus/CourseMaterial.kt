package com.cygnus

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
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
import com.cygnus.model.CourseFile
import com.cygnus.model.StoreNotifications
import com.cygnus.model.Subject
import com.cygnus.model.User
import com.cygnus.storage.FileManager
import com.cygnus.storage.FileUtils.getLastPathSegmentOnly
import com.cygnus.storage.MaterialAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_coursematerial.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.set

class CourseMaterial : DashboardChildActivity() {
    private lateinit var materialManager: FileManager
    private var materialAdapter: MaterialAdapter? = null
    private val material = ArrayList<CourseFile>()
    lateinit var sp_loginsave: SharedPreferences
    lateinit var ed_loginsave: SharedPreferences.Editor
    var subjectteachername: String? = null
    var teacherClassId: String? = null
    private var pickRequestCode = RESULT_ACTION_PICK_MATERIAL
    var tokenlist: ArrayList<String> = ArrayList()
    private lateinit var subject: Subject
    var student_namee: String? = null
    val DATABASE_NAME = "FILES"
    val TABLE_NAME = "course"
    lateinit var sq: SQLiteDatabase
    private val UID = "PRIMARY_ID"
    private val C_NAME = "COURSE_NAME"
    private val C_FILE = "COURSE_FILE"
    private val C_TIME = "COURSE_TIME"
   // var checklist: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coursematerial)
        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        subjectteachername = sp_loginsave.getString("SubjectTeacherName", "")
        teacherClassId = sp_loginsave.getString("SubjectTeacherClassId", "")

        student_namee = intent.getStringExtra("student_namee");


        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject?
                ?: return finish()


        materialManager = FileManager.newInstance(this,
                "$schoolId/courses/${subject.classId}/subjects/${subject.name}/lectures/")

        addButtoncourse.setOnClickListener {
            if (PermissionUtils.requestPermissionIfNeeded(
                            this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            getString(R.string.permission_storage),
                            RC_WRITE_PERMISSION)) {
                pickFile(RESULT_ACTION_PICK_MATERIAL)
            }
        }
        if (student_namee.equals(null)) {
            addButtoncourse.visibility = View.VISIBLE
        }

    }

    override fun updateUI(currentUser: User) {
        supportActionBar?.title = subject.name
        showClassMaterial()
    }

    private fun showClassMaterial() {

        /*if (sp_loginsave.getString("course_sqlite", "").equals("true")) {
            material.clear()
            sq = SQLiteDatabase.openOrCreateDatabase(applicationContext.filesDir.toString() + "/" + DATABASE_NAME, null)
            val c: Cursor = sq.rawQuery("SELECT DISTINCT COURSE_FILE FROM " + TABLE_NAME, null)
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        material.add(CourseFile(c.getInt(
                                c.getColumnIndex("COURSE_FILE")).toString(), null))
                        materialAdapter?.notifyDataSetChanged()
                        *//*result.add("" + p_id + ","+p_name +","+ p_quantity +","+ p_price);
                    Log.d("msg", "productsFromDatabase1: " +result);*//*
                    } while (c.moveToNext())
                }
                materialAdapter = MaterialAdapter(this, material, materialManager)
                courseListnew.adapter = materialAdapter
            }

        } else {*/
            if (materialAdapter == null) {
                materialAdapter = MaterialAdapter(this, material, materialManager)
                courseListnew.adapter = materialAdapter
            }

            materialManager.listAll().addOnSuccessListener { result ->
                material.clear()
                result?.items?.forEach { reference ->
                    reference.metadata.addOnSuccessListener { metadata ->
                        pb_coursematerial.visibility=View.VISIBLE

                        Handler().postDelayed({
                            material.add(CourseFile(reference.name, metadata))
                            materialAdapter?.notifyDataSetChanged()

                            sq = SQLiteDatabase.openOrCreateDatabase(applicationContext.filesDir.toString() + "/" + DATABASE_NAME, null)

                            for (co in material) {
                                val extracteddatetime = co.name.substring(co.name.length - 24, co.name.length)

                                val CREATE_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + UID +
                                        " INTEGER PRIMARY KEY AUTOINCREMENT, " + C_NAME + " VARCHAR(100) ," +
                                        C_TIME + " VARCHAR(100) ," + C_FILE + " BLOB NOT NULL );")
                                sq.execSQL(CREATE_TABLE)

                                Log.e("msg", "COURSEDATABASE1" + CREATE_TABLE)

                                val INSERT = ("INSERT INTO " + TABLE_NAME
                                        + "(" + C_NAME + "," + C_TIME + "," + C_FILE + ")"
                                        + " VALUES('" + co.name.substring(0, co.name.length - 24) + "','"
                                        + extracteddatetime + "','" + co.name + "');")
                                sq.execSQL(INSERT)
                                Log.e("msg", "COURSEDATABASE2" + INSERT)
                                ed_loginsave.putString("course_sqlite", "true")
                                ed_loginsave.commit()
                                pb_coursematerial.visibility=View.GONE
                                // checklist=true
                            }
                        }, 1000)


                    }

                }
                if(material.size == 0){
                    Handler().postDelayed({
                    pb_coursematerial.visibility=View.GONE
                   // Toast.makeText(applicationContext,"No file has uploaded..",Toast.LENGTH_SHORT).show()
                    }, 1000)
                }
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
        try {

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            val formatted = current.format(formatter)

            MaterialAlertDialogBuilder(this)
                    .setTitle(String.format(getString(R.string.upload_title), subject.name, subject.classId))
                    .setMessage(String.format(getString(R.string.upload_confirm), filename))
                    .setPositiveButton(android.R.string.yes) { dialog, _ ->
                        dialog.dismiss()
                        val status = Snackbar.make(courseListnew, getString(R.string.upload_started), Snackbar.LENGTH_INDEFINITE)
                        status.show()
                        fm.upload(filename + formatted, data)
                                .addOnSuccessListener {
                                    it.metadata?.let { metadata ->

                                        adapter?.add(CourseFile(filename, metadata))
                                        adapter?.notifyDataSetChanged()
                                        showClassMaterial()
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
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "" + e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun fetchTokens(textbody: String) {
        tokenlist.clear()
        val reference: DatabaseReference
        reference = FirebaseDatabase.getInstance().reference.child(schoolId).child("StudentTokens")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    Log.e("msg", "TOKENCLASS" + datas.child("classId").value.toString())
                    //Log.e("msg", "TOKENCLASS1" + datas.child("token").value.toString())
                    if (teacherClassId.equals(datas.child("classId").value.toString(), ignoreCase = true)) {
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


                sendFCMPush(tokenlist, textbody);
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })
    }

    private fun sendFCMPush(tokenlist: ArrayList<String>, body: String) {
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
                            FirebaseDatabase.getInstance().reference.child(schoolId).child("Notifications").push()

                    missionsReference.setValue(post)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CourseMaterial.RESULT_ACTION_PICK_MATERIAL -> {
                    data?.data?.getLastPathSegmentOnly(this)?.let { filename ->

                        val materialclassbody = "Hey! Your Subject Teacher " + subjectteachername +
                                " has shared a File.\nClick to Check Now !"
                        uploadFile(materialManager, filename, data.data!!, materialAdapter, materialclassbody)
                    }
                }


            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CourseMaterial.RC_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFile(pickRequestCode)
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