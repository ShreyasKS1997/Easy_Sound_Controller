package com.EasySoundController;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class notificationClickReceiver extends BroadcastReceiver {

    NotificationManager notificationManager;
    Dialogs dialogs = new Dialogs();

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (intent.getAction().equals("com.example.soundcon.closeNotification")) {
            notificationManager.cancel(1);
            context.sendBroadcast(new Intent("com.EasySoundController.switch4off"));

        } else if (intent.getAction().equals("com.example.soundcon.home")) {
            Intent intent1 = new Intent(context.getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
            PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent1, PendingIntent.FLAG_IMMUTABLE);
            try {
                pendingIntent.send(context.getApplicationContext(), 0, intent1);
            } catch (PendingIntent.CanceledException e) {
                throw new RuntimeException(e);
            }
        } else if (intent.getAction().equals("com.example.soundcon.Music")) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
        } else if (intent.getAction().equals("com.example.soundcon.NotificationUnmute")) {
            if (notificationManager.isNotificationPolicyAccessGranted() | audioManager.getRingerMode() != 0) {
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL && audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
            } else {
                dialogs.needsDnDPermissionDialog(context, null, true);
            }
        } else if (intent.getAction().equals("com.example.soundcon.silent")) {
            if (notificationManager.isNotificationPolicyAccessGranted()) {
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
            } else {
                dialogs.needsDnDPermissionDialog(context, null, false);
            }
        } else if (intent.getAction().equals("com.example.soundcon.vibrate")) {
            if (notificationManager.isNotificationPolicyAccessGranted() | audioManager.getRingerMode() != 0) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            } else {
                dialogs.needsDnDPermissionDialog(context, null, false);
            }
        }
        dialogs = null;
    }

}
