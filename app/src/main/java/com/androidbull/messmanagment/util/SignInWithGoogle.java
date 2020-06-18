package com.androidbull.messmanagment.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.androidbull.messmanagment.R;
import com.androidbull.messmanagment.signupwith.SignUpWith;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInWithGoogle {
    private static final String TAG = "SignInWithGoogle";
    public static final int RESULT_CODE_SINGIN = 56;

    private Activity context;
    private GoogleSignInClient googleSignInClient;

    public SignInWithGoogle(Activity context) {
        this.context = context;


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
        Intent singInIntent = googleSignInClient.getSignInIntent();
        context.startActivityForResult(singInIntent, RESULT_CODE_SINGIN);
    }

    public void handleSignInResult(Task<GoogleSignInAccount> task) {
        //we use try catch block because of Exception.
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(context, "Signed In successfully", Toast.LENGTH_LONG).show();
            //SignIn successful now show authentication
            Log.i(TAG, "handleSignInResult: name: " + account.getDisplayName());
            Log.i(TAG, "handleSignInResult: email: " + account.getEmail());
            Log.i(TAG, "handleSignInResult: email: " + account.getPhotoUrl());
            FirebaseGoogleAuth(account);

        } catch (ApiException e) {
            e.printStackTrace();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(context, "SignIn Failed!!!", Toast.LENGTH_LONG).show();
//            FirebaseGoogleAuth(null);
        }
    }


    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        //here we are checking the Authentication Credential and checking the task is successful or not and display the message
        //based on that.
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
