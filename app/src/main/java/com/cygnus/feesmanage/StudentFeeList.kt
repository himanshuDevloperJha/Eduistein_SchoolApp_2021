package com.cygnus.feesmanage

import android.app.Dialog
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cygnus.R
import com.cygnus.adapter.ClasswiseTotalfeeAdapter
import com.cygnus.model.ClassFees
import com.cygnus.model.Classname
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_student_fee_list.*
import java.util.*
import kotlin.collections.ArrayList

class StudentFeeList : AppCompatActivity() {
    lateinit var studentfeeList: RecyclerView
    lateinit var classwiseadapter: ClasswiseTotalfeeAdapter
    var feelist = ArrayList<String>()
    lateinit var reference: DatabaseReference
    lateinit var schoolID: String
    lateinit var sp_loginsave: SharedPreferences
    lateinit var ed_loginsave: SharedPreferences.Editor
    lateinit var ch1: List<String>
    lateinit var fee_std: AppCompatAutoCompleteTextView
    lateinit var fee_classname: AppCompatAutoCompleteTextView
    private var classes: ArrayList<String> = ArrayList()
    var classname: String? = null
    lateinit var pb_classfeelist:ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_fee_list)

        studentfeeList = findViewById(R.id.studentfeeList)

        pb_classfeelist=findViewById(R.id.pb_class)

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()
        schoolID = sp_loginsave.getString("SchoolID", "").toString()

        classes.add("Class Nursery")
        classes.add("Class LKG")
        classes.add("Class UKG")
        classes.add("Class 1st")
        classes.add("Class 2nd")
        classes.add("Class 3rd")
        classes.add("Class 4th")
        classes.add("Class 5th")
        classes.add("Class 6th")
        classes.add("Class 7th")
        classes.add("Class 8th")
        classes.add("Class 9th")
        classes.add("Class 10th")
        classes.add("Class 11th")
        classes.add("Class 12th")

        val clslist: MutableList<String> = mutableListOf()
        val teacherlist: MutableList<String> = mutableListOf()
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child(schoolID)
                .child("classes")
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                clslist.clear()
                for (ds in dataSnapshot.getChildren()) {


                    //display classname
                    classname = ds.child("name").getValue().toString()
                    //  teachername = ds.child("teacherId").getValue().toString()
                    clslist.add(classname.toString())
                    // teacherlist.add(teachername.toString())


                }
                //Log.e("msggg3",""+ clslist.size);   //gives the value for given keyname

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("msg", databaseError.message) //Don't ignore errors!
            }
        }
        uidRef.addListenerForSingleValueEvent(valueEventListener)

        getClasses()



        addButtonFees.setOnClickListener {
            val dialog1 = Dialog(this)
            dialog1.setContentView(R.layout.dialog_addclassfee)
            dialog1.show()

            val addClassFees = dialog1.findViewById(R.id.addFeeClass) as MaterialButton
            fee_std = dialog1.findViewById(R.id.fee_std) as AppCompatAutoCompleteTextView
            fee_classname = dialog1.findViewById(R.id.fee_classname) as AppCompatAutoCompleteTextView

            fee_classname.setAdapter(ArrayAdapter(applicationContext,
                    android.R.layout.select_dialog_item, clslist))
            fee_classname.threshold = 1

            fee_std.setAdapter(ArrayAdapter(applicationContext, android.R.layout.select_dialog_item, classes))
            fee_std.threshold = 1

            addClassFees.setOnClickListener {
                if (fee_classname.text!!.isEmpty() &&
                        fee_std.text!!.isEmpty()) {

                    fee_classname.setError("Enter class name")
                    fee_std.setError("Enter standard")
                } else if (fee_classname.text!!.isEmpty()) {
                    fee_classname.setError("Enter class name")


                } else if (fee_std.text!!.isEmpty()) {
                    fee_std.setError("Enter standard")
                } else {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Loading")
                    progressDialog.setMessage("Loading")
                    progressDialog.show()

                    val post = Classname(fee_classname.text.toString())


                    // val post = Classname(fee_classname.text.toString())

                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child(schoolID).child("SchoolFeeClasses").push()
                    missionsReference.setValue(post)


                    Toast.makeText(applicationContext, "Added Successfully", Toast.LENGTH_SHORT).show()

                    fee_classname.setText("")
                    fee_std.setText("")
                    progressDialog.dismiss()
                    dialog1.dismiss()
                    getClasses()

                }
            }


        }

    }

    private fun getClasses() {

        feelist.clear()
        reference = FirebaseDatabase.getInstance().reference.child(schoolID).child("SchoolFeeClasses")

        reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    val classid = datas.child("classId").value.toString()

                    try {
                        val classFees = ClassFees(datas.child("classId").value.toString(),
                                datas.child("feeType").value.toString(),
                                datas.child("feeCharges").value.toString(),
                                datas.child("recursiveFees").value.toString())
                        feelist.add(classid)


                    } catch (e: Exception) {
                        // Toast.makeText(applicationContext, "" + e.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                val chaptername: LinkedHashSet<String> = LinkedHashSet<String>(feelist)
                ch1 = ArrayList(chaptername)
                studentfeeList.setLayoutManager(LinearLayoutManager(applicationContext,
                        RecyclerView.VERTICAL, false))
                classwiseadapter = ClasswiseTotalfeeAdapter(applicationContext,
                        ch1)
                studentfeeList.setAdapter(classwiseadapter)
                pb_classfeelist.visibility = View.INVISIBLE

            }

            override fun onCancelled(databaseError: DatabaseError) {
                pb_classfeelist.visibility = View.INVISIBLE


                //Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })
    }


}
