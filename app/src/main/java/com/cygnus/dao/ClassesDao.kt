package com.cygnus.dao

import com.cygnus.CygnusApp
import com.cygnus.model.SchoolClass
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

/**
 * A data access class to read details of school classes.
 *
 * Purpose of this class is to provide methods for communicating with the
 * Firebase backend to access data related to school classes.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object ClassesDao {

    /**
     * Adds a new class to database.
     *
     * @param schoolId The id of the class's school.
     * @param schoolClass The school class to add.
     * @param listener A listener for receiving response of the request.
     */
    fun add(schoolId: String, schoolClass: SchoolClass, listener: OnCompleteListener<Void?>) {
        CygnusApp.refToClasses(schoolId)
                .child(schoolClass.name)
                .setValue(schoolClass)
                .addOnCompleteListener(listener)
    }

    /**
     * Retrieves a list of classes in a school.
     *
     * @param schoolId The id of the school.
     * @param listener A listener for receiving response of the request.
     */
    fun getClassesAtSchool(schoolId: String, listener: OnSuccessListener<List<SchoolClass>?>) {
        CygnusApp.refToClasses(schoolId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, SchoolClass>>() {}
                        snapshot.getValue(t)?.values?.let {
                            listener.onSuccess(it.toList())
                        } ?: listener.onSuccess(null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

    fun getClassByTeacher(schoolId: String, teacherId: String, listener: OnSuccessListener<SchoolClass?>) {
        CygnusApp.refToClasses(schoolId)
                .orderByChild("teacherId")
                .equalTo(teacherId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, SchoolClass>>() {}
                        listener.onSuccess(snapshot.getValue(t)?.values?.elementAtOrNull(0))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

}