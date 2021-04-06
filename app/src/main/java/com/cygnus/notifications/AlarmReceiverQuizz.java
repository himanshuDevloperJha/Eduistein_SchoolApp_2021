package com.cygnus.notifications;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.cygnus.R;
import com.cygnus.SplashActivity;

public class AlarmReceiverQuizz extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.applogo)
                .setContentTitle("Eduistein")
                .setContentText("Quiz Time\nTime to Challenge your friends,sharpen your mind, Learn and much more in just 10 minutes!!")
                .setWhen(when)
                .setContentIntent(pendingIntent);
        notificationManager.notify((int) when, mNotifyBuilder.build());

    }

}