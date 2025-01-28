package com.example.messapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create an Intent to open the app when the notification is clicked
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification with the default system icon
        Notification notification = new NotificationCompat.Builder(context, "DINNER_CHANNEL")
                .setContentTitle("Do you want to have dinner?")
                .setContentText("It's 6 PM, time to decide if you want to have your dinner.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)  // Using default system icon
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        // Get the NotificationManager system service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Show the notification
        notificationManager.notify(1, notification);
    }
}
