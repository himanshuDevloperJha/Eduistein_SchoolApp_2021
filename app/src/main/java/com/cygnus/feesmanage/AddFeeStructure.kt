package com.cygnus.feesmanage

import android.app.Dialog
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cygnus.adapter.StudentFeeListAdapter
import com.cygnus.model.ClassFees
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_fee_structure.*
import com.cygnus.R



class AddFeeStructure : AppCompatActivity(), TotalFees {

    private var recursivelist: ArrayList<String> = ArrayList()
    var teachername: String? = null
    lateinit var classID: String
    lateinit var fee_type: AppCompatEditText
    lateinit var fee_amt: AppCompatEditText
    lateinit var reference1: DatabaseReference

    lateinit var feeannual: AppCompatAutoCompleteTextView

    lateinit var sp_loginsave: SharedPreferences
    lateinit var ed_loginsave: SharedPreferences.Editor
    lateinit var schoolID: String
    lateinit var selectedannualfee: String

    lateinit var studentfeeadapter: StudentFeeListAdapter
    var feelist = ArrayList<ClassFees>()
    var feekeylist = ArrayList<String>()
    lateinit var reference: DatabaseReference
    lateinit var pb_addfee: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_fee_structure)

        sp_loginsave = getSharedPreferences("SAVELOGINDETAILS", MODE_PRIVATE)
        ed_loginsave = sp_loginsave.edit()
        schoolID = sp_loginsave.getString("SchoolID", "").toString()

        classID = intent.getStringExtra("FeeClassID")
        toolbar_addFees.setText(classID + " Fee Structure")
        pb_addfee=findViewById(R.id.pb_addfee)

        //Toast.makeText(applicationContext,"school ID::"+schoolID,Toast.LENGTH_SHORT).show()


        recursivelist.add("One time Fee")
        recursivelist.add("Monthly Fee")
        recursivelist.add("Yearly Fee")
        recursivelist.add("Quarterly Fee")
        recursivelist.add("Half Yearly Fee")

        getClassFees()


        addFeeStructure.setOnClickListener {
            val dialog1 = Dialog(this)
            dialog1.setContentView(R.layout.dialog_addfess)
            dialog1.show()

            val addFees = dialog1.findViewById(R.id.addFees) as MaterialButton
            fee_amt = dialog1.findViewById(R.id.fee_amt) as AppCompatEditText
            fee_type = dialog1.findViewById(R.id.fee_type) as AppCompatEditText

            feeannual = dialog1.findViewById(R.id.feeannual) as AppCompatAutoCompleteTextView



            feeannual.setAdapter(ArrayAdapter(applicationContext,
                    android.R.layout.select_dialog_item, recursivelist))
            feeannual.threshold = 1

            addFees.setOnClickListener {

                if (feeannual.text!!.isEmpty()
                        && fee_type.text!!.isEmpty()
                        && fee_amt.text!!.isEmpty()) {
                    feeannual.setError("Enter recursive nature of fee")
                    fee_type.setError("Enter fee type")
                    fee_amt.setError("Enter fee amount")
                } else if (feeannual.text!!.isEmpty()) {
                    feeannual.setError("Enter recursive nature of fee")
                } else if (fee_type.text!!.isEmpty()) {
                    fee_type.setError("Enter fee type")
                } else if (fee_amt.text!!.isEmpty()) {
                    fee_amt.setError("Enter fee amount")
                } else {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Loading")
                    progressDialog.setMessage("Loading")
                    progressDialog.show()

                    val post = ClassFees(classID, fee_type.text.toString(),
                            fee_amt.text.toString(), selectedannualfee)


                    // val post = Classname(fee_classname.text.toString())

                    val missionsReference =
                            FirebaseDatabase.getInstance().reference.child(schoolID).
                                    child("SchoolFeeList").push()
                    missionsReference.setValue(post)


                    Toast.makeText(applicationContext, "Added Successfully"+selectedannualfee, Toast.LENGTH_SHORT).show()
                   // Toast.makeText(applicationContext, "Added Successfully", Toast.LENGTH_SHORT).show()

                    fee_type.setText("")
                    fee_amt.setText("")
                    progressDialog.dismiss()
                    getClassFees()
                    dialog1.dismiss()

                }


            }

            feeannual.setOnItemClickListener { adapterView, view, i, l ->
                if(feeannual.adapter.getItem(i).equals("Monthly Fee")){
                    selectedannualfee="Due Fee"
                   // Toast.makeText(applicationContext, "1:" + selectedannualfee, Toast.LENGTH_SHORT).show()

                }
                else{
                    selectedannualfee= feeannual.adapter.getItem(i).toString()
                     //Toast.makeText(applicationContext, "2:" + selectedannualfee, Toast.LENGTH_SHORT).show()

                }


            }
        }


    }

    private fun getClassFees() {
        feelist.clear()
        feekeylist.clear()
        reference = FirebaseDatabase.getInstance().reference.child(schoolID).child("SchoolFeeList")

        reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {

                    if (classID.equals(datas.child("classId").value.toString(), ignoreCase = true)) {
                        val classid = datas.child("classId").value.toString()

                        try {
                            val classFees = ClassFees(datas.child("classId").value.toString(),
                                    datas.child("feeType").value.toString(),
                                    datas.child("feeCharges").value.toString(),
                                    datas.child("recursiveFees").value.toString())
                            feelist.add(classFees)
                            feekeylist.add(datas.key.toString())

                        } catch (e: Exception) {
                            // Toast.makeText(applicationContext, "" + e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                classFeeList.setLayoutManager(LinearLayoutManager(applicationContext,
                        RecyclerView.VERTICAL, false))
                studentfeeadapter = StudentFeeListAdapter(applicationContext,
                        feelist, feekeylist, this@AddFeeStructure)
                classFeeList.setAdapter(studentfeeadapter)
                pb_addfee.visibility = View.INVISIBLE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                pb_addfee.visibility = View.INVISIBLE


                //Toast.makeText(applicationContext, ""+databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        })
    }

    override fun studentTotalFees(position: Int, fees: Int) {
        tv_classtotal.setText("TOTAL : \u20B9 " + fees)

    }

    override fun editfees(position: Int, fees: String?, feetype: String?, feekey: String?) {
        //Toast.makeText(applicationContext, "FeeKey::"+feekey, Toast.LENGTH_SHORT).show();

        val dialog1 = Dialog(this)
        dialog1.setContentView(R.layout.dialog_addfess)
        dialog1.show()

        val addFees = dialog1.findViewById(R.id.addFees) as MaterialButton
        fee_amt = dialog1.findViewById(R.id.fee_amt) as AppCompatEditText
        fee_type = dialog1.findViewById(R.id.fee_type) as AppCompatEditText

        feeannual = dialog1.findViewById(R.id.feeannual) as AppCompatAutoCompleteTextView



        feeannual.setAdapter(ArrayAdapter(applicationContext,
                android.R.layout.select_dialog_item, recursivelist))


        reference1 = FirebaseDatabase.getInstance().reference.child(schoolID).child("SchoolFeeList")
        reference1!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (datas in dataSnapshot.getChildren()) {
                    if (feekey.equals(datas.key.toString())) {
                        val fee_ty = datas.child("feeType").value.toString()
                        //  Toast.makeText(applicationContext, "FeeType::"+fee_type, Toast.LENGTH_SHORT).show();

                        val fees = datas.child("feeCharges").value.toString()
                        val recursive_fee = datas.child("recursiveFees").value.toString()

                        if(recursive_fee.equals("Due Fee")){
                            selectedannualfee="Monthly Fee"
                        }
                        else{
                            selectedannualfee=recursive_fee

                        }

                        fee_type.setText(fee_ty)
                        fee_amt.setText(fees)
                        feeannual.setText(selectedannualfee)
                    }

                }


                //   Toast.makeText(applicationContext, "Account No:"+accountno, Toast.LENGTH_SHORT).show();
                // Toast.makeText(applicationContext, "UPI ID:"+upiid, Toast.LENGTH_SHORT).show();

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        feeannual.setOnItemClickListener { adapterView, view, i, l ->
            if(feeannual.adapter.getItem(i).equals("Monthly Fee")){
                selectedannualfee="Due Fee"
                // Toast.makeText(applicationContext, "1:" + selectedannualfee, Toast.LENGTH_SHORT).show()

            }
            else{
                selectedannualfee= feeannual.adapter.getItem(i).toString()
                //Toast.makeText(applicationContext, "2:" + selectedannualfee, Toast.LENGTH_SHORT).show()

            }


        }

        addFees.setOnClickListener {


            if (feeannual.text!!.isEmpty()
                    && fee_type.text!!.isEmpty()
                    && fee_amt.text!!.isEmpty()) {
                feeannual.setError("Enter recursive nature of fee")
                fee_type.setError("Enter fee type")
                fee_amt.setError("Enter fee amount")
            } else if (feeannual.text!!.isEmpty()) {
                feeannual.setError("Enter recursive nature of fee")
            } else if (fee_type.text!!.isEmpty()) {
                fee_type.setError("Enter fee type")
            } else if (fee_amt.text!!.isEmpty()) {
                fee_amt.setError("Enter fee amount")
            } else {
                /* val progressDialog = ProgressDialog(this)
                 progressDialog.setTitle("Loading")
                 progressDialog.setMessage("Loading")
                 progressDialog.show()

                 val post = ClassFees(classID, fee_type.text.toString(),
                         fee_amt.text.toString(), feeannual.text.toString())


                 // val post = Classname(fee_classname.text.toString())

                 val missionsReference =
                         FirebaseDatabase.getInstance().reference.child(schoolID).
                                 child("SchoolFeeList").push()
                 missionsReference.setValue(post)


                 Toast.makeText(applicationContext, "Added Successfully", Toast.LENGTH_SHORT).show()

                 fee_type.setText("")
                 fee_amt.setText("")
                 progressDialog.dismiss()
                 getClassFees()
                 dialog1.dismiss()*/

                val ref = FirebaseDatabase.getInstance().reference.child(schoolID).
                        child("SchoolFeeList")

                ref.orderByKey().equalTo(feekey).addListenerForSingleValueEvent(object : ValueEventListener {


                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (datas in dataSnapshot.children) {
                                val key = datas.key
                                //  val name = datas.child("name").value.toString()
                                // val priorities = datas.child("priority").value.toString()
                                //  ref.child(key!!).child("done").setValue(isdone)
                                ref.child(feekey.toString()).child("feeType").setValue(fee_type.text.toString())
                                ref.child(feekey.toString()).child("feeCharges").setValue(fee_amt.text.toString())
                                ref.child(feekey.toString()).child("recursiveFees").setValue(selectedannualfee)

                                Toast.makeText(applicationContext, "Updated Successfully"+selectedannualfee, Toast.LENGTH_SHORT).show()

                                fee_type.setText("")
                                fee_amt.setText("")
                                getClassFees()
                                dialog1.dismiss()
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })

            }


        }
    }


}
