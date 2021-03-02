package com.cygnus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cygnus.adapter.StudentUploadFilesAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.model.StudentUploadFiles
import com.cygnus.model.Subject
import com.cygnus.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_studentfiles.*
import kotlinx.android.synthetic.main.activity_subject.*

class StudentFiles : DashboardChildActivity(), ZoomAutoAttendance {
    private val filesListTeacher = ArrayList<StudentUploadFiles>()
    var student_namee:String?=null
    private lateinit var subject: Subject
    private var stu: StudentUploadFilesAdapter? = null

    lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studentfiles)

        try{
            student_namee=intent.getStringExtra("student_namee");
            Log.e("msg","NAMEEE::"+student_namee);

        }
        catch (e:Exception){
            Log.e("msg","NAMEEE::11"+e.toString());

        }



        //Toast.makeText(applicationContext,"name1: "+student_namee,Toast.LENGTH_SHORT).show()
        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject? ?: return finish()
    }

    override fun updateUI(currentUser: User) {
        showStudentUploadFiles()
    }

    private fun showStudentUploadFiles() {
        filesListTeacher.clear()
        reference = FirebaseDatabase.getInstance().reference.child(schoolId).
                child("StudentUploadFiles")

        reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas1 in dataSnapshot.children) {
                    if (subject.classId.equals(datas1.child("classId").value.toString(), ignoreCase = true)) {

                        try {
                            val uploadfiles = StudentUploadFiles(datas1.child("classId").value.toString(),
                                    datas1.child("teacherId").value.toString(), datas1.child("subject").value.toString(),
                                    datas1.child("studentname").value.toString())
                            filesListTeacher.add(uploadfiles)
                        } catch (e: Exception) {
                            pb_stfiles.visibility=View.GONE

                            // Toast.makeText(applicationContext, "" + e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if(filesListTeacher.size==0){
                    Snackbar.make(ll_stfiles,"No Student Files has been uploaded",Snackbar.LENGTH_LONG).show()
                    pb_stfiles.visibility=View.GONE
                }
                else{
                    studentFilesList.setLayoutManager(LinearLayoutManager(applicationContext,
                            RecyclerView.VERTICAL, false))
                    stu = StudentUploadFilesAdapter(this@StudentFiles, filesListTeacher,
                            this@StudentFiles)
                    studentFilesList.setAdapter(stu)
                    pb_stfiles.visibility=View.GONE
                }



            }

            override fun onCancelled(databaseError: DatabaseError) {
                pb_stfiles.visibility=View.GONE
                //Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })

    }

    override fun studentuploadfiles(studentname: String?, position: Int, classid: String?) {
        startSecurely(ShowStudentUploadedFiles::class.java, Intent().apply {
            putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
            putExtra(CygnusApp.EXTRA_EDITABLE_MODE, true)
            putExtra("student_namee", studentname)
        })
    }

    override fun zoomauto(classid: String?, position: Int) {

    }

}