package com.cygnus.feesmanage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cygnus.CygnusApp
import com.cygnus.R
import com.cygnus.StudentFileUpload
import kotlinx.android.synthetic.main.activity_feesmanagment_list.*

class FeesmanagmentList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feesmanagment_list)

        fee_str.setOnClickListener {
            intent = Intent(this, StudentFeeList::class.java)
            startActivity(intent)
        }
        check_fee.setOnClickListener {
            intent = Intent(this, CheckFeeStatus::class.java)
            startActivity(intent)
        }
        account_details.setOnClickListener {
            intent = Intent(this, AccounDetails::class.java)
            startActivity(intent)
        }
        fee_str1.setOnClickListener {
            intent = Intent(this, StudentFeeList::class.java)
            startActivity(intent)
        }
        check_fee1.setOnClickListener {
            intent = Intent(this, CheckFeeStatus::class.java)
            startActivity(intent)
        }
        account_details1.setOnClickListener {
            intent = Intent(this, AccounDetails::class.java)
            startActivity(intent)
        }
    }
}
