package com.androidbull.messmanagment;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONObject;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "Main2Activity";
    private FirebaseHelper mFirebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_testing);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);


    }

    public static FirebaseHelper getFirebaseHelper() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (!firebaseHelper.isUserLoggedIn()) {
            firebaseHelper.loginUserAnonymously();
        }
        firebaseHelper.downloadCompleteMenu();
        firebaseHelper.getCurrentUserMealState();
        return firebaseHelper;
    }
}
