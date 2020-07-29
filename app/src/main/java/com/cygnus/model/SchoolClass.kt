package com.cygnus.model

import co.aspirasoft.model.BaseModel

/**
 * SchoolClass is a class in a [School].
 *
 * A class consists of a group of [Student]s, and is assigned to
 * one [Teacher] who manages that class. Students in one class
 * study different [Subject]s, each taught by a particular
 * teacher.
 *
 * @param name Name of the class.
 * @param teacherId Unique id of the class teacher.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SchoolClass(name: String, teacherId: String,teachername:String,standard:String) : BaseModel()
{

    // no-arg constructor required for Firebase
    constructor() : this("", "","","")

    var name = name
        set(value) {
            field = value
            notifyObservers()
        }
    var teachername = teachername
        set(value) {
            field = value
            notifyObservers()
        }
    var standard = standard
        set(value) {
            field = value
            notifyObservers()
        }

    var teacherId = teacherId
        set(value) {
            field = value
            notifyObservers()
        }

    var subjects: HashMap<String, Subject>? = null

    var notices: HashMap<String, NoticeBoardPost>? = null

    var attendance: HashMap<String, Attendance>? = null

}