package com.androidbull.messmanagment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidbull.messmanagment.util.CircleImageView;
import com.androidbull.messmanagment.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    public static final int ACTION_REQUEST_GALLERY = 001;
    private CircleImageView mCivProfilePic;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtPasswordRepeat;

    private Button mBtnSignUp;
    private Button mBtnSignUpWithGoogle;
    private Button mBtnSignUpWithFacebook;

    private LinearLayout mLlAlreadyHaveAccount;

    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();

        initUi();

        mBtnSignUp.setOnClickListener(btnSignUpClickListener);

        mCivProfilePic = findViewById(R.id.civ_profile_pic);
        mCivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // GET IMAGE FROM THE GALLERY
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                Intent chooser = Intent.createChooser(intent, "Choose a Picture");
                startActivityForResult(chooser, ACTION_REQUEST_GALLERY);
            }
        });

    }

    private View.OnClickListener btnSignUpClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String email = mEtEmail.getText().toString();
            String password = mEtPassword.getText().toString();
            String repeatPassword = mEtPasswordRepeat.getText().toString();

            if(!isEmailValid(email)){
                //Email format is invalid
                Toast.makeText(SignUpActivity.this, "Please provide valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if(password.length()<6){
                //Password is less than 6 six digits
                Toast.makeText(SignUpActivity.this, "Password must be 6 digits long", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!password.equals(repeatPassword)){
                //Password does not match
                Toast.makeText(SignUpActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                return;
            }

            mFirebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Sign Up Complete
                        Log.i(TAG, "Sign up with email and password Successful " + Util.lineOut());
                    }else{
                        //Somwthing went wrong with the sign up
                        Log.i(TAG, "Sign up with email and password Failed \nError: " + task.getException().getLocalizedMessage() + Util.lineOut());
                    }
                }
            });


        }
    };

    //Method to chack if email is in a valid format or not
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void initUi() {
        mEtEmail = findViewById(R.id.et_signup_email);
        mEtPassword = findViewById(R.id.et_signup_pass);
        mEtPasswordRepeat = findViewById(R.id.et_signup_pass_repeat);

        mBtnSignUp = findViewById(R.id.btn_signup_signup);
        mBtnSignUpWithFacebook = findViewById(R.id.btn_signup_with_facebook);
        mBtnSignUpWithGoogle = findViewById(R.id.btn_signup_with_google);

        mLlAlreadyHaveAccount = findViewById(R.id.ll_already_account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ACTION_REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                mCivProfilePic.setImageURI(selectedImage);
            } else {
                Toast.makeText(this, "Something went wrong while choosing pic\nPlease try again", Toast.LENGTH_SHORT).show();
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
