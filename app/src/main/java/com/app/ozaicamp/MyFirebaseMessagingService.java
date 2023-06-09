package com.app.ozaicamp;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("######TAG", "Refreshed token: " + token);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("fcmToken", token);
        editor.apply();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
       // sendRegistrationToServer(token);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
       // Log.d("########", "Message data payload: " + remoteMessage.getData());
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            if (!TextUtils.isEmpty(pref.getString("userName", ""))&&!pref.getString("userName", "").equals("null"))  {

                // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
                //Log.d(TAG, "From: " + remoteMessage.getFrom());
                Log.d("########", "Message data payload: " + remoteMessage.getData());
                final Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("link", remoteMessage.getData().get("link"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int notificationID = new Random().nextInt(3000);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    setupChannels(notificationManager);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                }
                else {
                    pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                }

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher);

                Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(getNotificationIcon())
//                        .setContentTitle(remoteMessage.getData().get("title"))
//                        .setContentText(remoteMessage.getData().get("body"))
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(notificationSoundUri)
                        .setContentIntent(pendingIntent);

                //Set notification color to match your app color template
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.purple_500));
                }
                notificationManager.notify(notificationID, notificationBuilder.build());
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "onMessageReceived error: " + e.getMessage());
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager) {
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to device notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        adminChannel.setVibrationPattern(new long[]{0, 0, 0, 0,0, 0, 0, 0, 0});
        adminChannel.setShowBadge(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }



    private boolean isAppOnForeground(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private int getNotificationIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return R.mipmap.ic_launcher;
        else
            return R.mipmap.ic_launcher;
    }
}
