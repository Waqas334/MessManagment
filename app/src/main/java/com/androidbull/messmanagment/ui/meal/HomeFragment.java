package com.androidbull.messmanagment.ui.meal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.androidbull.messmanagment.CollectDataActivity;
import com.androidbull.messmanagment.FirebaseHelper;
import com.androidbull.messmanagment.FirebaseHelperInterface;
import com.androidbull.messmanagment.Main2Activity;
import com.androidbull.messmanagment.R;
import com.androidbull.messmanagment.modal.User;
import com.androidbull.messmanagment.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownServiceException;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private TextView mTvDay1;
    private TextView mTvDay1Meal1;
    private TextView mTvDay1Meal2;
    private TextView mTvSave1;
    private CheckBox mCbDay1Meal1;
    private CheckBox mCbDay1Meal2;
    private TextView mTvDay1Meal1Name;
    private TextView mTvDay1Meal2Name;

    private TextView mTvDay2;
    private TextView mTvDay2Meal1;
    private TextView mTvDay2Meal2;
    private TextView mTvSave2;
    private CheckBox mCbDay2Meal1;
    private CheckBox mCbDay2Meal2;
    private TextView mTvDay2Meal1Name;
    private TextView mTvDay2Meal2Name;

    private TextView mTvDay3;
    private TextView mTvDay3Meal1;
    private TextView mTvDay3Meal2;
    private TextView mTvSave3;
    private CheckBox mCbDay3Meal1;
    private CheckBox mCbDay3Meal2;
    private TextView mTvDay3Meal1Name;
    private TextView mTvDay3Meal2Name;

    public static FirebaseHelper mFirebaseHelper;

    private LinearLayout mLlCompleteProfile;

    private String MEAL_1;
    private String MEAL_2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_meal, container, false);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate: in HomeFragment called");
        FirebaseDatabase.getInstance().getReference("/Mess/Students/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Profile/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.i(TAG, "onDataChange: user: " + user);
                if (user != null) {
                    //Profile is not completed
                    if (user.isProfileComplete()) {
                        //Profile is not completed too
                        mLlCompleteProfile.setVisibility(View.GONE);
                    } else {
                        mLlCompleteProfile.setVisibility(View.VISIBLE);
                    }
                } else {
                    mLlCompleteProfile.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFirebaseHelper = Main2Activity.getFirebaseHelper();
        mFirebaseHelper.setmFirebaseHelperInterface(helperInterface);


        initializeViews(view);
        setDays();
        setClickListeners();
        disableCheckBoxes();




    }

    private void disableCheckBoxes() {
        //Students can not change the lunch state after 10AM and Dinner state after 5PM

        int currentHourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHourOfDay >= 10) {
            //It 10AM or more
            //Disable today Lunch CheckBoxes
            mCbDay1Meal1.setEnabled(false);
        }

        if (currentHourOfDay >= 17) {
            //It is 5PM or more
            //Disable today Lunch checkbox
            mCbDay1Meal2.setEnabled(false);
            mTvSave1.setAlpha(0.5f);
        }
    }

    private void setClickListeners() {
        mTvSave1.setOnClickListener(saveDay1ClickListener);
        mTvSave2.setOnClickListener(saveDay2ClickListener);
        mTvSave3.setOnClickListener(saveDay3ClickListener);
        mLlCompleteProfile.setOnClickListener(completeProfileClickListener);
    }


    private FirebaseHelperInterface helperInterface = new FirebaseHelperInterface() {
        @Override
        public void menuDownloadComplete(JSONObject menuJSONObject) {
            Log.w(TAG, "menuDownloadComplete: HomeFragment");
            setMeals(menuJSONObject);
        }

        @Override
        public void menuDownloadFailed(String errorMessage) {
            Log.w(TAG, "menuDownloadFailed: HomeFragment");
            //TODO Add a error dialog to show that menu fetch failed

        }

        @Override
        public void currentUserMealState(JSONObject userMealState) {
            updateUserMealState(userMealState);
        }
    };

    private void updateUserMealState(JSONObject userMealState) {
        try {

            JSONObject currentDayMealState = userMealState.getJSONObject(Util.getCurrentDayOfWeek());
            JSONObject nextDayMealState = userMealState.getJSONObject(Util.getNextDayOfWeek());
            JSONObject nextNextDayMealState = userMealState.getJSONObject(Util.getNextNextDayOfWeek());

            mCbDay1Meal1.setChecked((Boolean) currentDayMealState.get("Lunch"));
            mCbDay1Meal2.setChecked((Boolean) currentDayMealState.get("Dinner"));

            mCbDay2Meal1.setChecked((Boolean) nextDayMealState.get("Lunch"));
            mCbDay2Meal2.setChecked((Boolean) nextDayMealState.get("Dinner"));

            mCbDay3Meal1.setChecked((Boolean) nextNextDayMealState.get("Lunch"));
            mCbDay3Meal2.setChecked((Boolean) nextNextDayMealState.get("Dinner"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setMeals(JSONObject mJsonMenu) {
        if (mFirebaseHelper == null) {
            Log.e(TAG, "setMeals: mFirebaseHelper is null");
            return;
        }
        if (mJsonMenu != null) {
            try {
                JSONObject todayMenu = (JSONObject) mJsonMenu.get(Util.getCurrentDayOfWeek());
                JSONObject nextDayMenu = (JSONObject) mJsonMenu.get(Util.getNextDayOfWeek());
                JSONObject nextNextDayMenu = (JSONObject) mJsonMenu.get(Util.getNextNextDayOfWeek());

                mTvDay1Meal1.setText(todayMenu.getString(Util.getCurrentDayMeals()[0]));
                mTvDay1Meal2.setText(todayMenu.getString(Util.getCurrentDayMeals()[1]));

                mTvDay2Meal1.setText(nextDayMenu.getString(Util.getNextDayMeals()[0]));
                mTvDay2Meal2.setText(nextDayMenu.getString(Util.getNextDayMeals()[1]));

                mTvDay3Meal1.setText(nextNextDayMenu.getString(Util.getNextNextDayMeals()[0]));
                mTvDay3Meal2.setText(nextNextDayMenu.getString(Util.getNextNextDayMeals()[1]));


            } catch (JSONException e) {
                Log.e(TAG, "setMeals: Exception caught: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "setMeals: JSON Object is null");
        }
    }

    private void setDays() {
        mTvDay1.setText(Util.getCurrentDayOfWeek());
        mTvDay2.setText(Util.getNextDayOfWeek());
        mTvDay3.setText(Util.getNextNextDayOfWeek());

        mTvDay1Meal1Name.setText(Util.getCurrentDayMeals()[0]);
        mTvDay1Meal2Name.setText(Util.getCurrentDayMeals()[1]);

        mTvDay2Meal1Name.setText(Util.getNextDayMeals()[0]);
        mTvDay2Meal2Name.setText(Util.getNextDayMeals()[1]);

        mTvDay3Meal1Name.setText(Util.getNextNextDayMeals()[0]);
        mTvDay3Meal2Name.setText(Util.getNextNextDayMeals()[1]);
    }


    private void initializeViews(View view) {


        mTvDay1 = view.findViewById(R.id.tv_day_1);
        mTvDay1Meal1 = view.findViewById(R.id.tv_day_1_meal_1);
        mTvDay1Meal2 = view.findViewById(R.id.tv_day_1_meal_2);
        mTvSave1 = view.findViewById(R.id.tv_save_day_1);
        mCbDay1Meal1 = view.findViewById(R.id.cb_day_1_meal_1);
        mCbDay1Meal2 = view.findViewById(R.id.cb_day_1_meal_2);
        mTvDay1Meal1Name = view.findViewById(R.id.tv_day_1_meal_1_name);
        mTvDay1Meal2Name = view.findViewById(R.id.tv_day_1_meal_2_name);

        mTvDay2 = view.findViewById(R.id.tv_day_2);
        mTvDay2Meal1 = view.findViewById(R.id.tv_day_2_meal_1);
        mTvDay2Meal2 = view.findViewById(R.id.tv_day_2_meal_2);
        mTvSave2 = view.findViewById(R.id.tv_save_day_2);
        mCbDay2Meal1 = view.findViewById(R.id.cb_day_2_meal_1);
        mCbDay2Meal2 = view.findViewById(R.id.cb_day_2_meal_2);
        mTvDay2Meal1Name = view.findViewById(R.id.tv_day_2_meal_1_name);
        mTvDay2Meal2Name = view.findViewById(R.id.tv_day_2_meal_2_name);

        mTvDay3 = view.findViewById(R.id.tv_day_3);
        mTvDay3Meal1 = view.findViewById(R.id.tv_day_3_meal_1);
        mTvDay3Meal2 = view.findViewById(R.id.tv_day_3_meal_2);
        mTvSave3 = view.findViewById(R.id.tv_save_day_3);
        mCbDay3Meal1 = view.findViewById(R.id.cb_day_3_meal_1);
        mCbDay3Meal2 = view.findViewById(R.id.cb_day_3_meal_2);
        mTvDay3Meal1Name = view.findViewById(R.id.tv_day_3_meal_1_name);
        mTvDay3Meal2Name = view.findViewById(R.id.tv_day_3_meal_2_name);

        mLlCompleteProfile = view.findViewById(R.id.ll_complete_profile);
        mLlCompleteProfile.setVisibility(View.GONE);
    }

    private View.OnClickListener completeProfileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getContext(), CollectDataActivity.class));
        }
    };

    private View.OnClickListener saveDay1ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseHelper.saveClicked(Util.getCurrentDayOfWeek(), mCbDay1Meal1.isChecked(), mCbDay1Meal2.isChecked());

        }
    };

    private View.OnClickListener saveDay2ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseHelper.saveClicked(Util.getNextDayOfWeek(), mCbDay2Meal1.isChecked(), mCbDay2Meal2.isChecked());

        }
    };

    private View.OnClickListener saveDay3ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseHelper.saveClicked(Util.getNextNextDayOfWeek(), mCbDay3Meal1.isChecked(), mCbDay3Meal2.isChecked());

        }
    };

}