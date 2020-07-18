package com.cygnus.dao

import android.os.Parcelable
import com.cygnus.CygnusApp
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A data access class to read details of sent invites.
 *
 * Purpose of this class is to provide methods for communicating with the
 * Firebase backend to access data related to sent sign up invites.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object InvitesDao {

    /**
     * Adds a new invite to the database.
     *
     * @param schoolId The id of the inviting school.
     * @param invitee Email address of the invitee.
     */
    fun add(schoolId: String, invitee: String, sender: String) {
        CygnusApp.refToInvites(schoolId)
                .push()
                .setValue("${sender}:${invitee}:${CygnusApp.STATUS_INVITE_PENDING}")
    }

    /**
     * Retrieves a sent invite, if found, from the database.
     *
     * @param schoolId The id of the school whose invite is to be retrieved.
     * @param invitee Email address of the invitee.
     * @param listener A listener for receiving response of the request.
     */
    fun get(schoolId: String, invitee: String, listener: OnSuccessListener<Invite?>) {
        getAll(schoolId, OnSuccessListener { invites ->
            val thisInvite = Invite("", "", invitee, "")
            if (invites != null && invites.contains(thisInvite)) {
                listener.onSuccess(invites.find { it.invitee == invitee })
            } else {
                listener.onSuccess(null)
            }
        })
    }

    /**
     * Retrieves a list of all, if any, invites sent by a school.
     *
     * A [List] of [Invite] items, or `null` is passed to the response [listener]
     * on successful completion of the retrieval request. A `null` response means
     * no invites for the given [schoolId] were found in database.
     *
     * @param schoolId The id of the school whose invites are to be retrieved.
     * @param listener A listener for receiving response of the request.
     */
    fun getAll(schoolId: String, listener: OnSuccessListener<List<Invite>?>) {
        val invitesRef = CygnusApp.refToInvites(schoolId)
        val callback = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val t = object : GenericTypeIndicator<HashMap<String, String>>() {}
                snapshot.getValue(t)?.let { map ->
                    val invites = ArrayList<Invite>()
                    map.forEach { entry ->
                        invites.add(Invite(
                                entry.key,
                                entry.value.split(":")[0],
                                entry.value.split(":")[1].toLowerCase(Locale.getDefault()),
                                entry.value.split(":")[2]
                        ))
                    }
                    listener.onSuccess(invites)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onSuccess(null)
            }
        }
        invitesRef.addListenerForSingleValueEvent(callback)
    }

    /**
     * Removes an invite from the school's records.
     *
     * @param schoolId The id of the inviting school.
     * @param invite The invite to remove.
     * @param listener A listener for receiving response of the request.
     */
    fun remove(schoolId: String, invite: Invite, listener: OnSuccessListener<Void>) {
        CygnusApp.refToInvites(schoolId)
                .child(invite.id)
                .removeValue()
                .addOnSuccessListener(listener)
    }

}

@Parcelize
data class Invite(val id: String, val sender: String, val invitee: String, val status: String) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Invite

        if (invitee != other.invitee) return false

        return true
    }

    override fun hashCode(): Int {
        return invitee.hashCode()
    }
}