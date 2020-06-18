package com.androidbull.messmanagment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidbull.customprogressview.CustomProgressView;
import com.androidbull.messmanagment.fragment.LoginFragment;
import com.androidbull.messmanagment.fragment.PhoneFragment;
import com.androidbull.messmanagment.fragment.SignUpFragment;
import com.androidbull.messmanagment.modal.User;
import com.androidbull.messmanagment.util.Util;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.waqasyounis.phonenumberinput.PhoneNumberEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.michaelbel.bottomsheet.BottomSheet;

import static com.androidbull.messmanagment.MainActivity.SIGNIN_METHOD.EMAIL;
import static com.androidbull.messmanagment.MainActivity.SIGNIN_METHOD.FACEBOOK;
import static com.androidbull.messmanagment.MainActivity.SIGNIN_METHOD.GOOGLE;
import static com.androidbull.messmanagment.MainActivity.SIGNIN_METHOD.PHONE;
import static com.androidbull.messmanagment.util.SignInWithGoogle.RESULT_CODE_SINGIN;

public class MainActivity extends AppCompatActivity {

    private static final String SIGN_UP_FRAGMENT = "SignUpFragment";
    private static final String SIGN_IN_FRAGMENT = "SignInFragment";
    private static final String TAG = "MainActivity";
    public static final String SIGN_IN_METHOD = "Sign_in_method";
    public static final String SIGN_IN_WITH_EMAIL = "password";
    public static final String SIGN_IN_WITH_GOOGLE = "google.com";
    public static final String SIGN_IN_WITH_FACEBOOK = "facebook.com";
    public static final String SIGN_IN_WITH_PHONE = "phone";

    private RelativeLayout mRlContainer;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private TextView mTvSignWhat;
    private LinearLayout mLlSign;

    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    private ImageView mIvGoogle, mIvFacebook, mIvPhone;

    private View progressView;
    private View bottomLayout;

    private GoogleSignInClient googleSignInClient;

    private CustomProgressView customProgressView;
    private CallbackManager mFacebookCallbackManager;
    private LoginButton loginButton;

    private FirebaseAuth mFirebaseAuth;
    private BottomSheet.Builder signInWithPhoneNumberBottomSheetBuilder;
    private View signInWithPhoneNumberView;
    private EditText mEtVerficiationCode;

    private String mVerificationId;
    private Button mBtnContinue;
    private PhoneNumberEditText phoneNumberEditText;
    private TextView mTvCountdown;

    public static enum SIGNIN_METHOD {
        FACEBOOK,
        GOOGLE,
        PHONE,
        EMAIL

    }

