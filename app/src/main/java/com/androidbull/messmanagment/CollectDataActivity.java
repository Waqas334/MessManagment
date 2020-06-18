package com.androidbull.messmanagment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidbull.customprogressview.CustomProgressView;
import com.androidbull.messmanagment.modal.User;
import com.androidbull.messmanagment.util.CircleImageView;
import com.androidbull.messmanagment.util.Util;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.androidbull.messmanagment.MainActivity.SIGNIN_METHOD.EMAIL;
import static com.androidbull.messmanagment.MainActivity.SIGNIN_METHOD.FACEBOOK;
import static com.androidbull.messmanagment.MainActivity.SIGNIN_METHOD.GOOGLE;
import static com.androidbull.messmanagment.MainActivity.SIGNIN_METHOD.PHONE;
import static com.androidbull.messmanagment.MainActivity.SIGN_IN_WITH_EMAIL;
import static com.androidbull.messmanagment.MainActivity.SIGN_IN_WITH_FACEBOOK;
import static com.androidbull.messmanagment.MainActivity.SIGN_IN_WITH_GOOGLE;
import static com.androidbull.messmanagment.MainActivity.SIGN_IN_WITH_PHONE;

public class CollectDataActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 001;
    private static final String TAG = "CollectDataActivity";
    private static final int PICK_FROM_GALLERY = 1;


    private EditText mEtName;
    private EditText mEtRank;
    private EditText mEtFatherName;
    private EditText mEtRoomNo;
    private EditText mEtPhoneNumber;
    private LinearLayout mLlLogout;
    private TextView mTvChange;
    private TextView mTvLoggedInWith;
    private RelativeLayout mRlHalfCircle;
    private Button mBtnContinue;
    private CircleImageView mCivProfile;
    private CustomProgressView customProgressView;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference mealsReference;
    private DatabaseReference profileReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FirebaseUser firebaseUser;

    private Uri imageUri;
    private String error = "";

    private User user;
    private String name = "";
    private MainActivity.SIGNIN_METHOD signinMethod = EMAIL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);
        init();


        customProgressView = new CustomProgressView(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        databaseReference = firebaseDatabase.getReference().child("/Mess/Students/" + firebaseAuth.getUid() + "/");
        mealsReference = firebaseDatabase.getReference().child("/Mess/Students/" + firebaseAuth.getUid() + "/Meals/");
        profileReference = firebaseDatabase.getReference().child("/Mess/Students/" + firebaseAuth.getUid() + "/Profile/");
/*
        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Log.e(TAG, "onDataChange: user is not null");
                    {
                        if (user.isProfileComplete()) {
                            Log.i(TAG, "onDataChange: profile is not complete");
                            startActivity(new Intent(CollectDataActivity.this, Main2Activity.class));
                        }
                    }
                } else {
                    Log.e(TAG, "onDataChange: user is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        mBtnContinue.setOnClickListener(continueClickListener);
        mCivProfile.setOnClickListener(picProfileImageListener);
        mLlLogout.setOnClickListener(logoutClickListener);

        firebaseUser = firebaseAuth.getCurrentUser();

        mEtPhoneNumber.setText(firebaseUser.getPhoneNumber());

        user = new User();


        outNewUserMeals(mealsReference.child("Sunday"));
        outNewUserMeals(mealsReference.child("Monday"));
        outNewUserMeals(mealsReference.child("Tuesday"));
        outNewUserMeals(mealsReference.child("Wednesday"));
        outNewUserMeals(mealsReference.child("Thursday"));
        outNewUserMeals(mealsReference.child("Friday"));
        outNewUserMeals(mealsReference.child("Saturday"));


        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            Log.i(TAG, "onCreate: user: " + user.getProviderId());
            switch (user.getProviderId()) {
                case SIGN_IN_WITH_PHONE:
                    signinMethod = PHONE;
                    setPhoneNumber();
                    Log.i(TAG, "onCreate: sign in with phone");
                    mTvLoggedInWith.append("Phone Number. ");
                    break;
                case SIGN_IN_WITH_FACEBOOK:
                    signinMethod = FACEBOOK;
                    setProfileImage();
                    setName();
                    Log.i(TAG, "onCreate: sign in with facebook");
                    mTvLoggedInWith.append("Facebook. ");
                    break;
                case SIGN_IN_WITH_GOOGLE:
                    signinMethod = GOOGLE;
                    setProfileImage();
                    Log.i(TAG, "onCreate: sign in with google");
                    setName();
                    mTvLoggedInWith.append("Google. ");
                    break;
                case SIGN_IN_WITH_EMAIL:
                    Log.i(TAG, "onCreate: default");
                    mTvLoggedInWith.append("Email. ");
                    break;

            }
        }


    }


    private void setPhoneNumber() {
        mEtPhoneNumber.setText(firebaseUser.getPhoneNumber());
    }

    private void setName() {
        mEtName.setText(firebaseUser.getDisplayName());
    }

    private void setProfileImage() {
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(mCivProfile);
        mTvChange.setText("Change");

    }

    //Logout the user and start MainActivity
    private View.OnClickListener logoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customProgressView.show();
            firebaseAuth.signOut();
            customProgressView.done("Done");
            customProgressView.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Log.i(TAG, "onDismiss: called: " + Util.lineOut());
                    startActivity(new Intent(CollectDataActivity.this, MainActivity.class));
                    finish();
                }
            });

        }
    };

    private View.OnClickListener picProfileImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                if (ActivityCompat.checkSelfPermission(CollectDataActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CollectDataActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                } else {
                    customProgressView.show();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };

    private View.OnClickListener continueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            customProgressView.show();


            final String name = mEtName.getText().toString();
            final String fatherName = mEtFatherName.getText().toString();
            final String roomNo = mEtRoomNo.getText().toString();
            final String rank = mEtRank.getText().toString();
            final String phoneNumber = mEtPhoneNumber.getText().toString();

            if (name.isEmpty()) {
                customProgressView.failed("Please enter name");
                return;
            }
            if (fatherName.isEmpty()) {
                customProgressView.failed("Please enter father name");
                return;
            }
            if (roomNo.isEmpty()) {
                customProgressView.failed("Please enter room no");
                return;
            }
            if (rank.isEmpty()) {
                customProgressView.failed("Please enter rank");
                return;
            }
            if (phoneNumber.isEmpty()) {
                customProgressView.failed("Please enter phone number");
                return;
            }

            if (firebaseUser.getPhotoUrl() == null && imageUri == null) {
                customProgressView.failed("Please select profile image");
                return;
            }

            UserProfileChangeRequest.Builder profileChangeRequest;

            if (!name.equals(firebaseUser.getDisplayName())) {
                //If name is changed then update it
                profileChangeRequest = new UserProfileChangeRequest.Builder();
                profileChangeRequest.setDisplayName(name);
                profileChangeRequest.setPhotoUri(imageUri);

                firebaseUser.updateProfile(profileChangeRequest.build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Image and name updated successfully
                            //Now update the rest

                            Log.i(TAG, "onComplete: photo URL: " + firebaseUser.getPhotoUrl().toString());
                            Log.i(TAG, "onComplete: task: " + task.getResult());
                            updateProfile(name, fatherName, rank, phoneNumber, roomNo);
                        } else {
                            customProgressView.failed("Photo can not be updated\nPlease try again");
                        }
                    }
                });
            }


        }
    };

    private void updateProfile(String name, String fatherName, String rank, String phoneNumber, String roomNo) {
        user.setProfilePicUrl(firebaseUser.getPhotoUrl().toString());
        user.setName(name);
        user.setFatherName(fatherName);
        user.setRank(rank);
        user.setPhoneNumber(phoneNumber);
        user.setRoomNo(roomNo);
        user.setProfileComplete(true);


        databaseReference.child("/Profile/").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    customProgressView.done("Profile Updated");
                    customProgressView.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Log.i(TAG, "onDismiss: called: " + Util.lineOut());
                            startActivity(new Intent(CollectDataActivity.this, Main2Activity.class));
                            finish();
                        }
                    });
                } else {
                    customProgressView.failed(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    private void init() {

        mEtFatherName = findViewById(R.id.et_collect_father_name);
        mEtName = findViewById(R.id.et_collect_name);
        mEtRank = findViewById(R.id.et_collect_rank);
        mEtRoomNo = findViewById(R.id.et_room_no);
        mEtPhoneNumber = findViewById(R.id.et_collect_phone_number);
        mLlLogout = findViewById(R.id.ll_collect_data_logout);
        mCivProfile = findViewById(R.id.civ_profile_pic);
        mBtnContinue = findViewById(R.id.btn_complete_signup);
        mTvChange = findViewById(R.id.tv_collect_data_change);
        mRlHalfCircle = findViewById(R.id.rl_collect_data_half_circle);
        mTvLoggedInWith = findViewById(R.id.tv_logged_in_with);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (customProgressView.isShowing()) customProgressView.dismiss();

        if (requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            mCivProfile.setImageURI(imageUri);
            mTvChange.setText("Change");
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            Toast.makeText(this, "Result code was something else: " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    private void outNewUserMeals(DatabaseReference newUserDatabaseReference) {
        //This method will make the lunch and dinner of new user to false
        newUserDatabaseReference.child("Lunch").setValue(false);
        newUserDatabaseReference.child("Dinner").setValue(false);

    }

}
