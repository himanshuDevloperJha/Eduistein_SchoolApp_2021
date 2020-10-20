package com.cygnus.view

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.getIntent
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.showError
import com.cygnus.CygnusApp
import com.cygnus.R
import com.cygnus.dao.UsersDao
import com.cygnus.model.Student
import com.cygnus.tasks.InvitationTask
import com.cygnus.utils.DynamicLinksUtils.ACTION_REGISTRATION
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.android.play.core.splitinstall.e
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import kotlinx.android.synthetic.main.dialog_add_student.*
import java.net.URLEncoder

class AddStudentDialog : BottomSheetDialogFragment() {
    private  val domain = "https://cygnus.page.link"
    private  val androidPackageName = "com.cygnus"
    private  val iOSBundleId = "com.cygnus"
    private var model: Student? = null
    private val isReadOnly: Boolean get() = model != null

    private lateinit var classId: String
    private lateinit var schoolId: String
    private lateinit var teacherId: String

    private lateinit var rollNumberField: TextInputEditText
    private lateinit var studentEmailField: TextInputEditText
    private lateinit var txtt1: TextInputLayout
    private lateinit var txtt3: TextInputLayout
    private lateinit var txtt2: MaterialTextView
    private lateinit var studentMobileField: TextInputEditText
    private lateinit var okButton: Button
    private lateinit var editButton: Button
lateinit var url:String
    var onDismissListener: ((dialog: DialogInterface) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_add_student, container, false)

        try {
            val args = requireArguments()
            classId = args.getString(ARG_CLASS_ID)!!
            schoolId = args.getString(ARG_SCHOOL_ID)!!
            teacherId = args.getString(ARG_TEACHER_ID)!!
            model = args.getSerializable(ARG_SAVED_INSTANCE) as Student?
        } catch (ex: Exception) {
            Toast.makeText(v.context, ex.message, Toast.LENGTH_LONG).show()
            dismiss()
            return null
        }

        rollNumberField = v.findViewById(R.id.rollNoField)
        studentEmailField = v.findViewById(R.id.emailField)
        studentMobileField = v.findViewById(R.id.mobileField)
        txtt1 = v.findViewById(R.id.txtt1)
        txtt2 = v.findViewById(R.id.txtt2)
        txtt3= v.findViewById(R.id.txtt3)

        okButton = v.findViewById(R.id.okButton)
        editButton = v.findViewById(R.id.editButton)



        okButton.setOnClickListener {
            onOk()
        }
        editButton.setOnClickListener { dismiss() }

