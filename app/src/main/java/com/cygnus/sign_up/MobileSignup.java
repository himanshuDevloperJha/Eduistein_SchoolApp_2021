package com.cygnus.sign_up;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cygnus.ProfileActivity;
import com.cygnus.R;
import com.cygnus.SignInActivity;
import com.cygnus.StudentDashboardActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;

public class MobileSignup extends AppCompatActivity {
    RelativeLayout relative_layout_confirm_phone_number,relative_layout_confirm_top_login_activity;
    private String phoneNum ="";
    String VerificationCode = "";
    OtpEditText otp_edit_text_login_activity;
LinearLayout linear_layout_otp_confirm_login_activity,linear_layout_phone_input_login_activity;
    CountryCodePicker countryCodePicker;
    EditText edit_text_phone_number_login_acitivty;
    TextView tv_txt2;
    ProgressBar pb_bar1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilesignup);

        relative_layout_confirm_phone_number = findViewById(R.id.relative_layout_confirm_phone_number);

        countryCodePicker = findViewById(R.id.countrycodepicker);
        pb_bar1 = findViewById(R.id.pb_bar1);
        edit_text_phone_number_login_acitivty = findViewById(R.id.edit_text_phone_number_login_acitivty);
        otp_edit_text_login_activity = findViewById(R.id.otp_edit_text_login_activity);
        linear_layout_otp_confirm_login_activity = findViewById(R.id.linear_layout_otp_confirm_login_activity);
        linear_layout_phone_input_login_activity = findViewById(R.id.linear_layout_phone_input_login_activity);
        relative_layout_confirm_top_login_activity = findViewById(R.id.relative_layout_confirm_top_login_activity);

        relative_layout_confirm_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edit_text_phone_number_login_acitivty.getText().toString().isEmpty()){
                    Toast.makeText(MobileSignup.this, "Enter mobile number", Toast.LENGTH_SHORT).show();

                }
                else {
                    phoneNum = "+" + countryCodePicker.getSelectedCountryCode().toString() +
                            edit_text_phone_number_login_acitivty.getText().toString();
              /*  new AlertDialog.Builder(MobileSignup.this)
                        .setTitle("We will be verifying the phone number:")
                        .setMessage(" \n" + phoneNum + " \n\n Is this OK,or would you like to edit the number ?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Do Something Here
                                loginWithPhone();
                            }
                        }).setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();*/

                    final Dialog dialog = new Dialog(MobileSignup.this);
                    dialog.setContentView(R.layout.dialog_recharge);
                    // dialog.setCancelable(false);
                    //dialog.setCanceledOnTouchOutside(false);
                    //  dialog.getWindow().setWindowAnimations(R.style.DialogTheme);
                    dialog.show();
                    Button btn_confirm = dialog.findViewById(R.id.btn_confirm);
                    tv_txt2 = dialog.findViewById(R.id.tv_txt2);
                    Button btn_edit = dialog.findViewById(R.id.btn_edit);
                    tv_txt2.setText("We will be verifying the phone number: \n" + phoneNum + "\n\nIs this OK,or would you like to edit the number ?");


                    btn_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btn_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            loginWithPhone();

                        }
                    });
                }

            }
        });
        relative_layout_confirm_top_login_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb_bar1.setVisibility(View.VISIBLE);

                if (otp_edit_text_login_activity.getText().toString().trim().length()==0){
                    Toast.makeText(MobileSignup.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                    pb_bar1.setVisibility(View.GONE);

                }else {
                    if (otp_edit_text_login_activity.getText().toString().trim().equals(VerificationCode.toString().trim())) {
                        //String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg";
                       // signUp(phoneNum, phoneNum, "null".toString(), "phone", photo);
                    //  otp_edit_text_login_activity.setText("");
                        startActivity(new Intent(MobileSignup.this, SignInActivity.class));
                     finish();
                        otp_edit_text_login_activity.setText("");
                        pb_bar1.setVisibility(View.GONE);

                    } else {
                        Toast.makeText(MobileSignup.this, "The verification code you have been entered incorrect !", Toast.LENGTH_SHORT).show();
                        pb_bar1.setVisibility(View.GONE);

                    }
                }
            }
        });


    }
    private void loginWithPhone() {
        linear_layout_phone_input_login_activity.setVisibility(View.GONE);
        linear_layout_otp_confirm_login_activity.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNum, 30L /*timeout*/, TimeUnit.SECONDS,
                this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Toast.makeText(MobileSignup.this, "OTP send", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                        VerificationCode = phoneAuthCredential.getSmsCode().toString();
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(MobileSignup.this, "Verification Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("msg", "onVerificationFailed: "+e.getMessage() );
                    }
                });
    }

}
