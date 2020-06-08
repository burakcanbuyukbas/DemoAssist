package com.burakcanbuyukbas.demoassist;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.burakcanbuyukbas.demoassist.models.Constants;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    Constants.NewsChannel.ID,
                    Constants.NewsChannel.NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel1.setDescription(Constants.NewsChannel.DESCRIPTION);

            NotificationChannel channel2 = new NotificationChannel(
                    Constants.NotificationChannel.ID,
                    Constants.NotificationChannel.NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel2.setDescription(Constants.NotificationChannel.DESCRIPTION);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}