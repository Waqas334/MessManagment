package com.androidbull.messmanagment.signupwith;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidbull.messmanagment.Main2Activity;
import com.androidbull.messmanagment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.waqasyounis.phonenumberinput.InvalidPhoneNumberException;
import com.waqasyounis.phonenumberinput.PhoneNumberEditText;

import java.util.concurrent.TimeUnit;

public class SignUpWithPhoneNumber extends AppCompatActivity {

    private static final String TAG = "SignUpWithPhoneNumber";

    private EditText mEtVarificationCode;
    private Button mBtnContinue;
    private TextView mTvCountDown;

    private FirebaseAuth mFirebaseAuth;

    private String mVerificationId;


    private PhoneNumberEditText phoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_phone_number);

        mFirebaseAuth = FirebaseAuth.getInstance();


        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        mBtnContinue = findViewById(R.id.btn_signup_with_phone_number_continue);
        mEtVarificationCode = findViewById(R.id.et_phone_number_code);
        mTvCountDown = findViewById(R.id.tv_countdown);

        //Hide progressBar as soon as activity starts
        showProgressBar(false);

        phoneNumberEditText.setOnDoneListener(new PhoneNumberEditText.OnDoneListener() {
            @Override
            public void onDone(String number) {
                //This will be called when user presses the done button from keyboard
                sendVarificationCode(number);
                showProgressBar(true);
            }
        });

        //As activity starts, request the focus on Phone Number Edit Text
        phoneNumberEditText.requestFocus(PhoneNumberEditText.FOCUS.CODE);


        mTvCountDown.setVisibility(View.GONE);
        mTvCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvCountDown.getText().toString().equals("Resend")) {
                    //User clicked resend
                    phoneNumberEditText.setVisibility(View.VISIBLE);
                    mEtVarificationCode.setVisibility(View.INVISIBLE);
                    v.setVisibility(View.GONE);
                }
            }
        });

        mBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar(true);
                try {
                    if (phoneNumberEditText.getVisibility() == View.VISIBLE) {
                        sendVarificationCode(phoneNumberEditText.getPhoneNumberWithCountryCode());
                    } else {
                        //Code received and written to the textView, asking to verify the code
                        verifyVerificationCode(mEtVarificationCode.getText().toString());

                    }
                } catch (InvalidPhoneNumberException e) {
                    showProgressBar(false);
                    e.printStackTrace();
                    Toast.makeText(SignUpWithPhoneNumber.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void showProgressBar(boolean show) {
        if (show) {
            //Show ProgressView and disable all items on screen
            mEtVarificationCode.setEnabled(false);
            mBtnContinue.setEnabled(false);
            phoneNumberEditText.setEnabled(false);
        } else {
            //Hide Progress view and enable all items on screen

            mEtVarificationCode.setEnabled(true);
            mBtnContinue.setEnabled(true);
            phoneNumberEditText.setEnabled(true);

        }
    }

    private void sendVarificationCode(String number) {
        //Provided the number and now asking to continue sending verification code
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    //Getting the code sent by SMS
                    String code = phoneAuthCredential.getSmsCode();

                    //sometime the code is not detected automatically
                    //in this case the code will be null
                    //so user has to manually enter the code
                    if (code != null) {
                        mEtVarificationCode.setText(code);
                        //verifying the code
                        verifyVerificationCode(code);
                    }
                    showProgressBar(false);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(SignUpWithPhoneNumber.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, "onVerificationFailed: " + e.getLocalizedMessage());
                    showProgressBar(false);

                }

                //when the code is generated then this method will receive the code.
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                super.onCodeSent(s, forceResendingToken);

                    //Hide the Progress View
                    showProgressBar(false);
                    mEtVarificationCode.requestFocus();

                    //storing the verification id that is sent to the user
                    mVerificationId = s;
                    phoneNumberEditText.setVisibility(View.INVISIBLE);
                    mTvCountDown.setVisibility(View.VISIBLE);
                    mEtVarificationCode.setVisibility(View.VISIBLE);

                    new CountDownTimer(15000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long seconds = millisUntilFinished / 1000;
                            if (seconds < 10) {
                                mTvCountDown.setText("00 : 0" + millisUntilFinished / 1000);
                            } else {
                                mTvCountDown.setText("00 : " + millisUntilFinished / 1000 + "");

                            }
                        }

                        @Override
                        public void onFinish() {
                            mTvCountDown.setText("Resend");
                        }
                    }.start();


                }
            };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    //used for signing the user
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignUpWithPhoneNumber.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //hide progress view
                                showProgressBar(false);
                                if (task.isSuccessful()) {
                                    //verification successful we will start the profile activity
                                    Intent intent = new Intent(SignUpWithPhoneNumber.this, Main2Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    //verification unsuccessful.. display an error message
                                    String message = "Something is wrong, we will fix it soon...";
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        message = "Invalid code entered...";
                                    }
                                    Toast.makeText(SignUpWithPhoneNumber.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }
}
