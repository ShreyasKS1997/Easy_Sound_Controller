package com.EasySoundController;


import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaRouter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.media.AudioManager;
import android.widget.SeekBar;
import android.widget.TextView;

public class AudioData {

    int lastMusicVolume;
    MediaRouter.Callback callback;

    public void setupCallback(SeekBar seekBar, ImageView imageViewMediaVolume) {
        callback = new MediaRouter.Callback() {
            @Override
            public void onRouteSelected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
                mediaRouter.getCategoryCount();
            }

            @Override
            public void onRouteUnselected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {

            }

            @Override
            public void onRouteAdded(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {

            }

            @Override
            public void onRouteRemoved(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {

            }

            @Override
            public void onRouteChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {

            }

            @Override
            public void onRouteGrouped(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo, MediaRouter.RouteGroup routeGroup, int i) {

            }

            @Override
            public void onRouteUngrouped(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo, MediaRouter.RouteGroup routeGroup) {

            }

            @Override
            public void onRouteVolumeChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
                int currentMusicVolume = routeInfo.getVolume();
                seekBar.setProgress(currentMusicVolume);
                if ((lastMusicVolume == 0 && currentMusicVolume >= 1) | (lastMusicVolume >= 1 && currentMusicVolume == 0)) {
                    if (routeInfo.getVolume() <= 0) {
                        imageViewMediaVolume.setImageResource(R.drawable.music_off_35);
                    } else {
                        switch (MainActivity.audioOutputType) {
                            case "headphone":
                                imageViewMediaVolume.setImageResource(R.drawable.headphones_35);
                                break;
                            case "headset":
                                imageViewMediaVolume.setImageResource(R.drawable.headset_mic_35);
                                break;
                            case "bluetoothHeadset":
                                imageViewMediaVolume.setImageResource(R.drawable.bluetooth_audio_35);
                                break;
                            default:
                                imageViewMediaVolume.setImageResource(R.drawable.music_on_35);
                                break;
                        }
                    }
                }
                lastMusicVolume = currentMusicVolume;
            }
        };
    }

