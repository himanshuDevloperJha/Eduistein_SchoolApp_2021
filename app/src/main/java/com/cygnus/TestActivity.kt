package com.cygnus

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import co.aspirasoft.adapter.ModelViewAdapter
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.TestsDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.*
import com.cygnus.view.TestScoreView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_test.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * TestActivity shows details of a single test.
 *
 * Purpose of this activity is to create a new test or view/update
 * details of an existing test.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class TestActivity : DashboardChildActivity() {
    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
    var subjectteachername:String?=null
    var teacherClassId:String?=null
    private lateinit var subject: Subject
    private lateinit var test: Test
    var tokenlist: ArrayList<String> = ArrayList()
    private val testTypes = mapOf(
            "Class Test" to ArrayList<String>().apply { (1..10).forEach { this.add(it.toString()) } },
            "Monthly Test" to ArrayList<String>().apply { (1..12).forEach { this.add(it.toString()) } },
            "Term Exam" to listOf("1st", "2nd", "Mid", "Final"),
            "Unit Test" to ArrayList<String>().apply { (1..10).forEach { this.add(it.toString()) } },
            "Weekly Test" to ArrayList<String>().apply { (1..52).forEach { this.add(it.toString()) } }
    )

    private lateinit var adapter: TestScoresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        subjectteachername= sp_loginsave.getString("SubjectTeacherName","")
        teacherClassId= sp_loginsave.getString("SubjectTeacherClassId","")
        // Schools cannot see this activity
        if (currentUser is School) return finish()

        // Must know which subject's grades we are managing
        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject? ?: return finish()

        // Create a new test instance
        test = Test(subject.classId, subject.name)

        // Is test name known?
        intent.getStringExtra(CygnusApp.EXTRA_TEST_NAME)?.let { test.name = it }

        // if YES, then we are editing/viewing an existing test
        if (test.name.isNotBlank()) {
            // Disable edit options
            setEditEnabled(false)

            // Show test name in action bar
            supportActionBar?.title = test.name
            typeSpinner.visibility = View.GONE
            subtypeSpinner.visibility = View.GONE

            // Look for test details in database
            TestsDao.get(schoolId, test, OnSuccessListener {
                // If no existing record found, we can't view/edit. CLOSE ACTIVITY
                if (it == null) return@OnSuccessListener finish()

                // Invoke callback method to update view items
                updateTestWith(it)
            })
        }

        // if NO, then we are creating a new test
        else {
            // Enable edit options
            setEditEnabled(true)

            // Init input fields to write test details (name, date, etc.)
            val typeAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, testTypes.keys.toTypedArray())
            typeSpinner.adapter = typeAdapter
            typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    testTypes[testTypes.keys.elementAt(position)]?.let {
                        val subtypeAdapter = ArrayAdapter(this@TestActivity, android.R.layout.simple_list_item_1, it)
                        subtypeSpinner.adapter = subtypeAdapter
                    }
                }
            }

            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            examDate.setText(formatter.format(System.currentTimeMillis()))
            selectDateButton.setOnClickListener {
                val picker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Exam Date")
                        .build()

                picker.addOnPositiveButtonClickListener {
                    examDate.setText(formatter.format(Date(it)))
                }
                picker.show(supportFragmentManager, picker.toString())
            }
        }

        adapter = TestScoresAdapter(this, test.obtainedScore)
        contentList.adapter = adapter
    }

    override fun updateUI(currentUser: User) {
        UsersDao.getStudentsInClass(schoolId, subject.classId, OnSuccessListener { it ->
            onStudentsReceived(it)
        })
    }

    fun onCreateButtonClicked(v: View) {
        test.name = "${subtypeSpinner.selectedItem} ${typeSpinner.selectedItem}"
        test.maxMarksTheory = theoryMarks.text.toString().toIntOrNull() ?: 0
        test.maxMarksPractical = practicalMarks.text.toString().toIntOrNull() ?: 0
        if (test.maxMarksTheory > 0) {
            practicalMarks.setText(test.maxMarksPractical.toString())
            // Disable edit options
            setEditEnabled(false)

            // Show test name in action bar
            supportActionBar?.title = test.name
            typeSpinner.visibility = View.GONE
            subtypeSpinner.visibility = View.GONE
        } else showError("Total marks are required!")
    }

    fun onSaveButtonClicked(v: View) {
        if (checkInputGradesValid()) {
            MaterialAlertDialogBuilder(this)
                    .setTitle("Save Grades")
                    .setMessage("Please make sure that all marks are correct. Do you wish to save?")
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        saveTest()
                    }
                    .setNegativeButton(android.R.string.no) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
        } else showError("Obtained marks cannot be higher than total marks!")
    }

    private fun checkInputGradesValid(): Boolean {
        for (record in test.obtainedScore) {
            if (record.theoryMarks > test.maxMarksTheory || record.practicalMarks > test.maxMarksPractical) {
                return false
            }
        }
        return true
    }

    private fun onStudentsReceived(students: List<Student>) {
        test.obtainedScore.clear()
        students.forEach {
            val record = TestScore().apply {
                this.studentName = it.name
                this.studentRollNo = it.rollNo
            }
            test.obtainedScore.add(record)
        }
        adapter.notifyDataSetChanged()

        if (test.name.isNotBlank()) {
            TestsDao.get(schoolId, test, OnSuccessListener { savedTest ->
                savedTest?.let { onTestReceived(it) }
            })
        }
    }

    private fun onTestReceived(saved: Test) {
        // Update test details
        updateTestWith(saved)

        // View updated details
        examDate.setText(saved.date)
        theoryMarks.setText(saved.maxMarksTheory.toString())
        practicalMarks.setText(saved.maxMarksPractical.toString())
        adapter.notifyDataSetChanged()
    }

    private fun saveTest() {
        val status = Snackbar.make(contentList, "Saving grades...", Snackbar.LENGTH_INDEFINITE)
        status.show()
        TestsDao.add(schoolId, test, OnCompleteListener {
            status.setText("Saving grades... Done!")
            fetchTokens()
            Handler().postDelayed({
                status.dismiss()
                finish()


            }, 1500L)
        })
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

                val scheduledclassbody="Hey! Your Subject Teacher "+subjectteachername+
                        " has shared your Marks from "+test.name+".\nClick to Check Now !"
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

                    val post = StoreNotifications(subjectteachername, body);

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

    private fun setEditEnabled(enabled: Boolean) {
        typeSpinner.isEnabled = enabled
        subtypeSpinner.isEnabled = enabled
        selectDateButton.isEnabled = enabled
        theoryMarks.isEnabled = enabled
        practicalMarks.isEnabled = enabled

        createButton.visibility = if (enabled) View.VISIBLE else View.GONE
        contentList.visibility = if (enabled) View.GONE else View.VISIBLE
        saveButton.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    private fun showError(error: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Snackbar.make(contentList, error, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getColor(R.color.colorWarning))
                    .setTextColor(Color.WHITE)
                    .show()
        }
    }

    private fun updateTestWith(saved: Test) {
        test.name = saved.name
        test.date = saved.date
        test.classId = saved.classId
        test.subjectId = saved.subjectId
        test.maxMarksTheory = saved.maxMarksTheory
        test.maxMarksPractical = saved.maxMarksPractical

        saved.obtainedScore.forEach { marks ->
            test.updateRecord(marks.studentRollNo!!, marks.theoryMarks, marks.practicalMarks)
        }
        adapter.notifyDataSetChanged()
    }

    private inner class TestScoresAdapter(context: Context, val scores: List<TestScore>)
        : ModelViewAdapter<TestScore>(context, scores, TestScoreView::class) {

        override fun notifyDataSetChanged() {
            scores.sortedBy { it.studentRollNo }
            super.notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            if (currentUser is Student) {
                (v as TestScoreView).setEditable(false)
                if (scores[position].studentRollNo != (currentUser as Student).rollNo) {
                    v.visibility = View.GONE
                }
            }

            return v
        }

    }

}