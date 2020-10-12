package com.cygnus.feesmanage

import android.app.ProgressDialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.cygnus.R
import com.cygnus.model.AccountDetails
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*

class AccounDetails : AppCompatActivity() {
    lateinit var savebankDetail: MaterialButton
    lateinit var accountno: AppCompatEditText
    lateinit var upiid: AppCompatEditText
    lateinit var holdername: AppCompatEditText
    lateinit var ifsccode: AppCompatEditText
    lateinit var bankname: AppCompatEditText
    lateinit var transactionid: AppCompatEditText
    lateinit var transactionrefid: AppCompatEditText
    lateinit var schoolID: String
    lateinit var sp_loginsave: SharedPreferences
    lateinit var ed_loginsave: SharedPreferences.Editor
    lateinit var reference1: DatabaseReference
    lateinit var accno: String
    lateinit var upi: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accoun_details)

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()
        schoolID = sp_loginsave.getString("SchoolID", "").toString()

        bankname = findViewById(R.id.bankname)
        savebankDetail = findViewById(R.id.savebankDetail)
        accountno = findViewById(R.id.accountno)
        upiid = findViewById(R.id.upiid)
        holdername = findViewById(R.id.holdername)
        ifsccode = findViewById(R.id.ifsccode)
        transactionid = findViewById(R.id.transactionid)
        transactionrefid = findViewById(R.id.transactionrefid)


        reference1 = FirebaseDatabase.getInstance().reference.child(schoolID).
                child("AccountDetails")
        reference1!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //accno = dataSnapshot.child("accountno").value.toString()
                upi = dataSnapshot.child("upiid").value.toString()

              /*  if (!accno.equals("")) {
                    val holname = dataSnapshot.child("holdername").value.toString()
                    val ifsc = dataSnapshot.child("ifsc").value.toString()
                    val bank = dataSnapshot.child("bankname").value.toString()
                    val transactionId = dataSnapshot.child("transactionId").value.toString()
                    val transactionRefId = dataSnapshot.child("transactionRefId").value.toString()

                    if (accno.equals("null")) {
                        accountno.setText("")
                        holdername.setText("")
                        ifsccode.setText("")
                        bankname.setText("")
                        transactionid.setText("")
                        transactionrefid.setText("")
                    } else {
                        accountno.setText(accno)
                        holdername.setText(holname)
                        ifsccode.setText(ifsc)
                        bankname.setText(bank)
                        transactionid.setText(transactionId)
                        transactionrefid.setText(transactionRefId)
                    }

                } */
                if (!upi.equals("")) {
                    if (upi.equals("null")) {
                        upiid.setText("")

                    }
                    else{
                        upiid.setText(upi)

                    }

                }


                //   Toast.makeText(applicationContext, "Account No:"+accountno, Toast.LENGTH_SHORT).show();

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, "UPI ID:" + databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        })

        savebankDetail.setOnClickListener {

            /*if (!accountno.text!!.isEmpty()) {
                if (accountno.text!!.isEmpty() &&
                        holdername.text!!.isEmpty() && ifsccode.text!!.isEmpty()
                        && bankname.text!!.isEmpty() && transactionid.text!!.isEmpty()
                        && transactionrefid.text!!.isEmpty()
                ) {

                    accountno.setError("Enter account number")
                    holdername.setError("Enter holder name")
                    ifsccode.setError("Enter ifsc code")
                    bankname.setError("Enter bank name")
                    transactionid.setError("Enter transaction Id")
                    transactionrefid.setError("Enter transaction ref Id")

                } else if (accountno.text!!.isEmpty()) {
                    accountno.setError("Enter account number")


                } else if (holdername.text!!.isEmpty()) {
                    holdername.setError("Enter holder name")

                } else if (ifsccode.text!!.isEmpty()) {
                    ifsccode.setError("Enter ifsc code")

                } else if (bankname.text!!.isEmpty()) {
                    bankname.setError("Enter bank name")
                } else if (transactionid.text!!.isEmpty()) {
                    transactionid.setError("Enter transaction Id")
                } else if (transactionrefid.text!!.isEmpty()) {
                    transactionrefid.setError("Enter transaction ref Id")
                } else {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Loading")
                    progressDialog.setMessage("Loading")
                    progressDialog.show()
                    val post = AccountDetails(accountno.text.toString(), holdername.text.toString(),
                            ifsccode.text.toString(), bankname.text.toString(),
                            transactionid.text.toString(), transactionrefid.text.toString(), "");

                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child(schoolID).child("AccountDetails")
                    missionsReference.setValue(post)
                    Toast.makeText(applicationContext, "Added Successfully", Toast.LENGTH_SHORT).show()
                    accountno.setText("")
                    holdername.setText("")
                    ifsccode.setText("")
                    bankname.setText("")
                    transactionid.setText("")
                    transactionrefid.setText("")
                    progressDialog.dismiss()

                }
            } */
           if (!upiid.text!!.isEmpty()) {

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Loading")
                progressDialog.setMessage("Loading")
                progressDialog.show()
                val post = AccountDetails( upiid.text.toString())

                val missionsReference =
                        FirebaseDatabase.getInstance().reference.child(schoolID).child("AccountDetails")

                missionsReference.setValue(post)
                Toast.makeText(applicationContext, "Added Successfully", Toast.LENGTH_SHORT).show()
                upiid.setText("")
                progressDialog.dismiss()
            } else {
                Toast.makeText(applicationContext, "Enter mandatory fields", Toast.LENGTH_SHORT).show()

            }


        }
    }
}
