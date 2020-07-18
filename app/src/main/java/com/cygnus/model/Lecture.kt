package com.cygnus.model

import co.aspirasoft.model.BaseModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Lecture is a model class which represents a weekly appointment.
 *
 * An appointment is a weekly meeting held on a particular day, at a defined time
 * and a location.
 *
 * @constructor Creates a new appointment
 * @property dayOfWeek day of week when appointment is scheduled
 * @property startTime time of day when appointment start
 * @property endTime time of day when appointment ends
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class Lecture(var dayOfWeek: Int, var startTime: Date, var endTime: Date) : BaseModel() {

    // no-arg constructor required for Firebase
    constructor() : this(1, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()))

    init {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw IllegalStateException("Day of week must be in range 1-7 inclusive")
        }
    }

    override fun toString(): String {
        val formatter = SimpleDateFormat("hh:mm", Locale.getDefault())
        return "${formatter.format(startTime)} - ${formatter.format(endTime)}"
    }


}
