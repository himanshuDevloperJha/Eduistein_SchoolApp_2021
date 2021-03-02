package com.cygnus.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.cygnus.R
import com.cygnus.model.QuizModel
import com.cygnus.model.QuizScreen
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.activity_start_quiz.*
import kotlinx.android.synthetic.main.activity_start_quiz.tv_ques

class StartQuizActivity : AppCompatActivity() {
    lateinit var standard: String
    lateinit var studentschoolid: String
    lateinit var studentclassId: String
    lateinit var studentname: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_quiz)

        standard = intent.getStringExtra("user_standard")
        studentschoolid = intent.getStringExtra("studentschoolid")
        studentclassId = intent.getStringExtra("studentclassId")
        studentname = intent.getStringExtra("studentname")

        btn_startquiz.setOnClickListener {


            val reff2 = FirebaseDatabase.getInstance().reference.
                    child("quizstudents")
            reff2.orderByKey().equalTo(studentname).
                    addListenerForSingleValueEvent(object : ValueEventListener {


                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (datas in dataSnapshot.children) {
                            if (datas.key.toString().equals(studentname) &&
                                    datas.child("status").value.toString().equals("false")) {
                            } else {
                                val post = QuizStatusStudent("true")
                                val missionsReference = FirebaseDatabase.getInstance().reference.child("quizstudents").child(studentname)
                                missionsReference.setValue(post)
                            }

                        }
                    }
                    else {
                        val post = QuizStatusStudent("true")
                        val missionsReference = FirebaseDatabase.getInstance().reference.child("quizstudents").child(studentname)
                        missionsReference.setValue(post)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })


            val intent = Intent(applicationContext, QuizActivity::class.java)
            intent.putExtra("user_standard", standard)
            intent.putExtra("studentschoolid", studentschoolid)
            intent.putExtra("studentclassId", studentclassId)
            intent.putExtra("studentname", studentname)
            startActivity(intent)
            finish()
        }
    }
}
