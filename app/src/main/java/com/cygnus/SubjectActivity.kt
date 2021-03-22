package com.cygnus

import android.Manifest
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
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.util.PermissionUtils
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cygnus.adapter.StudentUploadFilesAdapter
import com.cygnus.chatstaff.Chat
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.SubjectsDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.*
import com.cygnus.storage.FileManager
import com.cygnus.storage.FileUtils.getLastPathSegmentOnly
import com.cygnus.storage.MaterialAdapter
import com.cygnus.view.AddLectureDialog
import com.cygnus.view.LectureView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_subject.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


/**
 * SubjectActivity shows details of a [Subject].
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SubjectActivity : DashboardChildActivity(),ZoomAutoAttendance {

    private lateinit var subject: Subject
    private var editable: Boolean = false

    private var appointmentsAdapter: AppointmentsAdapter? = null
    var tokenlist: ArrayList<String> = ArrayList()

    private lateinit var materialManager: FileManager
    private var materialAdapter: MaterialAdapter? = null
    private val material = ArrayList<CourseFile>()

    private var stu: StudentUploadFilesAdapter? = null

    lateinit var reference: DatabaseReference;
    private lateinit var homeworkManager: FileManager
    private var homeworkAdapter: MaterialAdapter? = null
    private val homework = ArrayList<CourseFile>()
    private val filesListTeacher = ArrayList<StudentUploadFiles>()

    private lateinit var filesManager: FileManager
    private var filesAdapter: MaterialAdapter? = null
    private val files = ArrayList<CourseFile>()

    lateinit var sp_loginsave: SharedPreferences;
    lateinit var ed_loginsave: SharedPreferences.Editor;
    var subjectteachername:String?=null
    var teacherClassId:String?=null
    var name:String?=null
    var rollNo:String?=null
     var subjectTeacher_namee:String=""
     var subjectTeacher_nameonly:String=""
     var userrtypeeee:String=""
     var user_tokennn:String=""
    private var pickRequestCode = RESULT_ACTION_PICK_MATERIAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", Context.MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()

        subjectteachername= sp_loginsave.getString("SubjectTeacherName","")
        teacherClassId= sp_loginsave.getString("SubjectTeacherClassId","")




        name = intent.getStringExtra("user_name")
        user_tokennn = intent.getStringExtra("user_tokennn")
        subjectTeacher_namee = intent.getStringExtra("subjectTeacher_namee")
        userrtypeeee = intent.getStringExtra("userrtypeeee")
        Log.e("msg","nameeeeeeeeee"+name);

        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject? ?: return finish()
        editable = intent.getBooleanExtra(CygnusApp.EXTRA_EDITABLE_MODE, editable)


        if (editable) {
            addAppointmentButton.visibility = View.VISIBLE
            //addMaterialButton.visibility = View.VISIBLE
           // addHomeworkButton.visibility = View.VISIBLE
            tv_studentFiles.visibility = View.VISIBLE
          //  studentFilesList.visibility = View.VISIBLE
          //  showStudentUploadFiles()

            addFilesButton.visibility = View.GONE
            ll_uploadFiles.visibility = View.GONE
            filesList.visibility = View.GONE
            groupChat.visibility = View.VISIBLE
        }


        UsersDao.getUserByEmail(schoolId, subjectTeacher_namee, OnSuccessListener { user ->
            subjectTeacher_nameonly = user?.name ?: subjectTeacher_namee
            Log.e("msg","subjectTeacher_nameonlysubjectTeacher_nameonly"+subjectTeacher_nameonly);
        })
        materialManager = FileManager.newInstance(this, "$schoolId/courses/${subject.classId}/subjects/${subject.name}/lectures/")
        homeworkManager = FileManager.newInstance(this, "$schoolId/courses/${subject.classId}/subjects/${subject.name}/exercises/")
        filesManager = FileManager.newInstance(this, "$schoolId/courses/${subject.classId}/subjects/${subject.name}$name/files/")

        tv_studentFiles.setOnClickListener {
            startSecurely(StudentFiles::class.java, Intent().apply {
                putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
                putExtra("student_namee", name)

            })
        }

        tv_coursematerial.setOnClickListener {
            startSecurely(CourseMaterial::class.java, Intent().apply {
                putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
                putExtra("student_namee", name)
                ed_loginsave.putString("course_sqlite", "false")
                ed_loginsave.commit()


            })
        }
        tv_homework.setOnClickListener {
            startSecurely(HomeworkMaterial::class.java, Intent().apply {
                putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
                putExtra("student_namee", name)

            })
        }
        tv_uploadfiles.setOnClickListener {
            startSecurely(StudentFileUpload::class.java, Intent().apply {
                putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
                putExtra("student_namee", name)

            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_ACTION_PICK_MATERIAL -> {
                    data?.data?.getLastPathSegmentOnly(this)?.let { filename ->

                        val materialclassbody="Hey! Your Subject Teacher "+subjectteachername+
                                " has shared a File.\nClick to Check Now !"
                        uploadFile(materialManager, filename, data.data!!, materialAdapter,materialclassbody)
                    }
                }

                RESULT_ACTION_PICK_HOMEWORK -> {
                    data?.data?.getLastPathSegmentOnly(this)?.let { filename ->
                        val homeworkclassbody="Hey! Your Subject Teacher "+subjectteachername+
                                " has assigned a new Homework.\nClick to Solve Now !"
                        uploadFile(homeworkManager, filename, data.data!!, homeworkAdapter,homeworkclassbody)
                    }
                }
                RESULT_ACTION_PICK_FILES -> {
                    data?.data?.getLastPathSegmentOnly(this)?.let { filename ->


                        val post = StudentUploadFiles(subject.classId, subject.teacherId,
                                subject.name, name);

                        val missionsReference =
                                FirebaseDatabase.getInstance().reference.child(schoolId).
                                        child("StudentUploadFiles").child(name.toString())
                        missionsReference.setValue(post)

                        uploadFile(filesManager, filename, data.data!!, filesAdapter,"")
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
        // Show subject details
        supportActionBar?.title = subject.name
        className.text = subject.classId
        UsersDao.getUserByEmail(schoolId, subject.teacherId, OnSuccessListener {
            teacherName.text = "Teacher: " + (it?.name ?: subject.teacherId)
        })

        // Show lecture times
        appointmentsAdapter = AppointmentsAdapter(this, subject.appointments)

        appointmentsList.adapter = appointmentsAdapter

        // Show course material
        showCourseContents()
    }

    fun onAddAppointmentClicked(v: View) {
        AddLectureDialog.newInstance(schoolId, subject)
                .apply {
                    onDismissListener = {
                        appointmentsAdapter?.notifyDataSetChanged()
                        SubjectsDao.add(schoolId, subject, OnCompleteListener { })
                    }
                }
                .show(supportFragmentManager, "add_lecture_dialog")

    }


    fun onAddMaterialClicked(v: View) {
        if (PermissionUtils.requestPermissionIfNeeded(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        getString(R.string.permission_storage),
                        RC_WRITE_PERMISSION
                )) {
            pickFile(RESULT_ACTION_PICK_MATERIAL)
        }
    }

    fun onAddHomeworkClicked(v: View) {
        if (PermissionUtils.requestPermissionIfNeeded(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        getString(R.string.permission_storage),
                        RC_WRITE_PERMISSION
                )) {
            pickFile(RESULT_ACTION_PICK_HOMEWORK)
        }
    }

    fun onAddFilesClicked(v: View) {
        if (PermissionUtils.requestPermissionIfNeeded(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        getString(R.string.permission_storage),
                        RC_WRITE_PERMISSION
                )) {
            pickFile(RESULT_ACTION_PICK_FILES)
        }
    }

    fun onNoticeBoardClicked(v: View) {
        startSecurely(NoticeActivity::class.java, Intent().apply {
            putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
        })
    }

    fun onAddGradesClicked(v: View) {
        startSecurely(TestsActivity::class.java, Intent().apply {
            putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
        })
    }
    fun onChatClick(v: View) {
if(userrtypeeee.equals("Student")){
    val post = ChatTokens(name.toString(),subject.classId, user_tokennn,subject.name+" "+subject.classId)
    val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolId).
            child("ChatTokens").child(name.toString())

    missionsReference.setValue(post)
}
        else if(userrtypeeee.equals("Teacher")){
    val post = ChatTokens(name.toString(),subject.classId, user_tokennn,"")
    val missionsReference = FirebaseDatabase.getInstance().reference.child(schoolId).
            child("ChatTokens").child(name.toString())

    missionsReference.setValue(post)
}

        val i = Intent(applicationContext, Chat::class.java)
        i.putExtra("schoolid_chat", schoolId)
        i.putExtra("chat_groupuser", subjectTeacher_nameonly)
        i.putExtra("chat_username", name)
        i.putExtra("currentUser", name)
        i.putExtra("name_chatwith",subject.name+" "+subject.classId)
        i.putExtra("userrtypeeee", userrtypeeee)
        i.putExtra("user_tokennnnn", user_tokennn)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        startActivity(i)
    }

    private fun showCourseContents() {
        showClassMaterial()
        showHomeworkExercises()
               showFiles()

    }

    private fun showClassMaterial() {
        if (materialAdapter == null) {
            materialAdapter = MaterialAdapter(this, material, materialManager)
            contentList.adapter = materialAdapter
        }

        materialManager.listAll().addOnSuccessListener { result ->
            material.clear()
            result?.items?.forEach { reference ->
                reference.metadata.addOnSuccessListener { metadata ->
                    material.add(CourseFile(reference.name, metadata))
                    materialAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun showHomeworkExercises() {
        if (homeworkAdapter == null) {
            homeworkAdapter = MaterialAdapter(this, homework, homeworkManager)
            homeworkList.adapter = homeworkAdapter
        }

        homeworkManager.listAll().addOnSuccessListener { result ->
            homework.clear()
            result?.items?.forEach { reference ->
                reference.metadata.addOnSuccessListener { metadata ->
                    homework.add(CourseFile(reference.name, metadata))
                    homeworkAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun showFiles() {
        if (filesAdapter == null) {
            filesAdapter = MaterialAdapter(this, files, filesManager)
            filesList.adapter = filesAdapter
        }

        filesManager.listAll().addOnSuccessListener { result ->
            files.clear()
            result?.items?.forEach { reference ->
                reference.metadata.addOnSuccessListener { metadata ->
                    files.add(CourseFile(reference.name, metadata))
                    filesAdapter?.notifyDataSetChanged()
                }
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

    private fun uploadFile(fm: FileManager, filename: String, data: Uri, adapter: MaterialAdapter?,
                           textbody: String) {
        try{
            MaterialAlertDialogBuilder(this)
                    .setTitle(String.format(getString(R.string.upload_title), subject.name, subject.classId))
                    .setMessage(String.format(getString(R.string.upload_confirm), filename))
                    .setPositiveButton(android.R.string.yes) { dialog, _ ->
                        dialog.dismiss()
                        val status = Snackbar.make(contentList, getString(R.string.upload_started), Snackbar.LENGTH_INDEFINITE)
                        status.show()
                        fm.upload(filename, data)
                                .addOnSuccessListener {
                                    it.metadata?.let { metadata ->

                                        adapter?.add(CourseFile(filename, metadata))
                                        adapter?.notifyDataSetChanged()
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
            Toast.makeText(applicationContext,""+e.toString(),Toast.LENGTH_SHORT).show()
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

    private inner class AppointmentsAdapter(context: Context, val lectures: List<Lecture>)
        : ModelViewAdapter<Lecture>(context, lectures, LectureView::class) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            if (currentUser is Teacher) {
                v.findViewById<View>(R.id.deleteButton)?.apply {
                    this.visibility = View.VISIBLE
                    this.setOnClickListener {
                        MaterialAlertDialogBuilder(context)
                                .setTitle("Remove lecture time?")
                                .setPositiveButton("Delete") { dialog, _ ->
                                    subject.appointments.remove(lectures[position])
                                    notifyDataSetChanged()
                                    SubjectsDao.add(schoolId, subject, OnCompleteListener { })
                                    dialog.dismiss()
                                }
                                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                                    dialog.cancel()
                                }
                                .show()
                    }
                }
            }
            return v
        }

    }

    companion object {
        private const val RESULT_ACTION_PICK_MATERIAL = 100
        private const val RESULT_ACTION_PICK_HOMEWORK = 150
        private const val RESULT_ACTION_PICK_FILES = 250
        private const val RC_WRITE_PERMISSION = 200
    }

    override fun studentuploadfiles(studentname: String?, position: Int, classid: String?) {

    }

    override fun zoomauto(classid: String?, position: Int) {

    }


}