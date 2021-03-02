package com.cygnus.feesmanage

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cygnus.adapter.StudentFeeDetailAdapter
import com.cygnus.model.ClassFees
import com.cygnus.model.FeeHistory
import com.google.firebase.database.*
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener
import com.shreyaspatil.EasyUpiPayment.model.PaymentApp
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails
import kotlinx.android.synthetic.main.activity_payfeess.*
import java.text.SimpleDateFormat
import com.cygnus.R

import java.util.*


class PayFees : AppCompatActivity(), TotalFees, PaymentStatusListener {
    lateinit var reference5: DatabaseReference
    lateinit var reference1: DatabaseReference
    lateinit var reference2: DatabaseReference
    var feelist = ArrayList<ClassFees>()
    lateinit var schoolID: String
    var fees: Int = 0
    lateinit var easyUpiPayment: EasyUpiPayment
    lateinit var fees1: String
    //  lateinit var accountno: String
    lateinit var upiid: String
    lateinit var studentclassId: String
    lateinit var studentname: String
    lateinit var studentrollNoo: String
    lateinit var currentDate: String
    lateinit var month_name: String
    lateinit var studentfeeadapter: StudentFeeDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payfeess)
        schoolID = intent.getStringExtra("studentschoolid")
        studentclassId = intent.getStringExtra("studentclassId")
        studentname = intent.getStringExtra("studentname")
        studentrollNoo = intent.getStringExtra("studentrollNoo")


        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMMM")
        month_name = month_date.format(cal.time)
        tv_month.setText(month_name)

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        currentDate = sdf.format(Date())






        reference1 = FirebaseDatabase.getInstance().reference.child(schoolID).child("AccountDetails")
        reference1!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                upiid = dataSnapshot.child("upiid").value.toString()
                // Toast.makeText(applicationContext, "UPI ID:"+upiid, Toast.LENGTH_SHORT).show();
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        paynow.setOnClickListener {
            // if (!accountno.equals("")) {
            //  easyUpiPayment.setDefaultPaymentApp(PaymentApp.PAYTM);

            /* }
             else if(!upiid.equals("")){
                 easyUpiPayment.setDefaultPaymentApp(PaymentApp.BHIM_UPI);

             }*/
            try {
                if(!upiid.equals("")){
                    easyUpiPayment.setDefaultPaymentApp(PaymentApp.PAYTM)
                    easyUpiPayment.startPayment()
                }
                else{
                    Toast.makeText(this, "Payment method not set", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
            }
        }

        fee_history.setOnClickListener {
            val intent = Intent(this, FeeHistoryStudent::class.java)
            intent.putExtra("studentschoolid", schoolID)
            intent.putExtra("studentclassId", studentclassId)
            intent.putExtra("studentname", studentname)
            intent.putExtra("studentrollNoo", studentrollNoo)
            startActivity(intent)
        }

        reference2 = FirebaseDatabase.getInstance().reference.child(schoolID).
                child("FeeHistory")
        reference2!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    if (studentclassId.equals(datas.child("classId").value.toString())
                            && studentname.equals(datas.child("studentName").value.toString())
                            && month_name.equals(datas.child("feeMonth").value.toString(),
                                    ignoreCase = true)
                            && studentrollNoo.equals(datas.child("studentRollno").value.toString())) {
                       iv_paymentsuccess.visibility=View.VISIBLE
                        rv_feeDetail.visibility=View.INVISIBLE
                        tv_month.visibility=View.INVISIBLE
                        ll_totalfee.visibility=View.INVISIBLE
                        paynow.visibility=View.INVISIBLE
                        fee_history.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))

                    }
                    else{
                        getFeesList()
                        rv_feeDetail.visibility=View.VISIBLE
                        tv_month.visibility=View.VISIBLE
                        ll_totalfee.visibility=View.VISIBLE
                        paynow.visibility=View.VISIBLE

                        iv_paymentsuccess.visibility=View.INVISIBLE

                    }

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                //pb_feehistory.visibility= View.GONE
                Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })



    }

    private fun getFeesList() {
        feelist.clear()
        reference5 = FirebaseDatabase.getInstance().reference.child(schoolID).
                child("SchoolFeeList")
        reference5!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    if (studentclassId.equals(datas.child("classId").value.toString())) {
                        val classid = datas.child("classId").value.toString()

                        try {
                            val classFees = ClassFees(datas.child("classId").value.toString(),
                                    datas.child("feeType").value.toString(),
                                    datas.child("feeCharges").value.toString(),
                                    datas.child("recursiveFees").value.toString())
                            feelist.add(classFees)


                        } catch (e: Exception) {
                            pb_payfees.visibility=View.GONE

                            // Toast.makeText(applicationContext, "" + e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                rv_feeDetail.setLayoutManager(LinearLayoutManager(applicationContext,
                        RecyclerView.VERTICAL, false))
                studentfeeadapter = StudentFeeDetailAdapter(applicationContext,
                        feelist, this@PayFees)
                rv_feeDetail.setAdapter(studentfeeadapter)
                pb_payfees.visibility=View.GONE

                Handler().postDelayed({
                    //Toast.makeText(applicationContext, "Delay", Toast.LENGTH_SHORT).show();

                    getFeesList()
                }, 2000)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                pb_payfees.visibility=View.GONE

                //Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })
    }


    override fun studentTotalFees(position: Int, fees: Int) {

        this.fees = fees
        fees1 = fees.toString() + ".00"
        //Toast.makeText(this, "Fees1::"+this.fees, Toast.LENGTH_SHORT).show()
        // Toast.makeText(this, "Fees2::"+fees1, Toast.LENGTH_SHORT).show()

        tv_total.setText("TOTAL : \u20B9 " + fees)

        val transactionId = "TID" + System.currentTimeMillis()
        // fieldTransactionId.setText(transactionId)
        //  fieldTransactionRefId.setText(transactionId)
        try {
            easyUpiPayment = EasyUpiPayment.Builder()
                    .with(this)
                    .setPayeeVpa(upiid)
                    .setPayeeName("xyz")
                    .setTransactionId(transactionId)
                    .setTransactionRefId(transactionId)
                    .setDescription("Fee Payment")
                    .setAmount(fees1)
                    .build()

            easyUpiPayment.setPaymentStatusListener(this)
        } catch (e: Exception) {
           // Toast.makeText(applicationContext, "Exception::" + e.toString(), Toast.LENGTH_SHORT).show()
        }

        // 20190603022401
//        0120192019060302240
    }

    override fun editfees(position: Int, fees: String?, feetype: String?, feekey: String?) {
    }

    override fun onTransactionSubmitted() {
        toast("Transaction Pending|Submitted")

    }

    override fun onTransactionCancelled() {
        toast("Transaction Cancelled")

    }

    override fun onTransactionSuccess() {
        toast("Paid Successfully")
        val post = FeeHistory(studentclassId, studentname,
                month_name, currentDate, fees.toString(), "paid",studentrollNoo)

        val missionsReference =
                FirebaseDatabase.getInstance().reference.child(schoolID).
                        child("FeeHistory").push()

        missionsReference.setValue(post)

        reference2 = FirebaseDatabase.getInstance().reference.child(schoolID).
                child("FeeHistory")
        reference2!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    if (studentclassId.equals(datas.child("classId").value.toString())
                            && studentname.equals(datas.child("studentName").value.toString())
                            && month_name.equals(datas.child("feeMonth").value.toString(),
                                    ignoreCase = true)
                            && studentrollNoo.equals(datas.child("studentRollno").value.toString())) {
                        iv_paymentsuccess.visibility=View.VISIBLE
                        rv_feeDetail.visibility=View.INVISIBLE
                        tv_month.visibility=View.INVISIBLE
                        ll_totalfee.visibility=View.INVISIBLE
                        paynow.visibility=View.INVISIBLE
                        fee_history.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))

                    }
                    else{
                        getFeesList()
                        rv_feeDetail.visibility=View.VISIBLE
                        tv_month.visibility=View.VISIBLE
                        ll_totalfee.visibility=View.VISIBLE
                        paynow.visibility=View.VISIBLE

                        iv_paymentsuccess.visibility=View.INVISIBLE

                    }

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                //pb_feehistory.visibility= View.GONE
                Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })

    }

    override fun onAppNotFound() {
        toast("Paytm app not found\nPlease install to make payment.")

    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails?) {
        Log.d("TransactionDetails", transactionDetails.toString())
       // toast("Transaction Details: "+transactionDetails.toString())

       /* when (transactionDetails!!.status) {
            TransactionStatus.SUCCESS -> onTransactionSuccess()
            TransactionStatus.FAILURE -> onTransactionFailed()
            TransactionStatus.SUBMITTED -> onTransactionSubmitted()
        }*/

    }

    override fun onTransactionFailed() {
      toast("Transaction Failed")

    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}