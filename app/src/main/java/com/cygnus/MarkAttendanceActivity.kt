package com.cygnus

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import co.aspirasoft.adapter.ModelViewAdapter
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.AttendanceDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.Attendance
import com.cygnus.model.AttendanceRecord
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.view.MarkAttendanceView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list.contentList
import kotlinx.android.synthetic.main.activity_mark_attendance.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class MarkAttendanceActivity : DashboardChildActivity() {
    var tokenlist: ArrayList<String> = ArrayList()

    private lateinit var attendance: Attendance

    private lateinit var currentTeacher: Teacher
    private lateinit var adapter: AttendanceAdapter
    var subjectteachername:String?=null
    var teacherClassId:String?=null
    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_attendance)

        sp_loginsave =getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        subjectteachername= sp_loginsave.getString("SubjectTeacherName","")
        teacherClassId= sp_loginsave.getString("SubjectTeacherClassId","")

        // Only allow a class teacher to access this page
        currentTeacher = when (currentUser) {
            is Teacher -> {
                val teacher = currentUser as Teacher
                if (teacher.classId == null) {
                    finish()
                    return
                }
                teacher
            }
            else -> {
                finish()
                return
            }
        }

        attendance = Attendance(currentTeacher.classId!!)
        dateView.text = attendance.date

        adapter = AttendanceAdapter(this, attendance.attendanceRecords)
        contentList.adapter = adapter

        saveButton.setOnClickListener {
            val status = Snackbar.make(contentList, "Saving attendance...", Snackbar.LENGTH_INDEFINITE)
            status.show()



            AttendanceDao.add(schoolId, attendance, OnCompleteListener {
                status.setText("Saving attendance... Done!")
                fetchTokens();
                Handler().postDelayed({ status.dismiss() }, 2500L)
            })
        }
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

                val scheduledclassbody="Hey! Your Attendance for the day has been marked.\nClick to Check Now !"
                sendFCMPush(tokenlist,scheduledclassbody);
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


    override fun updateUI(currentUser: User) {
        currentTeacher.classId?.let { classId ->
            UsersDao.getStudentsInClass(schoolId, classId, OnSuccessListener { it ->
                attendance.attendanceRecords.clear()
                it.forEach {
                    val record = AttendanceRecord().apply {
                        this.studentName = it.name
                        this.studentRollNo = it.rollNo
                        this.date = this@MarkAttendanceActivity.attendance.date
                        this.classId = classId
                        this.attendance = false
                    }
                    attendance.attendanceRecords.add(record)
                }
                adapter.notifyDataSetChanged()

                AttendanceDao.getByDate(schoolId, classId, attendance.date, OnSuccessListener {
                    savedAttendance ->
                    savedAttendance?.attendanceRecords?.forEach { savedRecord ->
                        attendance.updateRecord(savedRecord.studentRollNo!!, savedRecord.attendance)

                  Log.e("msg","Attendancee"+savedRecord.studentRollNo!!+", "+ savedRecord.attendance)  }
                    adapter.notifyDataSetChanged()
                })
            })
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
                .setTitle("Close Attendance")
                .setMessage("Make sure that you have saved any changes you made on this page before going back. Are you sure?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else super.onOptionsItemSelected(item)
    }

    private inner class AttendanceAdapter(context: Context, val records: List<AttendanceRecord>)
        : ModelViewAdapter<AttendanceRecord>(context, records, MarkAttendanceView::class) {

        override fun notifyDataSetChanged() {
            records.sortedBy { it.studentRollNo }
            super.notifyDataSetChanged()
        }

    }

}