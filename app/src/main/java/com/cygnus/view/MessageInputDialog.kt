package com.cygnus.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import co.aspirasoft.util.InputUtils.isNotBlank
import com.cygnus.R
import kotlinx.android.synthetic.main.dialog_message_input.*

class MessageInputDialog(context: Context) : Dialog(context) {

    private var onMessageReceived: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_message_input)
        okButton.setOnClickListener { onOk() }
    }

    fun setOnMessageReceivedListener(onEmailsReceived: ((String) -> Unit)): MessageInputDialog {
        this.onMessageReceived = onEmailsReceived
        return this
    }

    private fun onOk() {
        if (messageField.isNotBlank(true)) {
            val message = messageField.text.toString()
            onMessageReceived?.let { it(message) }

            dismiss()
        }
    }

}