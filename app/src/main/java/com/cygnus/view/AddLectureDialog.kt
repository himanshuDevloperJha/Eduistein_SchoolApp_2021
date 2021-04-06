package com.cygnus.view

import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import co.aspirasoft.util.InputUtils.showError
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cygnus.R
import com.cygnus.model.StoreNotifications
import com.cygnus.model.Subject
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AddLectureDialog : BottomSheetDialogFragment() {
    var tokenlist: ArrayList<String> = ArrayList()
    private lateinit var schoolId: String
    private lateinit var subject: Subject

    private lateinit var lectureDayField: TabLayout
    private lateinit var lectureTimeField: MaterialButton
    private lateinit var lectureDurationField: TextInputEditText
    private lateinit var okButton: Button
    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
    var subjectteachername:String?=null
    var teacherClassId:String?=null
    var onDismissListener: ((dialog: DialogInterface) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_add_lecture, container, false)

        sp_loginsave = v.context.getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        subjectteachername= sp_loginsave.getString("SubjectTeacherName","")
        teacherClassId= sp_loginsave.getString("SubjectTeacherClassId","")
        Log.e("msg","SUBJECT-TEACHERNAME"+subjectteachername)

        try {
            val args = requireArguments()
            schoolId = args.getString(ARG_SCHOOL_ID)!!
            subject = args.getSerializable(ARG_SUBJECT) as Subject
        } catch (ex: Exception) {
            Toast.makeText(v.context, ex.message, Toast.LENGTH_LONG).show()
            dismiss()
            return null
        }

        // Get UI references
        lectureDayField = v.findViewById(R.id.lectureDayField)
        lectureTimeField = v.findViewById(R.id.lectureTimeField)
        lectureDurationField = v.findViewById(R.id.lectureDurationField)
        okButton = v.findViewById(R.id.okButton)

        // Set up click handlers
        okButton.setOnClickListener { onOk(v) }

        // Init views
        lectureDayField.selectTab(lectureDayField.getTabAt(0))
        lectureTimeField.text = String.format("%02d:%02d", 0, 0)
        lectureTimeField.setOnClickListener {
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                lectureTimeField.text = String.format("%02d:%02d", hourOfDay, minute)
            }, 0, 0, true).show()
        }

        return v
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.let { it(dialog) }
    }

    private fun onOk(v: View) {
        val duration = lectureDurationField.text.toString().toIntOrNull()
        if (duration == null) lectureDurationField.showError("required")
        else {
            val startTime = SimpleDateFormat("hh:mm", Locale.getDefault()).parse(lectureTimeField.text.toString())
            val endTime = Date(startTime!!.time + (duration * 60000))
            val dayOfWeek = lectureDayField.selectedTabPosition + 1

            subject.addAppointment(dayOfWeek, startTime, endTime)
            subject.notifyObservers()
            dismiss()
            fetchTokens(v)

        }
    }

    private fun fetchTokens(v: View) {
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
                           // Toast.makeText(applicationContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Toast.makeText(applicationContext, "No scheduled class", Toast.LENGTH_SHORT).show();
                    }
                }

                val scheduledclassbody="Hey! Your Subject Teacher "+subjectteachername+
                        " has modified the time table.\nClick to Check Now !"
                sendFCMPush(tokenlist,scheduledclassbody,v);
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })
    }
    private fun sendFCMPush(tokenlist: ArrayList<String>, body: String, v: View) {
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
                    //Toast.makeText(v.context, "1:" + response.toString(), Toast.LENGTH_SHORT).show()
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
        val requestQueue = Volley.newRequestQueue(v.context)
        requestQueue.add(jsObjRequest)
    }

    companion object {
        private const val ARG_SCHOOL_ID = "school_id"
        private const val ARG_SUBJECT = "teacher_id"

        fun newInstance(
                schoolId: String,
                subject: Subject
        ): AddLectureDialog {
            return AddLectureDialog().also {
                it.arguments = Bundle().apply {
                    putString(ARG_SCHOOL_ID, schoolId)
                    putSerializable(ARG_SUBJECT, subject)
                }
            }
        }
    }

}