package com.androidbull.messmanagment.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidbull.customprogressview.CustomProgressView;
import com.androidbull.messmanagment.CollectDataActivity;
import com.androidbull.messmanagment.R;
import com.androidbull.messmanagment.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SignUpFragment extends Fragment {
    private final String emailConfirmationMessage = "Please click the link you received in email to verify your email address\n\nThanks";
    private static final String TAG = "SignUpFragment";

    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtPasswordRepeat;
    private Button mBtnSignUp;
    private FirebaseAuth mFirebaseAuth;
    private RelativeLayout mRlRoot;


    private boolean emailAlreadyExists = false;
    private Snackbar mSbEmailAlreadyExists;

    private OnClickListener onClickListener;
    private Context context;

    private CustomProgressView customProgressView;


    public interface OnClickListener {
        void signInClicked();
    }

    public SignUpFragment() {
    }

    public SignUpFragment(OnClickListener onClickListener, Context context) {
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        mFirebaseAuth = FirebaseAuth.getInstance();
        customProgressView = new CustomProgressView(getContext());

//        mEtEmail.setOnFocusChangeListener(emailFocusChangeListener);
        mEtPassword.setOnFocusChangeListener(passFocusChangeListener);
        mEtPasswordRepeat.setOnFocusChangeListener(passRepeatFocusChangeListener);

        mEtEmail.addTextChangedListener(emailTextWatcher);

        mBtnSignUp.setOnClickListener(signUpButtonClickListener);

    }

/*

    private View.OnFocusChangeListener emailFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (!hasFocus) {
                if (!Util.isEmailValid(mEtEmail.getText().toString())) {
                    Util.showSnackBar(mRlRoot, "Email is invalid", "", null);
                } else {
                    checkIfEmailExists();
                }
            }

        }
    };
*/

    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mSbEmailAlreadyExists != null && emailAlreadyExists) {
                emailAlreadyExists = false;
                mSbEmailAlreadyExists.dismiss();
                mEtPassword.setEnabled(true);
                mEtPasswordRepeat.setEnabled(true);
            }

        }
    };
    private View.OnFocusChangeListener passFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus && mEtPassword.getText().length() < 6) {
                Util.showSnackBar(mRlRoot, "Password must be more than 6 character", null, null);
            }
        }
    };

    private View.OnFocusChangeListener passRepeatFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus && (!mEtPassword.getText().toString().equals(mEtPasswordRepeat.getText().toString()))) {
                Util.showSnackBar(mRlRoot, "Password not match", null, null);
            }
        }
    };
/*

    private void checkIfEmailExists() {
        mFirebaseAuth.fetchSignInMethodsForEmail(mEtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                try {
                    if (task.getResult().getSignInMethods().size() == 0) {
                        //Email not already exists
                    } else {
                        //Email already exists
                        Log.e(TAG, "Email already exists");
                        emailAlreadyExists = true;
                        mEtPassword.setEnabled(false);
                        mEtPasswordRepeat.setEnabled(false);
                        mSbEmailAlreadyExists = Util.showSnackBar(mRlRoot, "Email already exists! Try logging in?", "Login", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickListener.signInClicked();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
*/

    private View.OnClickListener signUpButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String email = mEtEmail.getText().toString();
            final String pass = mEtPassword.getText().toString();
            String passRepeat = mEtPasswordRepeat.getText().toString();
            customProgressView.show();

            if (!Util.isNetworkAvailable(getContext())) {
                //No internet
                customProgressView.failed("No Internet");
                return;
            }

            if (!Util.isEmailValid(email)) {
                customProgressView.failed("Invalid email");
                return;
            }

            if (pass.length() < 6) {
                customProgressView.failed("Pass must be 6 characters long");
                return;
            }

            if (!pass.equals(passRepeat)) {
                customProgressView.failed("Password not match");
                return;
            }


            mFirebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if (task.getResult().getSignInMethods().size() != 0) {
                        //Email already exists
                        customProgressView.failed("Email already associated with account");
                    } else {
                        //Begin Sign Up Here
                        mFirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(signUpCompleteListener);

                    }

                }
            });

            ;

        }
    };

    private OnCompleteListener signUpCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                //Sign up successful
                customProgressView.done("Registration Completed");
                customProgressView.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        //Sending Verification email
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            showEmailVarificationDialog(emailConfirmationMessage);
                                        } else {
                                            Log.e(TAG, "Verify email couldn't sent. Try again later " + Util.lineOut());
                                            Util.showSnackBar(mRlRoot,
                                                    "Verification email couldn't send, try again later",
                                                    "OK",
                                                    null);
                                        }
                                    }
                                });

                    }
                });


//                Intent intent = new Intent(SignUpFragment.this, Main2Activity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();

            } else {
                //Something went wrong
                customProgressView.failed(task.getException().getLocalizedMessage());
            }

        }
    };

    private void showEmailVarificationDialog(String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Confirm Email");
        alertBuilder
                .setMessage(message)
                .setPositiveButton("I Verified!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                            //Email not Verified
                            showEmailVarificationDialog("Email not verified still, please click the link you received in your email to verify\n\nThanks");
                        } else {
                            showEmailVerifiedDialog();
                        }
                    }
                })
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getContext(), CollectDataActivity.class));
                    }
                }).show();
    }

    private void showEmailVerifiedDialog() {

        new AlertDialog.Builder(context)
                .setTitle("Email Verified")
                .setMessage("Your email address is verified")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private void init(View view) {
        mEtEmail = view.findViewById(R.id.et_signup_with_email_email);
        mEtPassword = view.findViewById(R.id.et_signup_with_email_pass);
        mEtPasswordRepeat = view.findViewById(R.id.et_signup_with_email_pass_repeat);
        mBtnSignUp = view.findViewById(R.id.btn_signup_with_email_signup);
        mRlRoot = view.findViewById(R.id.rl_sign_up_with_email_root);
    }


}
