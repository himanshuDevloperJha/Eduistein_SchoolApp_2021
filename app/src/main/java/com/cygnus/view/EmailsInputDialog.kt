package com.cygnus.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.InputUtils.isNotBlank
import com.cygnus.R
import kotlinx.android.synthetic.main.dialog_emails_input.*

class EmailsInputDialog(context: Context) : Dialog(context) {

    private var onEmailsReceived: ((List<String>) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_emails_input)
        okButton.setOnClickListener { onOk() }
    }

    fun setOnEmailsReceivedListener(onEmailsReceived: ((List<String>) -> Unit)): EmailsInputDialog {
        this.onEmailsReceived = onEmailsReceived
        return this
    }

    private fun Editable?.toEmailsList(): List<String> {
        val emailList = mutableListOf<String>()
        if (this != null) {
            val lines = this.trim().split("\n")
            for (text in lines) {
                if (text.trim().isEmail()) emailList.add(text.trim())
            }
        }

        return emailList
    }

    private fun onOk() {
        if (emailsField.isNotBlank(true)) {
            val emails = emailsField.text.toEmailsList()
            onEmailsReceived?.let { it(emails) }

            dismiss()
        }
    }

}