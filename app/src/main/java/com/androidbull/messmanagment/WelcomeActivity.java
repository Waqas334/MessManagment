package com.androidbull.messmanagment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.androidbull.messmanagment.signupwith.SignUpWith;

public class WelcomeActivity extends AppCompatActivity {
    private Button mBtnSignUp, mBtnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mBtnSignIn = findViewById(R.id.btn_welcome_signin);
        mBtnSignUp = findViewById(R.id.btn_welcome_signup);

        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, SignInWith.class));
            }
        });

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, SignUpWith.class));
            }
        });

    }
}
