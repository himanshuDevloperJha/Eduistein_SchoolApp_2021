package com.cygnus.dao

import com.cygnus.CygnusApp
import com.cygnus.model.School
import com.cygnus.model.Student
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*

/**
 * A data access class to read details of app users.
 *
 * Purpose of this class is to provide methods for communicating with the
 * Firebase backend to access data related to app users.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object UsersDao {

    /**
     * Converts a [snapshot] to an instance of a [User] subclass.
     */
    private fun DataSnapshot.toUser(): User? {
        return try {
            val t = object : GenericTypeIndicator<HashMap<String, *>>() {}
            this.getValue(t)?.let {
                when (it["type"]) {
                    Student::class.simpleName -> this.getValue(Student::class.java)
                    Teacher::class.simpleName -> this.getValue(Teacher::class.java)
                    School::class.simpleName -> this.getValue(School::class.java)
                    else -> null
                }
            }
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * Adds a new user to database.
     *
     * @param schoolId The id of the user's school.
     * @param user The user to add.
     * @param listener A listener for receiving response of the request.
     */
    fun add(schoolId: String, user: User, invite: Invite, listener: OnCompleteListener<Void?>) {
        FirebaseDatabase.getInstance().reference.updateChildren(mapOf(
                "$schoolId/users/${user.id}/" to user,  // user details
                "user_schools/${user.id}/" to schoolId, // link to user's school
                "$schoolId/invites/${invite.id}/"       // mark invite as `Accepted`
                        to "${invite.sender}:${user.email}:${CygnusApp.STATUS_INVITE_ACCEPTED}"
        )).addOnCompleteListener(listener)
    }

    fun update(schoolId: String, user: User, listener: OnSuccessListener<Boolean>) {
        CygnusApp.refToUsers(schoolId)
                .child(user.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            CygnusApp.refToUsers(schoolId)
                                    .child(user.id)
                                    .setValue(user)
                                    .addOnCompleteListener {
                                        listener.onSuccess(it.isSuccessful)
                                    }
                        } else listener.onSuccess(false)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(false)

                    }
                })
    }

    /**
     * Retrieves a list of all students in a class.
     *
     * @param schoolId The id of the students' school.
     * @param classId The id of the students' class.
     * @param listener A listener for receiving response of the request.
     */
    fun getStudentsInClass(schoolId: String, classId: String, listener: OnSuccessListener<List<Student>>) {
        CygnusApp.refToUsers(schoolId)
                .orderByChild("classId")
                .equalTo(classId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val students = ArrayList<Student>()
                        snapshot.children.forEach {
                            val user = it.toUser()
                            if (user is Student && user.classId == classId) {
                                students.add(user)
                            }
                        }

                        listener.onSuccess(students)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(ArrayList())
                    }
                })
    }

    /**
     * Retrieves a [Student] by their roll number.
     *
     * @param schoolId The id of the student's school.
     * @param rollNo The roll number of the student.
     * @param listener A listener for receiving response of the request.
     */
    fun getStudentByRollNumber(schoolId: String, rollNo: String, listener: OnSuccessListener<Student?>) {
        CygnusApp.refToUsers(schoolId)
                .orderByChild("rollNo")
                .equalTo(rollNo)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val user = it.toUser()
                            if (user is Student && user.rollNo == rollNo) {
                                listener.onSuccess(user)
                                return
                            }
                        }

                        listener.onSuccess(null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

    /**
     * Retrieves a [User] by their email address.
     *
     * @param schoolId The id of the user's school.
     * @param email The email address of the user.
     * @param listener A listener for receiving response of the request.
     */
    fun getUserByEmail(schoolId: String, email: String, listener: OnSuccessListener<in User?>) {
        CygnusApp.refToUsers(schoolId)
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val user = it.toUser()
                            if (user?.email == email) {
                                listener.onSuccess(it.toUser())
                                return
                            }
                        }

                        listener.onSuccess(null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

    /**
     * Retrieves a [User] by their unique id.
     *
     * @param schoolId The id of the user's school.
     * @param userId The firebase id of the user.
     * @param listener A listener for receiving response of the request.
     */
    fun getUserById(schoolId: String, userId: String, listener: OnSuccessListener<in User?>) {
        CygnusApp.refToUsers(schoolId)
                .child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listener.onSuccess(snapshot.toUser())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(null)
                    }
                })
    }

    fun getTeacherByClass(schoolId: String, classId: String, listener: OnSuccessListener<in User?>) {
        ClassesDao.getClassesAtSchool(schoolId, OnSuccessListener { classes ->
            classes?.find { it.name == classId }?.teacherId?.let { teacherId ->
                getUserByEmail(schoolId, teacherId, listener)
            } ?: listener.onSuccess(null)
        })

    }

}