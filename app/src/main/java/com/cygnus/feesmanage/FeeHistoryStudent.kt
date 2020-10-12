package com.cygnus.feesmanage

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cygnus.R
import com.cygnus.adapter.FeeHistoryAdapter
import com.cygnus.adapter.StudentFeeDetailAdapter
import com.cygnus.model.ClassFees
import com.cygnus.model.FeeHistory
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_fee_structure.*
import kotlinx.android.synthetic.main.activity_fee_history_student.*
import kotlinx.android.synthetic.main.activity_payfeess.*
import java.util.ArrayList

class FeeHistoryStudent : AppCompatActivity() {
    lateinit var sp_loginsave: SharedPreferences
    lateinit var ed_loginsave: SharedPreferences.Editor
    lateinit var schoolID: String
    lateinit var pb_feehistory: ProgressBar
    lateinit var studentclassId: String
    lateinit var studentname: String
    lateinit var studentrollNoo: String
    lateinit var reference2: DatabaseReference
    var feelist = ArrayList<FeeHistory>()
    lateinit var feeHistoryAdapter: FeeHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fee_history_student)

        schoolID = intent.getStringExtra("studentschoolid")
        studentclassId = intent.getStringExtra("studentclassId")
        studentname = intent.getStringExtra("studentname")
        studentrollNoo = intent.getStringExtra("studentrollNoo")

        //    Toast.makeText(this, ""+schoolID, Toast.LENGTH_SHORT).show()
       // Toast.makeText(this, ""+studentname, Toast.LENGTH_SHORT).show()


        pb_feehistory=findViewById(R.id.pb_feehistory)


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
                classFeeHistory.setLayoutManager(LinearLayoutManager(applicationContext,
                        RecyclerView.VERTICAL, false))
                feeHistoryAdapter = FeeHistoryAdapter(applicationContext,
                        feelist)
                classFeeHistory.setAdapter(feeHistoryAdapter)
                pb_feehistory.visibility= View.GONE

                if(feelist.size==0){
                    ll_nohistory.visibility=View.VISIBLE
                    classFeeHistory.visibility=View.GONE
                    pb_feehistory.visibility= View.GONE

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                //pb_feehistory.visibility= View.GONE

                Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })
    }
}
