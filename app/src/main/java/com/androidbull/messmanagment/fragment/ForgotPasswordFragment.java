package com.androidbull.messmanagment.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidbull.customprogressview.CustomProgressView;
import com.androidbull.messmanagment.R;
import com.androidbull.messmanagment.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ForgotPasswordFragment extends BottomSheetDialogFragment {

    private EditText mEtEmail;
    private Button mBtnButton;
    private String email;
    private CustomProgressView customProgressView;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_password_reset, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        customProgressView = new CustomProgressView(getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        mBtnButton.setOnClickListener(forgotPasswordClickListener);


    }


    private View.OnClickListener forgotPasswordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customProgressView.show();
            email = mEtEmail.getText().toString();
            if (!Util.isNetworkAvailable(getContext())) {
                customProgressView.failed("No Internet");
                return;
            }

            if (!Util.isEmailValid(email)) {
                customProgressView.failed("Invalid Email");
                return;
            }

            firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if (task.getResult().getSignInMethods().size() == 0) {
                        //Email not already exists
                        customProgressView.failed("No User found");
                    } else {
                        //Email exists
                        //Send reset email
                        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Task completed
                                    customProgressView.done("Email Sent");
                                } else {
                                    customProgressView.failed(task.getException().getLocalizedMessage());
                                }
                            }
                        });

                    }
                }
            });


        }
    };

    private void init(View view) {
        mEtEmail = view.findViewById(R.id.et_password_reset_email);
        mBtnButton = view.findViewById(R.id.btn_reset_password_reset_password);
    }
}
