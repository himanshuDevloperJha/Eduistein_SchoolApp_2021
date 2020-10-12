package com.cygnus.dao

import com.cygnus.CygnusApp
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * A data access class to read details of schools.
 *
 * Purpose of this class is to provide methods for communicating with the
 * Firebase backend to access data related to schools.
 *
 * @author https://github.com/Kushaggra
 * @since 1.0.0
 */
object SchoolDao {

    /**
     * Retrieves id of a user's school.
     *
     * @param userId The id of the user whose school is to be fetched.
     * @param listener A listener for receiving response of the request.
     */
    fun getSchoolByUser(userId: String, listener: OnSuccessListener<Pair<String, String>?>) {
        CygnusApp.refToSchoolId(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val schoolId = snapshot.value?.toString()
                        schoolId?.let {
                            getSchoolName(schoolId, OnSuccessListener { name ->
                                name?.let {
                                    listener.onSuccess(Pair(schoolId, name))
                                } ?: listener.onSuccess(null)
                            })
                        } ?: listener.onSuccess(null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

    /**
     * Retrieves name of a school.
     *
     * @param schoolId The id of the school.
     * @param listener A listener for receiving response of the request.
     */
    fun getSchoolName(schoolId: String, listener: OnSuccessListener<String?>) {
        CygnusApp.refToSchoolName(schoolId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listener.onSuccess(snapshot.value?.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

}