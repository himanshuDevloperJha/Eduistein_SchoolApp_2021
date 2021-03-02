package com.cygnus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.TestsDao
import com.cygnus.model.*
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_list.*

/**
 * TestsActivity displays names of all tests of a subject
 *
 * Purpose of this activity is to show a list of all graded tests
 * in a subject, and allow teachers to create and update test
 * grades.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class TestsActivity : DashboardChildActivity() {

    private lateinit var subject: Subject
    private lateinit var pb_attendance: ProgressBar
    private val tests = ArrayList<Test>()
    private val testNames = ArrayList<String>()
    private val testScores = ArrayList<TestScore>()

    private lateinit var adapter: TestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        pb_attendance=findViewById(R.id.pb_attendance)

        val subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject?
        if (subject == null) {
            finish()
            return
        }
        this.subject = subject

        addButton.visibility = if (currentUser is Teacher) View.VISIBLE else View.GONE
        addButton.setOnClickListener { onAddGradesClicked() }
    }

    override fun updateUI(currentUser: User) {
        TestsDao.getTestsBySubject(schoolId, subject.name, subject.classId, OnSuccessListener {
            this.tests.clear()
            it?.let { savedTests -> tests.addAll(savedTests) }

            this.testNames.clear()
            this.tests.forEach { test ->
                if (currentUser is Student) {
                    test.obtainedScore.find { it.studentRollNo == currentUser.rollNo }?.let { score ->
                        testNames.add(test.name + "\n${score.theoryMarks + score.practicalMarks} / ${test.maxMarksTheory + test.maxMarksPractical}")
                    }
                } else {
                    testNames.add(test.name)
                }
            }

            this.adapter = TestAdapter(this, testNames)
            contentList.adapter = this.adapter
            pb_attendance.visibility=View.GONE
        })
    }

    private fun onAddGradesClicked() {
        startSecurely(TestActivity::class.java, Intent().apply {
            putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
        })
    }

    private inner class TestAdapter(context: Context, val grades: ArrayList<String>)
        : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, grades) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            if (currentUser !is Student) v.setOnClickListener {
                startSecurely(TestActivity::class.java, Intent().apply {
                    putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
                    putExtra(CygnusApp.EXTRA_TEST_NAME, grades[position])
                })
            }
            return v
        }

    }

}