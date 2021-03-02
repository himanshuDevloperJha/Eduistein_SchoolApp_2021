package com.cygnus

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.cygnus.core.DashboardChildActivity
import com.cygnus.model.CourseFile
import com.cygnus.model.Subject
import com.cygnus.model.User
import com.cygnus.storage.FileManager
import com.cygnus.storage.MaterialAdapter
import kotlinx.android.synthetic.main.activity_showsstudentfiles.*

class ShowStudentUploadedFiles : DashboardChildActivity() {
    private lateinit var subject: Subject
    private lateinit var filesManager: FileManager
    private var filesAdapter: MaterialAdapter? = null
    private val files = ArrayList<CourseFile>()
    var student_namee:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showsstudentfiles)

        student_namee=intent.getStringExtra("student_namee");
        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject? ?: return finish()

        filesManager = FileManager.newInstance(this, "$schoolId/courses/${subject.classId}/subjects/${subject.name}$student_namee/files/")


        showFiles()
    }

    private fun showFiles() {
        if (filesAdapter == null) {
            filesAdapter = MaterialAdapter(this, files, filesManager)
            filesListt.adapter = filesAdapter
        }

        filesManager.listAll().addOnSuccessListener { result ->
            files.clear()
            pb_studentmaterial.visibility= View.VISIBLE
            result?.items?.forEach { reference ->
                reference.metadata.addOnSuccessListener { metadata ->

                    Handler().postDelayed({
                        files.add(CourseFile(reference.name, metadata))
                        filesAdapter?.notifyDataSetChanged()
                        pb_studentmaterial.visibility= View.GONE

                        // Toast.makeText(applicationContext,"No file has uploaded..",Toast.LENGTH_SHORT).show()
                    }, 1000)


                }

            }

        }
    }


    override fun updateUI(currentUser: User) {

    }


}