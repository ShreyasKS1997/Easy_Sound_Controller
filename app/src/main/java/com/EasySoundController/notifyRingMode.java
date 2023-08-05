package com.EasySoundController;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class notifyRingMode extends Service {

    AudioManager audioManager;
    BroadcastReceiver broadcastReceiver;
    static boolean isRingModeNotifyServiceEnabled = false;
    ImageView silent;
    WindowManager windowManager;
    WindowManager.LayoutParams windowManagerLayoutParams;
    CardView cardView;
    FrameLayout frameLayout;
    int LAYOUT_FLAG;

    BroadcastReceiver stopThisServiceBroadcast;

    public void displayRingModeIcon() {

        int ringerModeType = audioManager.getRingerMode();

        if ((ringerModeType == 0 && audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) | (audioManager.getRingerMode() == 2 && audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0)) {
            silent.setImageResource(R.drawable.notifications_off_35);
        } else if (ringerModeType == 1) {
            silent.setImageResource(R.drawable.vibration_35);
        } else if ((ringerModeType == 2 && audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0)) {
            silent.setImageResource(R.drawable.notifications_on_35);
        }
        silent.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        frameLayout.setVisibility(View.VISIBLE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(400);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(1);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                frameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                frameLayout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setStartOffset(800);
            }
        });

        cardView.setAnimation(alphaAnimation);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        Service service = this;
        new serviceNotificationManage().serviceNotification(getBaseContext(), service, "Esc_Ch_id_3",
                "Ring Mode Change Notify Service", 3, "stopNotifyRingModeChangesService", "Notify Ring Mode Change Service", "Notify Ring Mode Change Service is running", 7);

        stopThisServiceBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopSelf();
                if (Second_Fragment.isSecondFragmentResumed) {
                    sendBroadcast(new Intent("com.EasySoundController.stopNotifyRingModeService"));
                }
            }
        };
        registerReceiver(stopThisServiceBroadcast, new IntentFilter("stopNotifyRingModeChangesService"));

    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManagerLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        frameLayout = new FrameLayout(getBaseContext());
        cardView = new CardView(getBaseContext());
        cardView.setRadius(50);
        frameLayout.setVisibility(View.GONE);
        silent = new ImageView(getBaseContext());
        silent.setPadding(20, 20, 20, 20);
        silent.setBackgroundColor(Color.parseColor("#6701FF"));
        silent.setColorFilter(Color.GREEN);
        cardView.addView(silent);
        frameLayout.addView(cardView);

        windowManagerLayoutParams.y = 0;
        windowManagerLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

        windowManager.addView(frameLayout, windowManagerLayoutParams);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {

                } else {
                    displayRingModeIcon();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION));

        isRingModeNotifyServiceEnabled = true;

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        isRingModeNotifyServiceEnabled = false;
        unregisterReceiver(broadcastReceiver);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            stopForeground(true);
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
        unregisterReceiver(stopThisServiceBroadcast);

    }

}
