package com.moutamid.radiostreamapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

public class RadioForegroundService extends Service {
    private static final String CHANNEL_ID = "RadioServiceChannel";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_STOP = "ACTION_STOP";
    private ExoPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        player = new ExoPlayer.Builder(this).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_PLAY.equals(action)) {
            startPlaying();
        } else if (ACTION_STOP.equals(action)) {
            stopPlaying();
        }
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Radio is playing in the background")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    private void startPlaying() {
        if (player != null && !player.isPlaying()) {
            MediaItem mediaItem = MediaItem.fromUri("https://a2.asurahosting.com:6350/radio.aac");
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
    }

    private void stopPlaying() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