    public void volumeMedia(Context context, AudioManager audioManager, SeekBar seekBar, TextView textViewPercent) {
        int currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        lastMusicVolume = currentMusicVolume;

        seekBar.setProgress(currentMusicVolume);
        seekBar.setMax(maxMusicVolume);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
                String progressInPercent = (100 * progress) / maxMusicVolume + "%";
                textViewPercent.setText(progressInPercent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textViewPercent.setText(context.getString(R.string.textViewPercent, ((100 * audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) / maxMusicVolume)));
    }

    public void volumeCall(AudioManager audioManager, SeekBar seekBar2, TextView textViewPercent2, Dialogs dialogs, Context context) {

        int maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        int currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

        seekBar2.setMax(maxMusicVolume);
        seekBar2.setProgress(currentMusicVolume);

        textViewPercent2.setText(context.getString(R.string.textViewPercent, ((100 * currentMusicVolume) / maxMusicVolume)));

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, progress, 0);
                    if (progress == 0 && audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) >= 1) {
                        dialogs.CallOrAlarmCannotBeZeroDialog(context, seekBar2, audioManager, AudioManager.STREAM_VOICE_CALL);
                    }
                }
                String s = context.getString(R.string.textViewPercent, ((100 * audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)) / maxMusicVolume));
                textViewPercent2.setText(s);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void volumeRing(AudioManager audioManager, SeekBar seekBar3, TextView textViewPercent3, ImageView imageViewRingVolume,
                           NotificationManager notificationManager, Dialogs dialogs, Context context, SeekBar seekBar5, SeekBar seekBar6,
                           SeekBar seekBar7) {

        int maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        int currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        seekBar3.setMax(maxMusicVolume);
        seekBar3.setProgress(currentMusicVolume);

        int ringerMode = audioManager.getRingerMode();
        if (ringerMode == 0 | (currentMusicVolume == 0 && ringerMode != 1)) {
            imageViewRingVolume.setImageResource(R.drawable.notifications_off_35);
        } else if (ringerMode == 1) {
            imageViewRingVolume.setImageResource(R.drawable.vibration_35);
        } else if (ringerMode == 2) {
            imageViewRingVolume.setImageResource(R.drawable.notifications_on_35);
        }

        textViewPercent3.setText(context.getString(R.string.textViewPercent, ((100 * currentMusicVolume) / maxMusicVolume)));

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (audioManager.getRingerMode() != 1 && audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) {
                        if (notificationManager.isNotificationPolicyAccessGranted()) {
                            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            }
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
                        } else {
                            seekBar.setEnabled(false);
                            dialogs.needsDnDPermissionDialog(context, seekBar3, false);
                            seekBar3.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
                        }
                    } else {
                        try {
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
                        } catch (SecurityException securityException) {
                            seekBar.setEnabled(false);
                            dialogs.needsDnDPermissionDialog(context, seekBar3, false);
                            seekBar3.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
                        }
                    }
                }
                textViewPercent3.setText(context.getString(R.string.textViewPercent, ((100 * audioManager.getStreamVolume(AudioManager.STREAM_RING)) / maxMusicVolume)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar5.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                seekBar6.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                seekBar7.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_DTMF));
            }
        });
    }

    public void volumeAlarm(AudioManager audioManager, SeekBar seekBar4, TextView textViewPercent4, Dialogs dialogs, Context context) {

        int maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        int currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

        seekBar4.setMax(maxMusicVolume);
        seekBar4.setProgress(currentMusicVolume);
        seekBar4.performClick();

        textViewPercent4.setText(context.getString(R.string.textViewPercent, ((100 * currentMusicVolume) / maxMusicVolume)));

        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
                    if (progress == 0 && audioManager.getStreamVolume(AudioManager.STREAM_ALARM) >= 1) {
                        dialogs.CallOrAlarmCannotBeZeroDialog(context, seekBar4, audioManager, AudioManager.STREAM_ALARM);
                    }
                }
                textViewPercent4.setText(context.getString(R.string.textViewPercent, ((100 * audioManager.getStreamVolume(AudioManager.STREAM_ALARM)) / maxMusicVolume)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void volumeNotification(AudioManager audioManager, SeekBar seekBar3, TextView textViewPercent5, NotificationManager notificationManager,
                                   Dialogs dialogs, Context context, SeekBar seekBar5, SeekBar seekBar6,
                                   SeekBar seekBar7) {

        int maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        int currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        seekBar5.setMax(maxMusicVolume);
        seekBar5.setProgress(currentMusicVolume);

        textViewPercent5.setText(context.getString(R.string.textViewPercent, ((100 * currentMusicVolume) / maxMusicVolume)));

        seekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (audioManager.getRingerMode() != 1 && audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) {
                        if (notificationManager.isNotificationPolicyAccessGranted()) {
                            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, progress, 0);
                        } else {
                            seekBar.setEnabled(false);
                            dialogs.needsDnDPermissionDialog(context, seekBar5, false);
                            seekBar5.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                        }
                    } else {
                        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, progress, 0);
                    }
                    if (audioManager.getRingerMode() == 1 | (audioManager.getRingerMode() != 1 &&
                            audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) | audioManager.getRingerMode() == 0) {
                        if (progress > 0) {
                            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI);
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }
                    }
                }
                textViewPercent5.setText(context.getString(R.string.textViewPercent, ((100 * audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)) / maxMusicVolume)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar3.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
                seekBar6.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                seekBar7.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_DTMF));
            }
        });
    }

    public void volumeSystem(AudioManager audioManager, SeekBar seekBar3, TextView textViewPercent6, NotificationManager notificationManager,
                             Dialogs dialogs, Context context, SeekBar seekBar5, SeekBar seekBar6,
                             SeekBar seekBar7) {

        int maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        int currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);

        seekBar6.setMax(maxMusicVolume);
        seekBar6.setProgress(currentMusicVolume);

        textViewPercent6.setText(context.getString(R.string.textViewPercent, ((100 * currentMusicVolume) / maxMusicVolume)));

        seekBar6.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (audioManager.getRingerMode() != 1 && audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) {
                        if (notificationManager.isNotificationPolicyAccessGranted()) {
                            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
                        } else {
                            seekBar.setEnabled(false);
                            dialogs.needsDnDPermissionDialog(context, seekBar6, false);
                            seekBar6.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                        }
                    } else {
                        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
                    }
                    if (audioManager.getRingerMode() == 1 | (audioManager.getRingerMode() == 2 &&
                            audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) | audioManager.getRingerMode() == 0) {
                        if (progress > 0) {
                            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI);
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }
                    }
                }
                textViewPercent6.setText(context.getString(R.string.textViewPercent, ((100 * audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)) / maxMusicVolume)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar3.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
                seekBar5.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                seekBar7.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_DTMF));
            }
        });
    }

    public void volumeDTMF(AudioManager audioManager, SeekBar seekBar3, TextView textViewPercent7, NotificationManager notificationManager,
                           Dialogs dialogs, Context context, SeekBar seekBar5, SeekBar seekBar6,
                           SeekBar seekBar7) {

        int maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF);
        int currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_DTMF);

        seekBar7.setMax(maxMusicVolume);
        seekBar7.setProgress(currentMusicVolume);

        textViewPercent7.setText(context.getString(R.string.textViewPercent, ((100 * currentMusicVolume) / maxMusicVolume)));

        seekBar7.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (audioManager.getRingerMode() != 1 && audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) {
                        if (notificationManager.isNotificationPolicyAccessGranted()) {
                            audioManager.setStreamVolume(AudioManager.STREAM_DTMF, progress, 0);
                        } else {
                            seekBar.setEnabled(false);
                            dialogs.needsDnDPermissionDialog(context, seekBar7, false);
                            seekBar7.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_DTMF));
                        }
                    } else {
                        audioManager.setStreamVolume(AudioManager.STREAM_DTMF, progress, 0);
                    }
                }
                textViewPercent7.setText(context.getString(R.string.textViewPercent, ((100 * audioManager.getStreamVolume(AudioManager.STREAM_DTMF)) / maxMusicVolume)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar3.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
                seekBar5.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                seekBar6.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
            }
        });
    }

    public void DnDButtonFF(NotificationManager notificationManager, ImageButton FFimageView, AudioManager audioManager, Dialogs dialogs, Context context) {
            if (notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_ALL) {
                FFimageView.setImageResource(R.drawable.do_not_disturb_on_35);
            } else {
                FFimageView.setImageResource(R.drawable.baseline_do_not_disturb_off_35);
            }
            FFimageView.setOnClickListener(v -> {
                if (notificationManager.isNotificationPolicyAccessGranted()) {
                    if (notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_NONE) {
                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                        FFimageView.setImageResource(R.drawable.do_not_disturb_on_35);
                        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                        }
                    } else {
                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                        FFimageView.setImageResource(R.drawable.baseline_do_not_disturb_off_35);
                    }
                } else {
                    dialogs.needsDnDPermissionDialog(context, null, false);
                }
            });
    }

    public void silentButtonFF(AudioManager audioManager, ImageButton FFimageView2, NotificationManager notificationManager, Dialogs dialogs, Context context) {
            FFimageView2.setOnClickListener(v -> {
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
            });
    }

    public void vibrateButtonFF(AudioManager audioManager, NotificationManager notificationManager, ImageButton FFimageView3, Dialogs dialogs, Context context) {
        FFimageView3.setOnClickListener(v -> {
            if (notificationManager.isNotificationPolicyAccessGranted() | audioManager.getRingerMode() != 0) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            } else {
                dialogs.needsDnDPermissionDialog(context, null, false);
            }
        });
    }

    public void normalRingVolumeButtonFF(AudioManager audioManager, NotificationManager notificationManager, ImageButton FFimageView4, Dialogs dialogs, Context context) {
        FFimageView4.setOnClickListener(v -> {
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
        });
    }

    public void musicButtonFF(AudioManager audioManager, ImageButton FFimageView5) {
        FFimageView5.setOnClickListener(v -> audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0));
    }

}
