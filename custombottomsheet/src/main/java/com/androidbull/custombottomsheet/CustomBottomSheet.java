package com.androidbull.custombottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomBottomSheet extends Dialog {

    public CustomBottomSheet(@NonNull Context context) {
        this(context,R.style.CustomTheme);
//        super(context);
        init(context);
    }

    public CustomBottomSheet(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected CustomBottomSheet(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }


    private void init(Context context){

        // Setting dialogview
        Window window = getWindow();
//        window.setGravity(Gravity.CENTER);
//        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        setContentView(R.layout.layout_resource_file);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow()
//                .setLayout(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT
//                );
    }


}
