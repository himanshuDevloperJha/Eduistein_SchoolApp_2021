package com.cygnus

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.TestsDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.*
import com.cygnus.view.TestScoreView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_test.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * TestActivity shows details of a single test.
 *
 * Purpose of this activity is to create a new test or view/update
 * details of an existing test.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class TestActivity : DashboardChildActivity() {

    private lateinit var subject: Subject
    private lateinit var test: Test

    private val testTypes = mapOf(
            "Class Test" to ArrayList<String>().apply { (1..10).forEach { this.add(it.toString()) } },
            "Monthly Test" to ArrayList<String>().apply { (1..12).forEach { this.add(it.toString()) } },
            "Term Exam" to listOf("1st", "2nd", "Mid", "Final"),
            "Unit Test" to ArrayList<String>().apply { (1..10).forEach { this.add(it.toString()) } },
            "Weekly Test" to ArrayList<String>().apply { (1..52).forEach { this.add(it.toString()) } }
    )

    private lateinit var adapter: TestScoresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Schools cannot see this activity
        if (currentUser is School) return finish()

        // Must know which subject's grades we are managing
        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject? ?: return finish()

        // Create a new test instance
        test = Test(subject.classId, subject.name)

        // Is test name known?
        intent.getStringExtra(CygnusApp.EXTRA_TEST_NAME)?.let { test.name = it }

        // if YES, then we are editing/viewing an existing test
        if (test.name.isNotBlank()) {
            // Disable edit options
            setEditEnabled(false)

            // Show test name in action bar
            supportActionBar?.title = test.name
            typeSpinner.visibility = View.GONE
            subtypeSpinner.visibility = View.GONE

            // Look for test details in database
            TestsDao.get(schoolId, test, OnSuccessListener {
                // If no existing record found, we can't view/edit. CLOSE ACTIVITY
                if (it == null) return@OnSuccessListener finish()

                // Invoke callback method to update view items
                updateTestWith(it)
            })
        }

        // if NO, then we are creating a new test
        else {
            // Enable edit options
            setEditEnabled(true)

            // Init input fields to write test details (name, date, etc.)
            val typeAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, testTypes.keys.toTypedArray())
            typeSpinner.adapter = typeAdapter
            typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    testTypes[testTypes.keys.elementAt(position)]?.let {
                        val subtypeAdapter = ArrayAdapter(this@TestActivity, android.R.layout.simple_list_item_1, it)
                        subtypeSpinner.adapter = subtypeAdapter
                    }
                }
            }

            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            examDate.setText(formatter.format(System.currentTimeMillis()))
            selectDateButton.setOnClickListener {
                val picker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Exam Date")
                        .build()

                picker.addOnPositiveButtonClickListener {
                    examDate.setText(formatter.format(Date(it)))
                }
                picker.show(supportFragmentManager, picker.toString())
            }
        }

        adapter = TestScoresAdapter(this, test.obtainedScore)
        contentList.adapter = adapter
    }

    override fun updateUI(currentUser: User) {
        UsersDao.getStudentsInClass(schoolId, subject.classId, OnSuccessListener { it ->
            onStudentsReceived(it)
        })
    }

    fun onCreateButtonClicked(v: View) {
        test.name = "${subtypeSpinner.selectedItem} ${typeSpinner.selectedItem}"
        test.maxMarksTheory = theoryMarks.text.toString().toIntOrNull() ?: 0
        test.maxMarksPractical = practicalMarks.text.toString().toIntOrNull() ?: 0
        if (test.maxMarksTheory > 0) {
            practicalMarks.setText(test.maxMarksPractical.toString())
            // Disable edit options
            setEditEnabled(false)

            // Show test name in action bar
            supportActionBar?.title = test.name
            typeSpinner.visibility = View.GONE
            subtypeSpinner.visibility = View.GONE
        } else showError("Total marks are required!")
    }

    fun onSaveButtonClicked(v: View) {
        if (checkInputGradesValid()) {
            MaterialAlertDialogBuilder(this)
                    .setTitle("Save Grades")
                    .setMessage("Please make sure that all marks are correct. Do you wish to save?")
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        saveTest()
                    }
                    .setNegativeButton(android.R.string.no) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
        } else showError("Obtained marks cannot be higher than total marks!")
    }

    private fun checkInputGradesValid(): Boolean {
        for (record in test.obtainedScore) {
            if (record.theoryMarks > test.maxMarksTheory || record.practicalMarks > test.maxMarksPractical) {
                return false
            }
        }
        return true
    }

    private fun onStudentsReceived(students: List<Student>) {
        test.obtainedScore.clear()
        students.forEach {
            val record = TestScore().apply {
                this.studentName = it.name
                this.studentRollNo = it.rollNo
            }
            test.obtainedScore.add(record)
        }
        adapter.notifyDataSetChanged()

        if (test.name.isNotBlank()) {
            TestsDao.get(schoolId, test, OnSuccessListener { savedTest ->
                savedTest?.let { onTestReceived(it) }
            })
        }
    }

    private fun onTestReceived(saved: Test) {
        // Update test details
        updateTestWith(saved)

        // View updated details
        examDate.setText(saved.date)
        theoryMarks.setText(saved.maxMarksTheory.toString())
        practicalMarks.setText(saved.maxMarksPractical.toString())
        adapter.notifyDataSetChanged()
    }

    private fun saveTest() {
        val status = Snackbar.make(contentList, "Saving grades...", Snackbar.LENGTH_INDEFINITE)
        status.show()
        TestsDao.add(schoolId, test, OnCompleteListener {
            status.setText("Saving grades... Done!")
            Handler().postDelayed({
                status.dismiss()
                finish()
            }, 1500L)
        })
    }

    private fun setEditEnabled(enabled: Boolean) {
        typeSpinner.isEnabled = enabled
        subtypeSpinner.isEnabled = enabled
        selectDateButton.isEnabled = enabled
        theoryMarks.isEnabled = enabled
        practicalMarks.isEnabled = enabled

        createButton.visibility = if (enabled) View.VISIBLE else View.GONE
        contentList.visibility = if (enabled) View.GONE else View.VISIBLE
        saveButton.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    private fun showError(error: String) {
        Snackbar.make(contentList, error, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.colorWarning))
                .setTextColor(Color.WHITE)
                .show()
    }

    private fun updateTestWith(saved: Test) {
        test.name = saved.name
        test.date = saved.date
        test.classId = saved.classId
        test.subjectId = saved.subjectId
        test.maxMarksTheory = saved.maxMarksTheory
        test.maxMarksPractical = saved.maxMarksPractical

        saved.obtainedScore.forEach { marks ->
            test.updateRecord(marks.studentRollNo!!, marks.theoryMarks, marks.practicalMarks)
        }
        adapter.notifyDataSetChanged()
    }

    private inner class TestScoresAdapter(context: Context, val scores: List<TestScore>)
        : ModelViewAdapter<TestScore>(context, scores, TestScoreView::class) {

        override fun notifyDataSetChanged() {
            scores.sortedBy { it.studentRollNo }
            super.notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            if (currentUser is Student) {
                (v as TestScoreView).setEditable(false)
                if (scores[position].studentRollNo != (currentUser as Student).rollNo) {
                    v.visibility = View.GONE
                }
            }

            return v
        }

    }

}