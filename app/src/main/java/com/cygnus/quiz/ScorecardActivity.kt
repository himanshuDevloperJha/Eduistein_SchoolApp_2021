package com.cygnus.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cygnus.R
import com.cygnus.model.PointsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.activity_scorecard.*

class ScorecardActivity : AppCompatActivity() {
      var coinscomputer =0
    lateinit var no_correctans: String
    lateinit var no_wrongans : String
     var coinsuser =0
     var coinsseconduser =0
      var usertotalpoints =0
    lateinit var studentname: String
    lateinit var secondname: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scorecard)

        no_correctans = intent.getStringExtra("correctans")
        no_wrongans = intent.getStringExtra("wrongans")
        studentname = intent.getStringExtra("username1")
        secondname = intent.getStringExtra("username2")
        coinsuser = intent.getStringExtra("coinsuser").toInt()
        coinsseconduser = intent.getStringExtra("coinsseconduser").toInt()
        coinscomputer = intent.getStringExtra("coinscomputer").toInt()
        usertotalpoints = intent.getStringExtra("usertotalpts").toInt()

        if (secondname.contains("Aditya")) {

            if(coinsuser > coinscomputer){
                tv_wonlost.setText("Congratulations "+studentname+"\nYou Won !")
                usertotalpoints=usertotalpoints+10
                coinsuser=coinsuser+10
                tv_pts.setText("You Scored : "+coinsuser.toString())
            }
            else if(coinsuser == coinscomputer){
                tv_wonlost.setText("Quiz Tie !\n"+secondname+" scored: "+coinscomputer.toString())
                tv_pts.setText("You Scored : "+coinsuser.toString())
            }
            else if(coinsuser < coinscomputer) {
                tv_wonlost.setText("You Lost !\nWinner is "+secondname+" and scored: "+coinscomputer.toString())
                tv_pts.setText("You Scored : "+coinsuser.toString())

            }


        }


        if (!secondname.contains("Aditya")) {
            if(coinsuser > coinsseconduser){
                tv_wonlost.setText("Congratulations "+studentname+"\nYou Won !")
                usertotalpoints=usertotalpoints+10
                coinsuser=coinsuser+10
                tv_pts.setText("You Scored : "+coinsuser.toString())
            }
            else if(coinsuser == coinsseconduser){
                tv_wonlost.setText("Quiz Tie !\n"+secondname+" scored: "+coinsseconduser.toString())
                tv_pts.setText("You Scored : "+coinsuser.toString())
            }
            else {
                tv_wonlost.setText("You Lost !\nWinner is "+secondname+" and scored: "+coinsseconduser.toString())
                tv_pts.setText("You Scored : "+coinsuser.toString())

            }




        }

        try{
            tv_correct.setText("Correct Answers: "+no_correctans.toString())
            tv_wrong.setText("Wrong Answers: "+no_wrongans.toString())
            tv_totalptsss.setText(usertotalpoints.toString()+" Points")
        }
        catch (e:Exception){}

        val rootRef = FirebaseDatabase.getInstance().reference.child("coins")
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val ref = FirebaseDatabase.getInstance().reference
                            .child("coins")
                    ref.orderByChild("name").equalTo(studentname).
                            addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (datas in dataSnapshot.children) {
                                    val key = datas.key
                                    val points = datas.child("points").value.toString()
                                    ref.child(key!!).child("points").setValue(coinsuser)
                                    ref.child(key!!).child("usertotalpoints").setValue(usertotalpoints)


                                }
                            }

                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }
}
