package com.androidbull.messmanagment.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CustomAnimation extends Animation {

    int targetWidth;
    View view;

    public CustomAnimation(Context context, AttributeSet attrs, int targetHeight, View view) {
        super(context, attrs);
        this.targetWidth = targetHeight;
        this.view = view;

    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
//        super.applyTransformation(interpolatedTime, t);
        view.getLayoutParams().width = targetWidth;
        view.requestLayout();
    }


    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
