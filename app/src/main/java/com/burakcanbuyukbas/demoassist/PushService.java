package com.burakcanbuyukbas.demoassist;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import java.util.Map;

public class PushService  extends HmsMessageService {
    private static final String TAG = "PushDemoLog";
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i(TAG, "receive token:" + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG, "onMessageReceived()");

        if(remoteMessage == null){
            Log.e(TAG, "onMessageReceived() remoteMessage is null");
        }

        Map<String, String> notificationData = remoteMessage.getDataOfMap();
        if(notificationData.isEmpty()){
            Log.e(TAG, "onMessageReceived: notification data is empty");
            return;
        }

        int icon = R.mipmap.ic_launcher;
        String title = notificationData.get("title");
        String text = notificationData.get("text");
        String channelId = notificationData.get("channel_id");


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat
                .Builder(this, channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setColor(this.getResources().getColor(R.color.colorPrimary))
                .build();

        notificationManager.notify(1, notification);
    }
}