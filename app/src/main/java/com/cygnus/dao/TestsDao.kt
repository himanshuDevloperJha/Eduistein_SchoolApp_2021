package com.cygnus.dao

import com.cygnus.CygnusApp
import com.cygnus.model.Subject
import com.cygnus.model.Test
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

object TestsDao {

    fun add(schoolId: String, test: Test, listener: OnCompleteListener<Void?>) {
        CygnusApp.refToSubjects(schoolId, test.classId)
                .child(test.subjectId)
                .child("grades/")
                .child(test.name)
                .setValue(test)
                .addOnCompleteListener(listener)
    }

    /**
     * Gets record of a test's grades.
     *
     * @param schoolId The id of the class's school.
     * @param test
     * @param listener A listener for receiving response of the request.
     */
    fun get(schoolId: String, test: Test, listener: OnSuccessListener<Test?>) {
        CygnusApp.refToSubjects(schoolId, test.classId)
                .child(test.subjectId)
                .child("grades/")
                .child(test.name)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listener.onSuccess(snapshot.getValue(Test::class.java))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

    fun getTestsBySubject(schoolId: String, subject: String, classId: String, listener: OnSuccessListener<List<Test>?>) {
        CygnusApp.refToSubjects(schoolId, classId)
                .child(subject)
                .child("grades/")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, Test>>() {}
                        listener.onSuccess(snapshot.getValue(t)?.values?.toList())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }
}