        setEditModeEnabled(!isReadOnly)
        return v
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.let { it(dialog) }
    }


    private fun setEditModeEnabled(enabled: Boolean) {
        rollNumberField.isEnabled = enabled
        studentEmailField.isEnabled = enabled
        studentMobileField.isEnabled = enabled
        okButton.visibility = if (enabled) View.VISIBLE else View.GONE
        editButton.visibility = if (enabled) View.GONE else View.VISIBLE

        if (!enabled) {
            rollNumberField.setText(model?.rollNo)
            studentEmailField.setText(model?.email)
            studentMobileField.setText(model?.phone)
        }
    }

    private fun inviteStudent(rollNo: String, email: String) {
        context?.let {
            InvitationTask(it, schoolId, email, teacherId, classId, rollNo).start { task ->
                isCancelable = true
                okButton.isEnabled = true

                if (task.isSuccessful) {
                   /* Toast.makeText(
                            studentEmailField.context,
                            getString(R.string.status_invitation_sent),
                            Toast.LENGTH_LONG
                    ).show()
                    dismiss()*/
                } else {
                    Toast.makeText(context,
                            getString(R.string.status_invitation_sent),
                            Toast.LENGTH_LONG
                    ).show()
                    dismiss()
                   // studentEmailField.showError(task.exception?.message ?: getString(R.string.error_invitation_not_sent))
                }
            }
        }
    }
    fun createSignUpActionForStudent(referralCode: String, rollNo: String, classId: String):
            ActionCodeSettings {
         url = "$domain$ACTION_REGISTRATION?" +
                "${CygnusApp.PARAM_REFERRAL_CODE}=${encode(referralCode)}&" +
                "${CygnusApp.PARAM_ACCOUNT_TYPE}=${encode(Student::class.simpleName ?: "Student")}&" +
                "${CygnusApp.PARAM_STUDENT_CLASS_ID}=${encode(classId)}&" +
                "${CygnusApp.PARAM_STUDENT_ROLL_NO}=${encode(rollNo)}"

        val dynamicLink =FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(url))
                        .setDomainUriPrefix(domain)
                        //opening links with this app on Android
                        .setAndroidParameters(DynamicLink.AndroidParameters.Builder(androidPackageName).build())
                        //Opening links with com.example.iOS on iOS
                        .setIosParameters(DynamicLink.IosParameters.Builder(iOSBundleId).build())
                        .buildDynamicLink()
                        val dynamicLinkUri=dynamicLink.uri

                sendSMS(mobileField.text.toString(),"Hello, "+dynamicLink);


        return ActionCodeSettings.newBuilder()
                .setUrl(url)
                .setHandleCodeInApp(true)
                .setIOSBundleId(iOSBundleId)
                .setAndroidPackageName(androidPackageName, true, "1")
                .build()
    }
    private fun encode(param: String): String {
        return URLEncoder.encode(param, "utf-8")
    }


    private fun onOk() {
        if (rollNumberField.isNotBlank(true) &&
                studentEmailField.isNotBlank(true) ) {
            studentEmailField.visibility =  View.VISIBLE
            rollNumberField.visibility =  View.VISIBLE
            txtt1.visibility =  View.VISIBLE
            studentMobileField.visibility =  View.GONE
            txtt2.visibility =  View.GONE
            txtt3.visibility =  View.GONE


            val rollNo = rollNumberField.text.toString().trim()
            val email = studentEmailField.text.toString().trim()


                if (email.isEmail()) {
                    isCancelable = false
                    okButton.isEnabled = false

                    // Ensure roll number and email are unique
                    UsersDao.getStudentByRollNumber(schoolId, rollNo, OnSuccessListener { existingStudent ->
                        if (existingStudent != null) {
                            rollNumberField.showError(getString(R.string.error_roll_no_exists))
                            isCancelable = true
                            okButton.isEnabled = true
                        } else UsersDao.getUserByEmail(schoolId, email, OnSuccessListener { existingUser ->
                            if (existingUser != null) {
                                studentEmailField.showError(getString(R.string.error_email_exists))
                                isCancelable = true
                                okButton.isEnabled = true
                            } else
                                inviteStudent(rollNo, email)
                        })
                    })
                } else {
                    studentEmailField.showError(getString(R.string.error_email_invalid))
                }


        }
         else if (rollNumberField.isNotBlank(true) &&
                   studentMobileField.isNotBlank(true)) {
            studentMobileField.visibility =  View.VISIBLE
            rollNumberField.visibility =  View.VISIBLE
            txtt3.visibility =  View.VISIBLE
            txtt2.visibility =  View.GONE
            studentEmailField.visibility =  View.GONE
            txtt1.visibility =  View.GONE

               val rollNo = rollNumberField.text.toString().trim()
               val mobile = studentMobileField.text.toString().trim()
               if (mobile.length > 6 && mobile.length <= 13) {
                   isCancelable = false
                   okButton.isEnabled = false

                   // Ensure roll number and email are unique
                   UsersDao.getStudentByRollNumber(schoolId, rollNo, OnSuccessListener { existingStudent ->
                       if (existingStudent != null) {
                           rollNumberField.showError(getString(R.string.error_roll_no_exists))
                           isCancelable = true
                           okButton.isEnabled = true
                       } else UsersDao.getUserByPhone(schoolId, mobile, OnSuccessListener { existingUser ->
                           if (existingUser != null) {
                               studentMobileField.setError("Mobile already exists..")
                               isCancelable = true
                               okButton.isEnabled = true
                           }
                           else{
                             //  val acs:ActionCodeSettings=createSignUpActionForStudent(schoolId, rollNo!!, classId!!)
                             //  sendSMS("9115947240", acs.androidPackageName)


                               val appPackageName: String = context!!.getPackageName() // getPackageName() from Context or Activity object
                               var playstore:String

                                   playstore= Uri.parse("https://play.google.com/store/apps/details?id="+appPackageName).toString()
                                   Log.e("msg","taggggg::Hello,\nWe received a request to sign in to Eduistein School App using this number.If you want to sign up,click this link:\n"+playstore+"\nThanks,Eduistein School App Team")
                               createSignUpActionForStudent(schoolId,rollNo,classId);
                               //sendSMS(mobile,"Hello,We received a request to sign in.\nClick this link:\n"+playstore+"\nThanks,Eduistein School App Team");
                              // sendSMS("9115947240","Hello,\nWe received a request to sign in to Eduistein School App using this number.If you want to sign up,click this link:\nThanks,Eduistein School App Team");

                           }
                       })
                   })
               } else {
                   studentMobileField.setError("Enter valid mobile number")
               }

           }
    }




    fun sendSMS(phoneNo: String, msg: String) {
        try {

            val  sms_uri = Uri.parse("smsto:" +phoneNo);
             val sms_intent =  Intent(Intent.ACTION_VIEW, sms_uri);
            sms_intent.setData(sms_uri);
            sms_intent.putExtra("sms_body", msg);
            startActivity(sms_intent);
           // val smsManager: SmsManager = SmsManager.getDefault()
            //val smsBody =  StringBuffer()
           // smsManager.sendTextMessage(phoneNo, null, msg, null, null)


         //   Toast.makeText(context, "Message Sent on "+phoneNo, Toast.LENGTH_LONG).show()
            isCancelable = true
            okButton.isEnabled = true
            dismiss()
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message.toString(), Toast.LENGTH_LONG).show()
            ex.printStackTrace()
            isCancelable = true
            okButton.isEnabled = true
        }

       /*context?.let {
            InvitationTask(it, schoolId, email, teacherId, classId, rollNo).start { task ->
                isCancelable = true
                okButton.isEnabled = true

                if (task.isSuccessful) {
                    *//* Toast.makeText(
                             studentEmailField.context,
                             getString(R.string.status_invitation_sent),
                             Toast.LENGTH_LONG
                     ).show()
                     dismiss()*//*
                } else {
                    try {
                        val smsManager: SmsManager = SmsManager.getDefault()
                        smsManager.sendTextMessage(phoneNo, null, msg, null, null)
                        Toast.makeText(context, "Message Sent on "+phoneNo, Toast.LENGTH_LONG).show()
                        isCancelable = true
                        okButton.isEnabled = true
                        dismiss()
                    } catch (ex: Exception) {
                        Toast.makeText(context, ex.message.toString(), Toast.LENGTH_LONG).show()
                        ex.printStackTrace()
                        isCancelable = true
                        okButton.isEnabled = true
                    }
                }
                    // studentEmailField.showError(task.exception?.message ?: getString(R.string.error_invitation_not_sent))
                }
            }*/
        }



    companion object {
        private const val ARG_CLASS_ID = "class_id"
        private const val ARG_SCHOOL_ID = "school_id"
        private const val ARG_TEACHER_ID = "teacher_id"
        private const val ARG_SAVED_INSTANCE = "saved"

        @JvmStatic
        fun newInstance(
                classId: String,
                teacherId: String,
                schoolId: String,
                savedInstance: Student? = null
        ): AddStudentDialog {
            return AddStudentDialog().also {
                it.arguments = Bundle().apply {
                    putString(ARG_CLASS_ID, classId)
                    putString(ARG_TEACHER_ID, teacherId)
                    putString(ARG_SCHOOL_ID, schoolId)
                    putSerializable(ARG_SAVED_INSTANCE, savedInstance)
                }
            }
        }
    }

}