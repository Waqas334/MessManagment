package com.androidbull.messmanagment;

import android.util.Log;

import androidx.annotation.NonNull;

import com.androidbull.messmanagment.modal.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Map;

public class FirebaseHelper {


    private static final String TAG = "FirebaseHelper";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseDatabaseReference;
    private JSONObject menuJSONObject;

    private FirebaseHelperInterface mFirebaseHelperInterface;

    public void saveClicked(String currentDayOfWeek, boolean isLunch, boolean isDinner) {
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference("/Mess/Students/" + mFirebaseAuth.getCurrentUser().getUid() + "/" + currentDayOfWeek);
        mFirebaseDatabaseReference.child("/Lunch/").setValue(isLunch);
        mFirebaseDatabaseReference.child("/Dinner/").setValue(isDinner);

    }


    FirebaseHelper() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = mFirebaseDatabase.getInstance();
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference();
    }


    public void setmFirebaseHelperInterface(FirebaseHelperInterface mFirebaseHelperInterface) {
        this.mFirebaseHelperInterface = mFirebaseHelperInterface;
    }

    public void downloadCompleteMenu() {
        mFirebaseDatabaseReference.child("/Mess/Menu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menuJSONObject = new JSONObject((Map) dataSnapshot.getValue());
                mFirebaseHelperInterface.menuDownloadComplete(menuJSONObject);
                Log.i(TAG, "getCompleteMenuDatabase onDataChange: " + menuJSONObject.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mFirebaseHelperInterface.menuDownloadFailed(databaseError.getMessage());
                Log.e(TAG, "FirebaseHelper.getCompleteMenuDatabse cancelled: " + databaseError.getMessage());
            }
        });
    }

    boolean isUserLoggedIn() {
        return mFirebaseAuth.getCurrentUser() != null;
    }

    void loginUserAnonymously() {
        mFirebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                    checkIfNewUser(currentUser.getUid());
                } else {
                    Log.e(TAG, "Login Anonymously failed: " + task.getException().getLocalizedMessage());
                }

            }
        });

    }


    private void checkIfNewUser(final String UID) {
        //Checking if user already exists or not
        //If not then set all the meals of whole week to false
        final DatabaseReference userReference = mFirebaseDatabase.getReference("Mess/Students/");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(UID)) {
                    //User created for the very first time
                    Log.i(TAG, "onDataChange: brand new user");
                    outNewUserMeals(userReference.child(UID).child("Sunday"));
                    outNewUserMeals(userReference.child(UID).child("Monday"));
                    outNewUserMeals(userReference.child(UID).child("Tuesday"));
                    outNewUserMeals(userReference.child(UID).child("Wednesday"));
                    outNewUserMeals(userReference.child(UID).child("Thursday"));
                    outNewUserMeals(userReference.child(UID).child("Friday"));
                    outNewUserMeals(userReference.child(UID).child("Saturday"));
                } else {
                    Log.i(TAG, "onDataChange: user already exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void outNewUserMeals(DatabaseReference newUserDatabaseReference) {
        //This method will make the lunch and dinner of new user to false
        newUserDatabaseReference.child("Lunch").setValue(false);
        newUserDatabaseReference.child("Dinner").setValue(false);

    }

    public void getCurrentUserMealState() {
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference("/Mess/Students/" + mFirebaseAuth.getCurrentUser().getUid());
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                JSONObject userMealState = new JSONObject((Map) dataSnapshot.getValue());
                mFirebaseHelperInterface.currentUserMealState(userMealState);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
