package com.cygnus

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.util.PermissionUtils
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
import kotlinx.android.synthetic.main.activity_subject.*


/**
 * SubjectActivity shows details of a [Subject].
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SubjectActivity : DashboardChildActivity() {

    private lateinit var subject: Subject
    private var editable: Boolean = false

    private var appointmentsAdapter: AppointmentsAdapter? = null

    private lateinit var materialManager: FileManager
    private var materialAdapter: MaterialAdapter? = null
    private val material = ArrayList<CourseFile>()

    private lateinit var homeworkManager: FileManager
    private var homeworkAdapter: MaterialAdapter? = null
    private val homework = ArrayList<CourseFile>()

    private var pickRequestCode = RESULT_ACTION_PICK_MATERIAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject? ?: return finish()
        editable = intent.getBooleanExtra(CygnusApp.EXTRA_EDITABLE_MODE, editable)
        if (editable) {
            addAppointmentButton.visibility = View.VISIBLE
            addMaterialButton.visibility = View.VISIBLE
            addHomeworkButton.visibility = View.VISIBLE
        }

        materialManager = FileManager.newInstance(this, "$schoolId/courses/${subject.classId}/subjects/${subject.name}/lectures/")
        homeworkManager = FileManager.newInstance(this, "$schoolId/courses/${subject.classId}/subjects/${subject.name}/exercises/")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_ACTION_PICK_MATERIAL -> {
                    data?.data?.getLastPathSegmentOnly(this)?.let { filename ->
                        uploadFile(materialManager, filename, data.data!!, materialAdapter)
                    }
                }

                RESULT_ACTION_PICK_HOMEWORK -> {
                    data?.data?.getLastPathSegmentOnly(this)?.let { filename ->
                        uploadFile(homeworkManager, filename, data.data!!, homeworkAdapter)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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

    private fun showCourseContents() {
        showClassMaterial()
        showHomeworkExercises()
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

    private fun pickFile(requestCode: Int) {
        this.pickRequestCode = requestCode

        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "application/*"
        i.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(i, requestCode)
    }

    private fun uploadFile(fm: FileManager, filename: String, data: Uri, adapter: MaterialAdapter?) {
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
                                }

                                status.setText(getString(R.string.upload_success))
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
        private const val RC_WRITE_PERMISSION = 200
    }

}