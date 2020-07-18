package com.cygnus.timetable

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cygnus.model.Lecture
import com.cygnus.model.Subject

class TimetablePagerAdapter(fm: FragmentManager, subjects: List<Subject>, val showClass: Boolean)
    : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val days = arrayOf("M", "T", "W", "T", "F", "S", "S")
    private val timetable = mapOf(
            1 to ArrayList<Pair<Subject, Lecture>>(),
            2 to ArrayList(),
            3 to ArrayList(),
            4 to ArrayList(),
            5 to ArrayList(),
            6 to ArrayList(),
            7 to ArrayList()
    )

    init {
        timetable.updateWith(subjects)
    }

    override fun getCount(): Int = timetable.size

    override fun getPageTitle(position: Int): CharSequence? {
        return days[position]
    }

    override fun getItem(position: Int): Fragment {
        return TimetableFragment(timetable[position + 1], showClass)
    }

    private fun Map<Int, ArrayList<Pair<Subject, Lecture>>>.updateWith(subjects: List<Subject>) {
        for (subject in subjects) {
            for (lecture in subject.appointments) {
                this[lecture.dayOfWeek]?.add(Pair(subject, lecture))
            }
        }
    }

}
