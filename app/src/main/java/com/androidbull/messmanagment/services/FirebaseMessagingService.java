package com.androidbull.messmanagment.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.androidbull.messmanagment.MainActivity;
import com.androidbull.messmanagment.R;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMEssagingServic";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: Name: " + remoteMessage.getData().get("title"));

            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }

        } else {
            Log.i(TAG, "onMessageReceived: size is ZERO");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());


        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


    }

    private void showNotification(String title, String body) {

        String DailNotificationChannelId = "daily_notification";
        NotificationCompat.Builder builder;
        NotificationChannel defaultChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultChannel = new NotificationChannel(DailNotificationChannelId, "Daily lesson Notifications", NotificationManager.IMPORTANCE_HIGH);
            defaultChannel.setDescription("Daily notifications helps you learn new english content");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(defaultChannel);
        }

        //Pending Intent when notification closed
        Intent delIntent = new Intent(this, MainActivity.class);
        PendingIntent delPendingIntent = PendingIntent.getActivity(this, 0, delIntent, 0);

        builder = new NotificationCompat.Builder(this, DailNotificationChannelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(delPendingIntent)
                .setDeleteIntent(delPendingIntent)
                //autocancel will remove the notification when tapped
                .setAutoCancel(true)

                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1000, builder.build());
    }


}
