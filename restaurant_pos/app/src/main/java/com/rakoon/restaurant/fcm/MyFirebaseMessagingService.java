package com.rakoon.restaurant.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rakoon.restaurant.AllPath;
import com.rakoon.restaurant.DashBoardActivity2;
import com.rakoon.restaurant.R;

import java.util.Date;

/**
 * Created by AnaadIT on 3/30/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    MediaPlayer mp;

    @Override
    public void handleIntent(Intent intent) {
        Log.e(TAG, "From: " + "========================" + "");

        String title = "", msg = "";

        if (intent.hasExtra("gcm.notification.title"))
            title = intent.getStringExtra("gcm.notification.title");

        if (intent.hasExtra("gcm.notification.body"))
            msg = intent.getStringExtra("gcm.notification.body");


        Log.e(TAG, "From: " + "========================" + title);
        Log.e(TAG, "Notification Message Body:================= " + msg);

        if (!title.equalsIgnoreCase("") && !msg.equalsIgnoreCase("")) {
            // sendNotification(title, msg);
            drawNotification(title, msg);
        }
        //super.handleIntent(intent);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    }

    private void sendNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(AllPath.CHANNEL_ID, AllPath.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(AllPath.CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Intent intent = null;

        if (title.equalsIgnoreCase("Request for feedback")) {
            Log.e(TAG, " Message Title123456: " + "7");
            intent = new Intent(getApplicationContext(), DashBoardActivity2.class);
            intent.putExtra("nid", "7");
        } else {
            Log.e(TAG, " Message Title123456: " + "0");
            intent = new Intent(this, DashBoardActivity2.class);
            intent.putExtra("nid", "0");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(body);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_signin)
                .setStyle(bigText)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(uri)
                .setContentIntent(pendingIntent)
                .setContentText(body);
        Log.e("Here 1", " Message Title123456: " + "0");
        Notification notification = notificationBuilder.build();
        int i = (int) (new Date().getTime() / 1000);
        notificationManager.notify(i, notification);
        if (title.equalsIgnoreCase("Request for feedback")) {
            Intent broadcast = new Intent();
            broadcast.setAction("OPEN_NEW_ACTIVITY");
            broadcast.putExtra("status", "9");
            sendBroadcast(broadcast);
        }
        Log.e("Here 2", " Message Title123456: " + "0");
    }

    void drawNotification(String title, String body) {
        NotificationManagerCompat notificationManager1 =NotificationManagerCompat.from(this);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(body);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(AllPath.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, DashBoardActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, AllPath.CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_signin)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        int i = (int) (new Date().getTime() / 1000);
        notificationManager1.notify(i, builder.build());
    }
}