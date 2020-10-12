package com.cygnus.dao

import com.cygnus.CygnusApp
import com.cygnus.model.Subject
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

/**
 * A data access class to read details of subjects.
 *
 * Purpose of this class is to provide methods for communicating with the
 * Firebase backend to access data related to subjects.
 *
 * @author https://github.com/Kushaggra
 * @since 1.0.0
 */
object SubjectsDao {

    /**
     * Adds a new subject to database.
     *
     * @param schoolId The id of the school where the subject is taught.
     * @param subject The subject to add.
     * @param listener A listener for receiving response of the request.
     */
    fun add(schoolId: String, subject: Subject, listener: OnCompleteListener<Void?>) {
        CygnusApp.refToSubjects(schoolId, subject.classId)
                .child(subject.name)
                .setValue(subject)
                .addOnCompleteListener(listener)
    }

    /**
     * Retrieves a list of subjects taught by a teacher.
     *
     * @param schoolId The id of the school.
     * @param teacherId The email address of the teacher.
     * @param listener A listener for receiving response of the request.
     */
    fun getSubjectsByTeacher(schoolId: String, teacherId: String, listener: OnSuccessListener<List<Subject>>) {
        ClassesDao.getClassesAtSchool(schoolId, OnSuccessListener {
            val subjects = ArrayList<Subject>()
            it?.forEach { schoolClass ->
                schoolClass.subjects?.values?.forEach { subject ->
                    if (subject.teacherId == teacherId) {
                        subjects += subject
                    }
                }
            }
            listener.onSuccess(subjects)
        })
    }

    /**
     * Retrieves a list of subjects taught in a school class.
     *
     * @param schoolId The id of the school.
     * @param classId The id of the school class
     * @param listener A listener for receiving response of the request.
     */
    fun getSubjectsByClass(schoolId: String, classId: String, listener: OnSuccessListener<List<Subject>>) {
        CygnusApp.refToSubjects(schoolId, classId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, Subject>>() {}
                        listener.onSuccess(snapshot.getValue(t)?.values?.toList() ?: ArrayList())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(ArrayList())
                    }
                })
    }

}