    SignUpFragment.OnClickListener signUpListener = new SignUpFragment.OnClickListener() {
        @Override
        public void signInClicked() {
            switchFragment();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setUpBottomSheet();

        mFirebaseAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();

        customProgressView = new CustomProgressView(this);

        mFacebookCallbackManager = CallbackManager.Factory.create();

        loginButton = new LoginButton(this);
        loginButton.setPermissions("public_profile", "email", "name");
        loginButton.setLoginBehavior(LoginBehavior.WEB_ONLY);


        //Registering callback!
        loginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Sign in completed
                Log.i(TAG, "onSuccess: logged in successfully");
                customProgressView.done("Completed");
                FirebaseMessaging.getInstance().subscribeToTopic("notification");

                handleFacebookAccessToken(loginResult.getAccessToken());

                //Getting the user information
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        Log.i(TAG, "onCompleted: response: " + response.toString());
                        try {
                            String email = object.getString("email");
                            String birthday = object.getString("birthday");
                            String name = object.getString("name");
                            String ID_ = (String) object.get("id");

                            Log.i(TAG, "onCompleted: Email: " + email);
                            Log.i(TAG, "onCompleted: Birthday: " + birthday);
                            Log.i(TAG, "onCompleted: Birthday: " + name);
                            Log.i(TAG, "onCompleted: ID: " + ID_);
                            Log.i(TAG, "onCompleted: Profile URL: " + "https://graph.facebook.com/" + ID_ + "/picture?type=large");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "onCompleted: JSON exception");
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                customProgressView.failed("Cancelled");
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                customProgressView.failed(error.getLocalizedMessage()
                );
                Log.d(TAG, "facebook:onError", error);
            }
        });


        mIvGoogle.setOnClickListener(signInWithGoogleClickListener);
        mIvFacebook.setOnClickListener(signInWithFacebookClickListener);
        mIvPhone.setOnClickListener(signInWithPhoneNumberClickListener);

        loginFragment = new LoginFragment();
        signUpFragment = new SignUpFragment(signUpListener, this);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, loginFragment, SIGN_IN_FRAGMENT).commit();

        mTvSignWhat.setText("Sign Up");
        mLlSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment();
            }
        });


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }

    private void init() {
        mRlContainer = findViewById(R.id.container);
        mTvSignWhat = findViewById(R.id.tv_what);
        mLlSign = findViewById(R.id.ll_what);
        bottomLayout = findViewById(R.id.bottom_layout);
        mIvFacebook = findViewById(R.id.iv_facebook);
        mIvGoogle = findViewById(R.id.iv_google);
        mIvPhone = findViewById(R.id.iv_phone);
        progressView = findViewById(R.id.view_signup_with_email_progress);
    }

    private void setUpBottomSheet() {
        signInWithPhoneNumberBottomSheetBuilder = new BottomSheet.Builder(this);
        signInWithPhoneNumberView = getLayoutInflater().inflate(R.layout.layout_signup_with_phone_number, null);
        signInWithPhoneNumberBottomSheetBuilder.setView(signInWithPhoneNumberView);
        signInWithPhoneNumberBottomSheetBuilder.setDarkTheme(true);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in with facebook success, update UI with the signed-in user's information
                            customProgressView.done("Complete");
                            customProgressView.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    startDataCollectionActivity(FACEBOOK);

                                }
                            });

                        } else {
                            // Sign in with facebook failed
                            customProgressView.failed("Something went wrong");
                        }

                        // ...
                    }
                });
    }

    private void startDataCollectionActivity(SIGNIN_METHOD signinMethod) {
        Intent collectDataActivityIntent = new Intent(MainActivity.this, CollectDataActivity.class);
        collectDataActivityIntent.putExtra(SIGN_IN_METHOD, signinMethod);
        startActivity(collectDataActivityIntent);
        finish();
    }


    private View.OnClickListener signInWithPhoneNumberClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PhoneFragment phoneFragment = new PhoneFragment();
            phoneFragment.show(getSupportFragmentManager(), "PhoneFragment");
        }
    };


    private View.OnClickListener signInWithFacebookClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customProgressView.show();

            if (!Util.isNetworkAvailable(MainActivity.this)) {
                customProgressView.failed("No network");
                return;
            }

            loginButton.performClick();
        }
    };

    private View.OnClickListener signInWithGoogleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customProgressView.show();
            if (!Util.isNetworkAvailable(MainActivity.this)) {
                //No internet
                customProgressView.failed("No Internet");
                return;
            }


            Intent singInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(singInIntent, RESULT_CODE_SINGIN);
        }
    };

    private void switchFragment() {
        Fragment activeFragment = fragmentManager.findFragmentByTag(SIGN_IN_FRAGMENT);
        if (activeFragment != null && activeFragment.isVisible()) {
            showSignUpFragment();
        } else {
            showSignInFragment();
        }
    }

    private void showSignUpFragment() {
        mTvSignWhat.setText("Sign In");
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(mRlContainer.getId(), signUpFragment, SIGN_UP_FRAGMENT)
                .commit();
    }

    private void showSignInFragment() {
        mTvSignWhat.setText("Sign Up");
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(mRlContainer.getId(), loginFragment, SIGN_IN_FRAGMENT)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESULT_CODE_SINGIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }


        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        //we use try catch block because of Exception.
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //SignIn successful now show authentication
            Log.i(TAG, "handleSignInResult: name: " + account.getDisplayName());
            Log.i(TAG, "handleSignInResult: email: " + account.getEmail());
            Log.i(TAG, "handleSignInResult: email: " + account.getPhotoUrl());
            FirebaseGoogleAuth(account);

        } catch (ApiException e) {
            e.printStackTrace();
            customProgressView.failed("Failed\nTry again!");
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        //here we are checking the Authentication Credential and checking the task is successful or not and display the message
        //based on that.
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Sign In with google is successful
                    customProgressView.done("Completed");
                    Log.i(TAG, "onComplete: sign up with Google URL: " + mFirebaseAuth.getCurrentUser().getPhotoUrl());
                    customProgressView.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("/Mess/Students/" + mFirebaseAuth.getCurrentUser().getUid());
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild("Profile")) {
                                        //This is not a new user,
                                        User user = dataSnapshot.getValue(User.class);
                                        if (!user.isProfileComplete()) {
                                            //User information is not complete
                                            //Starting profile completion activity
                                            startDataCollectionActivity(GOOGLE);
                                        } else {
                                            //Profile is completed, starting the home (Main2Activity)
                                            startActivity(new Intent(MainActivity.this, Main2Activity.class));
                                        }

                                    } else {
                                        //This is a completely new user
                                        startDataCollectionActivity(GOOGLE);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                } else {
                    customProgressView.done(task.getException().getLocalizedMessage());
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        /*

        case(getProviderID){
        case "password": Logged in with email and password
        case "google.com": Logged in with Google
        case "facebook.com": Logged in with Facebook
        case "phone": Logged in with Phone
        }

         */

        SIGNIN_METHOD signin_method = EMAIL;


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //Someone is logged in
            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                switch (user.getProviderId()) {
                    case SIGN_IN_WITH_PHONE:
                        signin_method = PHONE;
                        break;
                    case SIGN_IN_WITH_FACEBOOK:
                        signin_method = FACEBOOK;
                        break;
                    case SIGN_IN_WITH_GOOGLE:
                        signin_method = GOOGLE;
                        break;
                }
            }
            Intent dataCollectionIntent = new Intent(this, Main2Activity.class);
            dataCollectionIntent.putExtra(SIGN_IN_METHOD, signin_method);
            startActivity(dataCollectionIntent);
            finish();
        }
    }
}

