package com.androidbull.messmanagment.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidbull.customprogressview.CustomProgressView;
import com.androidbull.messmanagment.CollectDataActivity;
import com.androidbull.messmanagment.MainActivity;
import com.androidbull.messmanagment.R;
import com.androidbull.messmanagment.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.waqasyounis.phonenumberinput.InvalidPhoneNumberException;
import com.waqasyounis.phonenumberinput.PhoneNumberEditText;

import java.util.concurrent.TimeUnit;

import static com.androidbull.messmanagment.MainActivity.SIGN_IN_METHOD;

public class PhoneFragment extends BottomSheetDialogFragment {
    private static final String TAG = "PhoneFragment";
    private static final int RESEND_VERIFICATION_CODE_SECONDS = 10;

    private Button mBtnContinue;
    private PhoneNumberEditText mPhoneEditText;
    private EditText mETCode;
    private TextView mTvCountDown;
    private CustomProgressView customProgressView;
    private String mVerificationId;

    private PhoneNumberEditText.OnDoneListener onDoneListener = new PhoneNumberEditText.OnDoneListener() {
        @Override
        public void onDone(String number) {
            mBtnContinue.performClick();
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_signup_with_phone_number, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        customProgressView = new CustomProgressView(getContext());
        mBtnContinue.setOnClickListener(continueClickListener);
        mTvCountDown.setOnClickListener(resendClickListener);
        mPhoneEditText.setOnDoneListener(onDoneListener);
    }

    private View.OnClickListener resendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTvCountDown.getVisibility() == View.VISIBLE && mTvCountDown.isEnabled()) {
                mBtnContinue.setText("Resend Code");
                mETCode.setVisibility(View.INVISIBLE);
                mTvCountDown.setVisibility(View.INVISIBLE);
                mPhoneEditText.setVisibility(View.VISIBLE);
                mTvCountDown.setEnabled(false);
            }
        }
    };

    private View.OnClickListener continueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customProgressView.show();
            if (!Util.isNetworkAvailable(getContext())) {
                customProgressView.failed("No Internet");
                return;
            }
            if (mBtnContinue.getText().equals("Get Code")) {
                //Send Verification Code
                PhoneFragment.this.setCancelable(false);
                try {
                    String phoneNumber = mPhoneEditText.getPhoneNumberWithCountryCode();
                    sendVerificationCode(phoneNumber);

                } catch (InvalidPhoneNumberException e) {
                    //Phone Number format is not correct
                    customProgressView.failed("Invalid Phone Number");
                    e.printStackTrace();
                }

            } else {
                //Verify Verification Code
                verifyVerificationCode(mETCode.getText().toString());

            }
        }
    };

    private void sendVerificationCode(String phoneNumber) {
        //Provided the number and now asking to continue sending verification code
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
    }


    private void init(View view) {
        mBtnContinue = view.findViewById(R.id.btn_reset_password_reset_password);
        mPhoneEditText = view.findViewById(R.id.phone_number_edit_text);
        mETCode = view.findViewById(R.id.et_phone_number_code);
        mTvCountDown = view.findViewById(R.id.tv_countdown);
        mTvCountDown.setEnabled(false);
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
                        mETCode.setText(code);
                        //verifying the code
                        verifyVerificationCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Log.i(TAG, "onVerificationFailed: " + e.getLocalizedMessage());
                    customProgressView.failed(e.getLocalizedMessage());
                    PhoneFragment.this.setCancelable(true);

                }

                //when the code is generated then this method will receive the code.
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                super.onCodeSent(s, forceResendingToken);

                    //Hide the Progress View
                    customProgressView.done("Code Sent");

                    //storing the verification id that is sent to the user
                    mVerificationId = s;

                    mBtnContinue.setText("Verify");
                    mPhoneEditText.setVisibility(View.INVISIBLE);
                    mETCode.setVisibility(View.VISIBLE);

                    mTvCountDown.setVisibility(View.VISIBLE);
                    mTvCountDown.setEnabled(false);

                    startTimer();


                }
            };

    private void startTimer() {
        new CountDownTimer(RESEND_VERIFICATION_CODE_SECONDS * 1000, 1000) {
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
                mTvCountDown.setEnabled(true);
            }
        }.start();
    }

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    //used for signing the user
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(getActivity(),
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //hide progress view
                                if (task.isSuccessful()) {
                                    PhoneFragment.this.setCancelable(true);
                                    //verification successful we will start the profile activity
                                    customProgressView.done("Verified");
                                    customProgressView.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            startDataCollectionActivity(MainActivity.SIGNIN_METHOD.PHONE);
                                        }
                                    });

                                } else {
                                    //verification unsuccessful.. display an error message
                                    String message = "Something is wrong\nPlease try again";
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        message = "Invalid code entered...";
                                    }
                                    customProgressView.failed(task.getException().getLocalizedMessage());
                                }
                            }
                        });
    }

    private void startDataCollectionActivity(MainActivity.SIGNIN_METHOD signinMethod) {
        Intent intent = new Intent(getActivity(), CollectDataActivity.class);
        intent.putExtra(SIGN_IN_METHOD,signinMethod);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }


}
