package com.cygnus.tasks

import android.content.Context
import co.aspirasoft.tasks.DummyTask
import co.aspirasoft.tasks.FirebaseTask
import com.cygnus.CygnusApp
import com.cygnus.dao.InvitesDao
import com.cygnus.utils.DynamicLinksUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query
import java.util.*

/**
 * InvitationTask is a [FirebaseTask] for sending invites.
 *
 * Purpose of this task is to asynchronously send a sign up invite
 * to [inviteeEmail] by generating a magic link.
 *
 * @param referral Unique referral code for generating invitation.
 */
class InvitationTask(
        val context: Context,
        private val referral: String,
        inviteeEmail: String,
        private val sender: String,
        private val classId: String? = null,
        private val rollNo: String? = null
) : FirebaseTask() {

    private val isTeacherInvite: Boolean get() = rollNo == null || classId == null
    private val inviteeEmail = inviteeEmail.toLowerCase(Locale.getDefault())

    /**
     * Checks if the invitee has previously been invited.
     *
     * Return `true` if an invitation for this invitee already exists.
     *
     * @param snapshot Snapshot of current database state.
     */
    private fun checkAlreadyInvited(snapshot: DataSnapshot): Boolean {
        var exists = false
        for (child in snapshot.children) {
            if (child.value.toString().split(":")[1].equals(inviteeEmail, true)) {
                exists = true
                break
            }
        }
        return exists
    }

    /**
     * Requests list of existing users.
     */
    override fun init(): Query {
        return CygnusApp.refToInvites(referral).orderByValue()
    }

    /**
     * Generates and sends an invitation.
     */
    override fun onQuerySuccess(): Task<Void?> {
        return FirebaseAuth.getInstance()
                .sendSignInLinkToEmail(inviteeEmail, when {
                    isTeacherInvite -> DynamicLinksUtils.createSignUpActionForTeacher(referral)
                    else -> DynamicLinksUtils.createSignUpActionForStudent(referral, rollNo!!, classId!!)
                }).addOnSuccessListener {
                    InvitesDao.add(referral, inviteeEmail, sender)
                }
    }

    /**
     * Callback for when invitee already exists in database.
     */
    override fun onQueryFailure(): Task<Void?> {
        return DummyTask(Exception("already exists"))
    }

    /**
     * Checks the precondition for sending the invite.
     *
     * A new invitation is only sent if the invitee is not already a member
     * and has not previously been invited.
     *
     * Return `true` if conditions satisfied or no initial query.
     */
    override fun checkCriteria(snapshot: DataSnapshot): Boolean {
        return !checkAlreadyInvited(snapshot)
    }

}
