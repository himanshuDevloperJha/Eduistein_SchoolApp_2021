package com.cygnus.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.cygnus.R;
import com.cygnus.SplashActivity;

public class ShowNotification extends Service {

    private final static String TAG = "ShowNotification";

    @Override
    public void onCreate() {
        super.onCreate();

       /* Intent mainIntent = new Intent(this, SplashActivity.class);

        NotificationManager notificationManager
                = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification noti = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle("HELLO " + System.currentTimeMillis())
                .setContentText("PLEASE CHECK WE HAVE UPDATED NEWS")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.applogoo)
                .setTicker("ticker message")
                .setWhen(System.currentTimeMillis())
                .build();*/

        int icon = R.drawable.applogoo;
        long when = System.currentTimeMillis();
        String appname = getResources().getString(R.string.app_name);
        NotificationManager notificationManager = (NotificationManager) 
                getSystemService(Context.NOTIFICATION_SERVICE);
        int currentapiVersion = Build.VERSION.SDK_INT;
        Notification notification;
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), SplashActivity.class), 0);

        // To support 2.3 os, we use "Notification" class and 3.0+ os will use
        // "NotificationCompat.Builder" class.

            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    getApplicationContext());
            notification = builder.setContentIntent(contentIntent)
                    .setSmallIcon(icon).setTicker(appname).setWhen(0)
                    .setAutoCancel(true).setContentTitle(appname)
                    .setContentText("New Quiz").build();

            notificationManager.notify((int) when, notification);




        Log.i(TAG, "Notification created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
