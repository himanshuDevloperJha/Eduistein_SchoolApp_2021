package com.cygnus.feesmanage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cygnus.R
import com.cygnus.adapter.PeopleAdapter
import com.cygnus.model.ChapterSpinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_checkfeestatus.*
import kotlinx.android.synthetic.main.activity_school.*

class CheckFeeStatus : AppCompatActivity() {

    lateinit var sp_loginsave: SharedPreferences
    lateinit var ed_loginsave: SharedPreferences.Editor
    lateinit var schoolID: String
    lateinit var peopleAdapter: PeopleAdapter
    var chsplist: ArrayList<ChapterSpinner> = ArrayList()

    var classname: String? = null
    var teachername: String? = null
    var rollNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkfeestatus)

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()
        schoolID = sp_loginsave.getString("SchoolID", "").toString()

        val clslist: MutableList<String> = mutableListOf()
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child(schoolID)
                .child("classes")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {

                    //display classname
                    classname = ds.child("name").getValue().toString()
                    //teachername = ds.child("teacherId").getValue().toString()
                    clslist.add(classname.toString())


                }
                check_classnamee.setAdapter(ArrayAdapter(applicationContext,
                        android.R.layout.select_dialog_item, clslist))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, "1: " + databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        }
        uidRef.addListenerForSingleValueEvent(valueEventListener)


        val studentlist: MutableList<String> = mutableListOf()
        val rootRef1 = FirebaseDatabase.getInstance().reference
        val uidRef1 = rootRef1.child(schoolID)
                .child("users")

        val valueEventListener1 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {
                    if (ds.child("type").getValue().toString().equals("Student")) {
                        val studentname = ds.child("name").getValue().toString()
                        val roll = ds.child("rollNo").getValue().toString()
                       // studentlist.add(studentname)
//                        check_stname.setAdapter(ArrayAdapter(applicationContext,
//                                android.R.layout.select_dialog_item, studentlist))

                         var cs = ChapterSpinner(studentname, roll)
                         chsplist.add(cs)
                         peopleAdapter = PeopleAdapter(applicationContext,
                                R.layout.custom_teacher_youtube_videos, R.id.tv_chh, chsplist)
                        check_stname.setAdapter(peopleAdapter)

                        check_stname.setOnItemClickListener { adapterView, view, i, l ->
                            // selectedchapter = peopleAdapter.getItem(i)!!.no + "-" + peopleAdapter.getItem(i)!!.name
                            rollNo = peopleAdapter.getItem(i)!!.studentRollno
                            check_stname.setText(peopleAdapter.getItem(i)!!.studentname)
                          //  Toast.makeText(applicationContext, "" + rollNo, Toast.LENGTH_SHORT).show();


                        }
                    }
                    //display classname

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, "2: " + databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        }
        uidRef1.addListenerForSingleValueEvent(valueEventListener1)



       /* check_stname.setOnItemClickListener { adapterView, view, i, l ->
            val t_name = check_stname.adapter.getItem(i).toString()
            Log.e("msg", "UNITSSS" + t_name)

            val uidRef2 = rootRef1.child(schoolID)
                    .child("users")

            val valueEventListener2 = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.getChildren()) {

                        if (ds.child("type").getValue().toString().equals("Student")) {
                            val c = ds.child("name").getValue().toString()
                            if (t_name.equals(c)) {
                                rollNo = ds.child("rollNo").getValue().toString()
                                Toast.makeText(applicationContext, "" + rollNo, Toast.LENGTH_SHORT).show();

                            }
                        }


                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("msg", databaseError.message) //Don't ignore errors!
                }
            }
            uidRef2.addListenerForSingleValueEvent(valueEventListener2)


        }*/


        checkFeeStatus.setOnClickListener {
            if (check_classnamee.text!!.isEmpty()
                    && check_stname.text!!.isEmpty()) {
                check_classnamee.setError("Enter class name")
                check_stname.setError("Enter student name")
            } else if (check_classnamee.text!!.isEmpty()) {
                check_classnamee.setError("Enter class name")
            } else if (check_stname.text!!.isEmpty()) {
                check_stname.setError("Enter student name")
            } else {
                val intent = Intent(this, SchoolFeeDetail::class.java)
                intent.putExtra("studentschoolid", schoolID)
                intent.putExtra("studentclassId", check_classnamee.text.toString())
                intent.putExtra("studentname", check_stname.text.toString())
                intent.putExtra("studentrollNoo", rollNo)
                startActivity(intent)
                check_stname.setText("")
                check_classnamee.setText("")
            }
        }
    }
}