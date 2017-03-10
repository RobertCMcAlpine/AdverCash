package com.project.level4.adgo.application;

/**
 * Created by Rob on 3/3/17.
 */

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.project.level4.adgo.R;
import com.project.level4.adgo.activities.MainActivity;
import com.project.level4.adgo.utils.Advertisement;

import java.util.List;
import java.util.UUID;

public class AdvercashApplication extends Application {

    private BeaconManager beaconManager;
    private Advertisement ad;

    @Override
    public void onCreate() {
        super.onCreate();

        ad = new Advertisement("Nike", getResources().getDrawable(R.drawable.nikeicon, null),
                "20% Discount in store", getResources().getDrawable(R.drawable.qrcodeicon, null), 0.05);

        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                showNotification(
                        "Advertisement Found",
                        "View advertisement to win ");
            }
            @Override
            public void onExitedRegion(Region region) {
                // could add an "exit" notification too if you want (-:
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region(
                        "monitored ad",
                        UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"),
                        5425, 2658));
            }
        });
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.nikeicon)
                .setContentTitle(title + ": " + ad.getAdOwner())
                .setContentText(message + ad.getReward())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
