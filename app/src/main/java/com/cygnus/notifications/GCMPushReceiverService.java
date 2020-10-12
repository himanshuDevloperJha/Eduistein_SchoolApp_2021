package com.cygnus.notifications;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.cygnus.AttendanceActivity;
import com.cygnus.NotificationActivity;
import com.cygnus.R;
import com.cygnus.ScheduleCkassOnlineList;
import com.cygnus.SplashActivity;
import com.cygnus.StudentDashboardActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import androidx.core.app.NotificationCompat;


/**
 * Created by Belal on 4/15/2016.
 */

//Class is extending GcmListenerService
public class GCMPushReceiverService extends FirebaseMessagingService {
    String title;
    String message;

    //This method will be called on every new message received
   /* @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        String message = data.getString("message");
        //Displaying a notiffication with the message
        sendNotification(message);
    }*/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("msg", "Message from server " + remoteMessage.getFrom());
        title = remoteMessage.getNotification().getTitle();
        message = remoteMessage.getNotification().getBody();


        // Log.e("msg", "onMessageReceivedddd: "+title );
        //   Log.e("msg", "onMessageReceivedddd123: "+message );
        //  Toast.makeText(this, "Title: "+title, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Message: "+message, Toast.LENGTH_SHORT).show();
        sendNotif2();
    }


    private void sendNotif2() {
        NotificationManager mNotificationManager;
        Log.e("msg", "onMessageReceivedddd1234: " + title);
        Log.e("msg", "onMessageReceivedddd12345: " + message);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");

        Intent ii;
//        if (message.contains("Your Attendance for the day has been marked")) {
//            ii = new Intent(getApplicationContext(), AttendanceActivity.class);
//        } else {
            ii = new Intent(getApplicationContext(), NotificationActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setBigContentTitle(title);


        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification() {
      /*  Intent intent = new Intent(this, Login_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
        int requestCode = 0;

        // PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this);
        noBuilder.setColor(getResources().getColor(R.color.red));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            noBuilder.setSmallIcon(R.drawable.applogo);
            noBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.applogo));
        } else {
            noBuilder.setSmallIcon(R.drawable.applogo);
        }
        noBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(sound);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}