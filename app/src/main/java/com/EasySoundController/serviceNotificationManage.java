package com.EasySoundController;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class serviceNotificationManage {
    public void serviceNotification(Context context, Service service, String channelId, String overrideNotificationTextMessage,
                                    int foregroundNotificationId, String actionIntentServiceName, String notificationTitle,
                                    String notificationMessage, int requestCode) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);
        notificationBuilder.setGroup("easySoundControllerDismissableNotification");
        notificationBuilder.setSmallIcon(R.drawable.volume_up_35);
        notificationBuilder.setOngoing(false);

        Intent notificationChannelIntent;

        String packageName = context.getPackageName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannelIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            notificationChannelIntent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName);
            notificationChannelIntent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);

            RemoteViews remoteViews = new RemoteViews(packageName, R.layout.foreground_notification_layout);
            remoteViews.setTextViewText(R.id.foregroundNotificationText, overrideNotificationTextMessage);
            PendingIntent stopServiceButtonIntent = PendingIntent.getBroadcast(context, requestCode + 2, new Intent(actionIntentServiceName), PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.button3, stopServiceButtonIntent);
            PendingIntent removeNotificationPendingIntent = PendingIntent.getActivity(context, requestCode, notificationChannelIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.button4, removeNotificationPendingIntent);

            notificationBuilder.setCustomBigContentView(remoteViews);
            notificationBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
            Notification makeThisServiceForegroundNotification = notificationBuilder.build();
            makeThisServiceForegroundNotification.flags = NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE;
            service.startForeground(foregroundNotificationId, makeThisServiceForegroundNotification);

        } else {
            notificationChannelIntent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
            notificationChannelIntent.putExtra("app_package", packageName);
            notificationChannelIntent.putExtra("app_uid", context.getApplicationInfo().uid);

            notificationBuilder.setContentTitle(notificationTitle);
            notificationBuilder.setContentText(notificationMessage);
            Notification makeThisServiceForegroundNotification = notificationBuilder.build();
            makeThisServiceForegroundNotification.flags = NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE;
            service.startForeground(foregroundNotificationId, makeThisServiceForegroundNotification);
        }
    }
}
