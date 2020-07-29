package com.cygnus.model

import co.aspirasoft.model.BaseModel
import kotlin.reflect.KClass

/**
 * User is a model class representing an app user.
 *
 * This is a sealed class which cannot be instantiated itself, but instead
 * is used by creating objects of its subtypes, which represent different
 * types of users the app can have.
 *
 * @property id A unique id for this user.
 * @property name Name of the user.
 * @property email Email address of the user.
 * @property phone Phone number of the user.
 * @property address Physical address of the user.
 * @property credentials Credentials for accessing user account.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
sealed class User(var id: String, var name: String, var credentials: Credentials) : BaseModel() {

    open val type: String get() = this::class.simpleName ?: "Student"

    val email: String get() = credentials.email

    var address: String? = null
        set(value) {
            field = value
            setChanged()
        }

    var phone: String? = null
        set(value) {
            field = value
            setChanged()
        }

    var gender: String? = null
        set(value) {
            field = value
            setChanged()
        }

    companion object {
        @JvmStatic
        fun valueOf(s: String?): KClass<out User>? {
            return when (s) {
                School::class.simpleName -> School::class
                Student::class.simpleName -> Student::class
                Teacher::class.simpleName -> Teacher::class
                else -> null
            }
        }

        @JvmStatic
        fun values(): List<KClass<out User>> {
            return listOf(Teacher::class, Student::class, School::class)
        }
    }

}

/**
 * School is a model class representing an educational institution.
 *
 * All other types of users belong to a particular school.
 */
class School(id: String, name: String, credentials: Credentials) : User(id, name, credentials) {

    // no-arg constructor required for Firebase
    constructor() : this("", "", Credentials())

}

/**
 * Student at a [School], belonging to one [SchoolClass].
 *
 * @property classId The class to which this student belongs.
 * @property dateOfBirth (Optional) Student's date of birth.
 * @property emergencyContact (Optional) Name of the student's father.
 * @property emergencyEmail (Optional) Name of the student's mother.
 * @property rollNo Student's unique roll number in class.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class Student(id: String, name: String, credentials: Credentials,
              var classId: String, var rollNo: String)
    : User(id, name, credentials) {

    // no-arg constructor required for Firebase
    constructor() : this("", "", Credentials(), "", "")

    var dateOfBirth: String? = null
        set(value) {
            field = value
            setChanged()
        }

    var bloodType: String? = null
        set(value) {
            field = value
            setChanged()
        }

    var emergencyContact: String? = null
        set(value) {
            field = value
            setChanged()
        }

    var emergencyPhone: String? = null
        set(value) {
            field = value
            setChanged()
        }

    var emergencyEmail: String? = null
        set(value) {
            field = value
            setChanged()
        }

}

/**
 * Teacher is an instructor at a [School].
 *
 * An instructor teaches different [subjects] at the school. Optionally,
 * they may also be the class teacher of a particular [SchoolClass].
 *
 * @property classId (Optional) The class to which this teacher is assigned.
 * @property subjects List of classes this taught by this instructor.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class Teacher(id: String, name: String, credentials: Credentials) : User(id, name, credentials) {

    // no-arg constructor required for Firebase
    constructor() : this("", "", Credentials())

    var classId: String? = null
        set(value) {
            field = value
            setChanged()
        }

    private var subjects = ArrayList<Subject>()
        set(value) {
            field = value
            setChanged()
        }

    fun isClassTeacher(): Boolean {
        return !this.classId.isNullOrBlank()
    }

    fun addSubject(name: String) {
        subjects.add(Subject(name, this.id, ""))
        setChanged()
    }

    fun updateWith(teacher: Teacher) {
        this.id = teacher.id
        this.name = teacher.name
        this.credentials = teacher.credentials
        this.address = teacher.address
        this.phone = teacher.phone
        this.gender = teacher.gender
        this.classId = teacher.classId
        this.subjects = teacher.subjects
    }

}