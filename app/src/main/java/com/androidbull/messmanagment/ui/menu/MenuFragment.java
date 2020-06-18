package com.androidbull.messmanagment.ui.menu;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androidbull.messmanagment.R;
import com.androidbull.messmanagment.ui.meal.HomeFragment;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";

    private DatabaseReference menuReference;
    private JSONObject menuObject;
    private List<Menu> menuList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuReference = FirebaseDatabase.getInstance().getReference("/Mess/Menu/");
        menuReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menuObject = new JSONObject((Map) dataSnapshot);

                try {
                    JSONObject sunday = menuObject.getJSONObject("Sunday");
//                    JSONObject sunday = menuObject.getJSONObject("Sunday");
//                    JSONObject sunday = menuObject.getJSONObject("Sunday");
//                    JSONObject sunday = menuObject.getJSONObject("Sunday");
//                    JSONObject sunday = menuObject.getJSONObject("Sunday");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        menuList = new ArrayList<>();
//        for (int i = 0; i < menuObject.length(); i++) {
//            menuList.add(new Menu(menuObject.getString("")));
//        }

    }
}