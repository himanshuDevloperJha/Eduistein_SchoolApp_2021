package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.Lecture
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

class LectureView : BaseView<Lecture> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val lectureDay: TabLayout
    private val lectureDetails: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_lecture, this)

        lectureDay = findViewById(R.id.lectureDay)
        lectureDay.touchables.forEach { it.isEnabled = false }

        lectureDetails = findViewById(R.id.lectureDetails)
    }

    override fun updateView(model: Lecture) {
        lectureDay.selectTab(lectureDay.getTabAt(model.dayOfWeek - 1))

        val formatter = SimpleDateFormat("hh:mma", Locale.getDefault())
        lectureDetails.text = String.format(
                Locale.getDefault(),
                "%s - %s",
                formatter.format(Date(model.startTime.time)),
                formatter.format(Date(model.endTime.time))
        )
    }


}