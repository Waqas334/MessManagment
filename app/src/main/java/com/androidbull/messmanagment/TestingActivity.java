package com.androidbull.messmanagment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.os.Handler;
import android.print.PageRange;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidbull.messmanagment.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TestingActivity extends AppCompatActivity {
    private View view;

    private RelativeLayout mRlDay1, mRlDay2;
    private LinearLayout mLlDay1, mLlDay2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        mRlDay1 = findViewById(R.id.rl_day_1);
        mRlDay2 = findViewById(R.id.rl_day_2);
        mLlDay1 = findViewById(R.id.ll_texts);
        mLlDay2 = findViewById(R.id.ll_texts_2);

        mRlDay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLlDay1.getVisibility() == View.GONE) {
                    mLlDay1.setVisibility(View.VISIBLE);
                } else {
                    mLlDay1.setVisibility(View.GONE);

                }
            }
        });

        mRlDay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLlDay2.getVisibility() == View.GONE) {
                    mLlDay2.setVisibility(View.VISIBLE);
                } else {
                    mLlDay2.setVisibility(View.GONE);

                }
            }
        });
    }

}
