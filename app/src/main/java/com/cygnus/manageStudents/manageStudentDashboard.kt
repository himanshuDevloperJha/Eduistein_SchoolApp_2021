package com.cygnus.manageStudents

//package com.cygnus.manageStudents
//
//import android.app.ProgressDialog
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import co.aspirasoft.util.InputUtils.hideKeyboard
//import co.aspirasoft.util.InputUtils.isNotBlank
//import co.aspirasoft.util.InputUtils.showError
//import com.cygnus.CygnusApp
//import com.cygnus.R
//import com.cygnus.dao.Invite
//import com.cygnus.dao.InvitesDao
//import com.cygnus.model.User
//import com.cygnus.tasks.InvitationTask
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.android.gms.tasks.OnSuccessListener
//import com.google.android.material.snackbar.Snackbar
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.android.synthetic.main.activity_school.*
//import kotlinx.android.synthetic.main.activity_school.invitedStaffButton
//import kotlinx.android.synthetic.main.activity_school.joinedStaffButton
//import kotlinx.android.synthetic.main.manage_students.*
//
//class manageStudentDashboard: AppCompatActivity() {
//
//    private val invitedStudents = ArrayList<Invite>()
//    private val joinedStudents = ArrayList<Invite>()
//    lateinit var currentUser: User
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val signedInUser = FirebaseAuth.getInstance().currentUser       // firebase auth -> (1)
//        val user = intent.getSerializableExtra(CygnusApp.EXTRA_USER) as User? // account details -> (2)
//        currentUser = when {
//            signedInUser == null || user == null -> return finish() // both (1) and (2) must exist
//            user.id == signedInUser.uid -> user                     // and both must belong to same user
//            else -> return finish()                                 // else finish activity
//        }
//        setContentView(R.layout.manage_students)
//
//    }
//
//    /**
//     * Sends a link to [email] which can be used to create a Teacher account.
//     *
//     * @param listener Optional callback to listen for completion of invitation task.
//     */
//    private fun inviteSingleEmailStudent(email: String, listener: OnCompleteListener<Void?>? = null) {
//        InvitationTask(this, currentUser.id, email, teacherID)
//                .start { task ->
//                    if (task.isSuccessful || task.exception == null) {
//                        Toast.makeText(this, getString(R.string.status_invitation_sent), Toast.LENGTH_LONG).show()
//                        emailField.setText("")
//                    } else {
//                        emailField.showError("Email ${task.exception?.message
//                                ?: "could not be sent"}.")
//                    }
//
//                    listener?.let { task.addOnCompleteListener(it) }
//                }
//    }
//
//    fun onInviteSingleClickedStudent(v: View) {
//        if (emailFieldStudent.isNotBlank()) {
//            hideKeyboard()
//            val email = emailFieldStudent.text.toString().trim()
//            val progressDialog = ProgressDialog.show(
//                    this,
//                    getString(R.string.status_invitation_sending),
//                    String.format(getString(R.string.status_invitation_progress), email),
//                    true
//            )
//
//            inviteSingleEmailStudent(email, OnCompleteListener {
//                progressDialog.dismiss()
//                if (!it.isSuccessful) {
//                    emailField.showError(it.exception?.message
//                            ?: getString(R.string.error_invitation_failure))
//                }
//            })
//        }
//    }
//
//
//
//    fun onJoinedStudentClicked(v: View) {
//        hideKeyboard()
//        if (joinedStudents.size > 0) {
//            startSecurely(SchoolTeachersActivity::class.java, Intent().apply {
//                putExtra(CygnusApp.EXTRA_INVITE_STATUS, getString(R.string.status_invite_accepted))
//                putExtra(CygnusApp.EXTRA_INVITES, joinedStudents)
//            })
//        } else {
//            Snackbar.make(joinedStaffButton, "No staff members have joined yet.", Snackbar.LENGTH_SHORT).show()
//        }
//    }
//
//    /**
//     * Displays number of invited and joined staff members.
//     */
//    private fun showStaffStats(invited: Int, joined: Int) {
//        invitedStaffButton.text = String.format(getString(R.string.label_staff_invited), invited)
//        joinedStaffButton.text = String.format(getString(R.string.label_staff_joined), joined)
//    }
//
//    /**
//     * Listens for changes in status of sent invites.
//     *
//     * Monitors all sent invites and displays the number of accepted
//     * and pending invites in realtime.
//     */
//    private fun trackSentInvites() {
//        InvitesDao.getAll(currentUser.id, OnSuccessListener {
//            invitedStudents.clear()
//            joinedStudents.clear()
//            it.orEmpty().forEach { invite ->
//                if (invite.sender == schoolId) {
//                    when (invite.status) {
//                        CygnusApp.STATUS_INVITE_PENDING -> invitedStudents.add(invite)
//                        else -> joinedStudents.add(invite)
//                    }
//                }
//            }
//            showStaffStats(invitedStudents.size, joinedStudents.size)
//        })
//    }
//
//
//
//}