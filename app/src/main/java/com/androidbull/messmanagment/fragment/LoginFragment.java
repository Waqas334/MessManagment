package com.androidbull.messmanagment.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidbull.custombottomsheet.CustomBottomSheet;
import com.androidbull.customprogressview.CustomProgressView;
import com.androidbull.messmanagment.CollectDataActivity;
import com.androidbull.messmanagment.Main2Activity;
import com.androidbull.messmanagment.R;
import com.androidbull.messmanagment.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.michaelbel.bottomsheet.BottomSheet;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    public static final String FORGOT_PASSWORD_FRAGMENT_TAG = "ForgotPassword";

    private EditText mEtEmail, mEtPassword;
    private Button mBtnSignIn;
    private TextView mTvForgotPassword;
    private FirebaseAuth firebaseAuth;

    private CustomProgressView customProgressView;
    private BottomSheet.Builder bottomSheetBuilder;
    private View forgotPasswordView;

    private DatabaseReference databaseReference;

    //Default Constructor
    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setUpBottomSheet();
        customProgressView = new CustomProgressView(getContext());
        customProgressView.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        firebaseAuth = FirebaseAuth.getInstance();
        mBtnSignIn.setOnClickListener(signInClickListener);
        mTvForgotPassword.setOnClickListener(forgotPasswordCustomBottomSheetListener);

        databaseReference = FirebaseDatabase.getInstance().getReference("/Mess/Students/" + firebaseAuth.getUid());


    }

    //Building BottomSheet
    private void setUpBottomSheet() {
        bottomSheetBuilder = new BottomSheet.Builder(getContext());
        forgotPasswordView = getActivity().getLayoutInflater().inflate(R.layout.layout_password_reset, null);
        bottomSheetBuilder.setView(forgotPasswordView);
        bottomSheetBuilder.setBackgroundColor(getResources().getColor(R.color.bottomSheetColor));
    }

    private View.OnClickListener forgotPasswordCustomBottomSheetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ForgotPasswordFragment fragment = new ForgotPasswordFragment();
            fragment.show(getFragmentManager(), FORGOT_PASSWORD_FRAGMENT_TAG);
        }
    };

    //Sign In Click Listener
    private View.OnClickListener signInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customProgressView.show();
            final String email = mEtEmail.getText().toString();
            final String password = mEtPassword.getText().toString();
            Log.w(TAG, "onClick: button clicked");

            if (!Util.isNetworkAvailable(getContext())) {
                //No internet
                customProgressView.failed("No Internet");
                return;
            }

            if (!Util.isEmailValid(email)) {
                //Invalid Email
                customProgressView.failed("Invalid Email");
                return;
            }

            if (password.length() < 6) {
                customProgressView.failed("Password must be 6 character long");
                return;
            }

            Log.w(TAG, "onClick: going to perform sign in");

            firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if (task.getResult().getSignInMethods().size() == 0) {
                        //Email not already exists
                        customProgressView.failed("No Account Found");
                    } else {
                        //Begin Sign IN
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(authResultOnCompleteListener);
                    }

                }

            });

        }
    };

    private void startApp() {
        startActivity(new Intent(getContext(), Main2Activity.class));
        getActivity().finish();
    }

    private void startDetailsCompleteActivity() {

        startActivity(new
                Intent(getContext(), CollectDataActivity.class));
        getActivity().finish();

    }


    //Auth Listener for Firebase Login with Email and pass
    private OnCompleteListener<AuthResult> authResultOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                //Sign in with email and pass is successful
                customProgressView.done("Done");
                customProgressView.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //Start the Main App
                        startApp();
                        /*databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("Profile")) {
                                    databaseReference.child("/Profile/profileComplete").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue(boolean.class)) {
                                                //Profile is completed
                                                startApp();
                                            } else {
                                                //Profile is not completed
                                                startDetailsCompleteActivity();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    //Profile does not exists
                                    startDetailsCompleteActivity();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/
                    }
                });
            } else {
                //TODO Sign in failed
                customProgressView.failed("Failed: " + task.getException().getLocalizedMessage());
                Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void init(View view) {
        mEtEmail = view.findViewById(R.id.et_signup_with_email_email);
        mEtPassword = view.findViewById(R.id.et_signup_with_email_pass);
        mBtnSignIn = view.findViewById(R.id.btn_signup_with_email_signup);
        mTvForgotPassword = view.findViewById(R.id.tv_forgot_password);

    }

}
