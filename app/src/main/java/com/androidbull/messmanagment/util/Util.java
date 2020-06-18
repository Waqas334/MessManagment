package com.androidbull.messmanagment.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.androidbull.messmanagment.BuildConfig;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final String TAG = "Util";

    private static String getDayAgainstNumber(int day) {
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 21) {
            day++;
        }
        String dayOfWeek = "";
        switch (day) {
            case Calendar.SUNDAY:
                dayOfWeek = "Sunday";
                //Sunday
                Log.i(TAG, "onCreate: Sunday");
                break;
            case Calendar.MONDAY:
                dayOfWeek = "Monday";

                //Monday
                Log.i(TAG, "onCreate: Monday");

                break;
            case Calendar.TUESDAY:
                dayOfWeek = "Tuesday";

                //Tuesday
                Log.i(TAG, "onCreate: Tuesday");

                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "Wednesday";

                //Wednesday
                Log.i(TAG, "onCreate: wednesday");

                break;
            case Calendar.THURSDAY:
                dayOfWeek = "Thursday";

                //Thursday
                Log.i(TAG, "onCreate: thursday");

                break;
            case Calendar.FRIDAY:
                dayOfWeek = "Friday";

                //Friday
                Log.i(TAG, "onCreate: friday");

                break;
            case Calendar.SATURDAY:
                dayOfWeek = "Saturday";
                //Saturday
                Log.i(TAG, "onCreate: saturday");

                break;
        }
        return dayOfWeek;
    }

    public static String getCurrentDayOfWeek() {
//        String dayOfWeek = "";
//        Calendar mCalender = Calendar.getInstance();
//        int day = mCalender.get(Calendar.DAY_OF_WEEK);
        return getDayAgainstNumber(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

    }

    public static String getNextDayOfWeek() {
        return getDayAgainstNumber(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 1);
    }

    public static String getNextNextDayOfWeek() {
        return getDayAgainstNumber(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 2);
    }


    public static String[] getCurrentDayMeals() {
        String[] meals = new String[2];
        meals[0] = "Lunch";
        meals[1] = "Dinner";
        if (getCurrentDayOfWeek().equals("Sunday")) {
            meals[0] = "Brunch";
        }
        return meals;
    }

    private static String[] getMealsAgainstDayName(String day) {
        String[] meals = new String[2];
        meals[0] = "Lunch";
        meals[1] = "Dinner";
        if (day.equals("Sunday")) {
            meals[0] = "Brunch";
        }
        return meals;
    }

    public static String[] getNextDayMeals() {
        return getMealsAgainstDayName(getNextDayOfWeek());
    }

    public static String[] getNextNextDayMeals() {
        return getMealsAgainstDayName(getNextNextDayOfWeek());
    }


    public static String lineOut() {
        int level = 3;
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String location = traces[level].toString();
        String answer = location.substring(location.indexOf("(") + 1, location.indexOf(")"));


        return (" at " + traces[level] + " ");
    }

    public static void log(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, message + " " + lineOut());
        }
    }


    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static Snackbar showSnackBar(View root, String message, String buttonText, View.OnClickListener buttonClickListener) {
        int DURATION = Snackbar.LENGTH_LONG;
        if (buttonText != null && buttonText.equals("Login")) {
            DURATION = Snackbar.LENGTH_INDEFINITE;
        }
        Snackbar snackbar = Snackbar
                .make(root, message, DURATION)
                .setAction(buttonText, buttonClickListener);

        snackbar.show();
        return snackbar;
    }


    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }

        return false;
    }

}
