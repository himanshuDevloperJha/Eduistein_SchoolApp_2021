package com.cygnus.feesmanage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cygnus.R
import com.cygnus.adapter.FeeHistoryAdapter
import com.cygnus.adapter.SchoolFeeDetailAdapter
import com.cygnus.model.FeeHistory
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_fee_history_student.*
import kotlinx.android.synthetic.main.activity_school_fee_detail.*
import java.util.ArrayList

class SchoolFeeDetail : AppCompatActivity() {
    lateinit var studentclassId: String
    lateinit var studentname: String
    lateinit var schoolID: String
    lateinit var studentrollNoo: String
    lateinit var reference2: DatabaseReference
    var feelist = ArrayList<FeeHistory>()
    lateinit var schoolfeeAdapter: SchoolFeeDetailAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_fee_detail)

        schoolID = intent.getStringExtra("studentschoolid")
        studentclassId = intent.getStringExtra("studentclassId")
        studentname = intent.getStringExtra("studentname")
        studentrollNoo = intent.getStringExtra("studentrollNoo")

        tv_name.setText("Name: "+studentname)
        tv_classId.setText("Class Name: "+studentclassId)
        tv_rollNo.setText("Roll No: "+studentrollNoo)

        getFeesHistoryList()
    }

    private fun getFeesHistoryList() {
        feelist.clear()
        reference2 = FirebaseDatabase.getInstance().reference.child(schoolID).
                child("FeeHistory")
        reference2!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    if (studentclassId.equals(datas.child("classId").value.toString())
                            && studentname.equals(datas.child("studentName").value.toString())
                            && studentrollNoo.equals(datas.child("studentRollno").value.toString())) {
                        val classid = datas.child("classId").value.toString()

                        try {
                            val feeHistory = FeeHistory(datas.child("classId").value.toString(),
                                    datas.child("studentName").value.toString(),
                                    datas.child("feeMonth").value.toString(),
                                    datas.child("feeDate").value.toString(),
                                    datas.child("feeAmount").value.toString(),
                                    datas.child("status").value.toString(),
                                    datas.child("studentRollno").value.toString())
                            feelist.add(feeHistory)

                        } catch (e: Exception) {
                            Toast.makeText(applicationContext, "" + e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                rv_studentfeeList.setLayoutManager(LinearLayoutManager(applicationContext,
                        RecyclerView.VERTICAL, false))
                schoolfeeAdapter = SchoolFeeDetailAdapter(applicationContext,
                        feelist)
                rv_studentfeeList.setAdapter(schoolfeeAdapter)

                if(feelist.size==0){
//                    ll_nohistory.visibility= View.VISIBLE
                    rv_studentfeeList.visibility= View.INVISIBLE
                    ll_nofeehistory.visibility= View.VISIBLE

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                //pb_feehistory.visibility= View.GONE

                Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })
    }
}
