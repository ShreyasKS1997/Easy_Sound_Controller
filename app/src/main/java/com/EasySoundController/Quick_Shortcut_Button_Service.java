package com.EasySoundController;

import static android.media.MediaRouter.CALLBACK_FLAG_UNFILTERED_EVENTS;
import static android.media.MediaRouter.ROUTE_TYPE_USER;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Quick_Shortcut_Button_Service extends Service {

    Context context;
    WindowManager windowManager;
    CardView quickShortcutFloatButtonLayout;
    int width;
    int height;
    SharedPreferences sharedPreferencesSettings;
    SharedPreferences sharedPreferencesService;
    AudioManager audioManager;
    int topYLimit;
    int topXLimit;
    ConstraintLayout floatButtonContainer;
    boolean refresh = false;
    String side = "right";

    WindowManager.LayoutParams windowManagerLayoutParams;

    NotificationManager notificationManager;

    int LAYOUT_FLAG;

    private CardView closeButtonCntr;
    private CardView silentButtonCntr;
    private ImageView silentButton;
    private CardView musicButtonCntr;
    private ImageView musicButton;
    private CardView silentWithoutDNDCntr;
    private CardView vibrateButtonCntr;
    private CardView imageViewCntr;
    private CardView normalButtonCntr;
    private CardView homeButtonCntr;

    static boolean isQuickShortcutButtonServiceRunning = false;
    int quickShortcutButtonSize;
    BroadcastReceiver stopThisServiceBroadcast;

    Dialogs dialogs = new Dialogs();

    BroadcastReceiver musicButtonIconToHeadphone;
    ImageView quickShortcutFloatButtonImage;

    ImageView closeButton;
    ImageView mainButton;
    ImageView silentWithoutDNDButton;
    ImageView vibrateButton;
    ImageView normalButton;
    ImageView homeButton;
    LinearLayout viewTopLeft1px;
    LinearLayout viewBottomLeft1px;
    LinearLayout topRight1px;
    LinearLayout bottomRight1px;
    boolean orientationChanged = false;
    int count = 0;
    ViewTreeObserver.OnGlobalLayoutListener screenLayoutOrietationChangeListener;
    MediaRouter mediaRouter;
    MediaRouter.Callback callback;



    @Override
    public void onCreate() {
        super.onCreate();
        isQuickShortcutButtonServiceRunning = true;


        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        sharedPreferencesSettings = getSharedPreferences("SettingsValues", MODE_PRIVATE);
        sharedPreferencesService = getSharedPreferences("quickButtonService", MODE_PRIVATE);

        Service service = this;

        new serviceNotificationManage().serviceNotification(getBaseContext(), service, "Esc_Ch_id_2",
                "Quick Shortcut Floating Button Service", 2, "stopQuickButtonService", "Quick Shortcut Button Service", "Quick Shortcut Button Service is running", 6);

        stopThisServiceBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopSelf();
                if (Second_Fragment.isSecondFragmentResumed) {
                    sendBroadcast(new Intent("com.EasySoundController.stopQuickButtonService"));
                }
            }
        };
        registerReceiver(stopThisServiceBroadcast, new IntentFilter("stopQuickButtonService"));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationChanged = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initLayouts() {
        topRight1px.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                topRight1px.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int[] topLeftViewPosition = new int[2];
                viewTopLeft1px.getLocationOnScreen(topLeftViewPosition);
                int[] bottomLeftViewPosition = new int[2];
                viewBottomLeft1px.getLocationOnScreen(bottomLeftViewPosition);
                int[] topRightViewPosition = new int[2];
                topRight1px.getLocationOnScreen(topRightViewPosition);

                topXLimit = topLeftViewPosition[0];
                topYLimit = topLeftViewPosition[1];

                height = bottomLeftViewPosition[1] - topYLimit;
                width = topRightViewPosition[0] - topXLimit;

                windowManagerLayoutParams.x = width - quickShortcutButtonSize;
                windowManagerLayoutParams.y = (height / 2) - (quickShortcutButtonSize / 2);

                if (!quickShortcutFloatButtonLayout.isAttachedToWindow()) {
                    windowManager.addView(quickShortcutFloatButtonLayout, windowManagerLayoutParams);
                }
            }
        });
    }

    private void updateScreenSizeAndPosition() {
        int[] topView1px = new int[2];
        int[] bottomView1px = new int[2];
        int[] rightTopView1px = new int[2];

        viewTopLeft1px.getLocationOnScreen(topView1px);
        viewBottomLeft1px.getLocationOnScreen(bottomView1px);
        topRight1px.getLocationOnScreen(rightTopView1px);

        topXLimit = topView1px[0];
        topYLimit = topView1px[1];

        int heightNew = bottomView1px[1] - topView1px[1];
        int widthNew = rightTopView1px[0] - topView1px[0];

        if (orientationChanged) {
            windowManagerLayoutParams.x = widthNew - quickShortcutButtonSize;
            windowManagerLayoutParams.y = (heightNew / 2) - (quickShortcutButtonSize / 2);
            if (quickShortcutFloatButtonLayout.isAttachedToWindow()) {
                windowManager.updateViewLayout(quickShortcutFloatButtonLayout, windowManagerLayoutParams);
            }
        }

        height = heightNew;
        width = widthNew;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = getApplicationContext();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        MainActivity.lastVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        callback = new MediaRouter.Callback() {
            @Override
            public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {

            }

            @Override
            public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {

            }

            @Override
            public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info) {

            }

            @Override
            public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo info) {

            }

            @Override
            public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo info) {

            }

            @Override
            public void onRouteGrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group, int index) {

            }

            @Override
            public void onRouteUngrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group) {

            }

            @Override
            public void onRouteVolumeChanged(MediaRouter router, MediaRouter.RouteInfo info) {
                int musicVolume = info.getVolume();
                broadcastAndOtherCommonMethods.setMusicIconWhenVolumeChanged(MainActivity.lastVolume, musicVolume, musicButton);
                MainActivity.lastVolume = musicVolume;
            }
        };

        mediaRouter = (MediaRouter) getSystemService(MEDIA_ROUTER_SERVICE);
        mediaRouter.addCallback(ROUTE_TYPE_USER, callback, CALLBACK_FLAG_UNFILTERED_EVENTS);

        quickShortcutButtonSize = (sharedPreferencesSettings.getInt("quickShortcutButtonSize", 2) * 10) + 75;

        quickShortcutFloatButtonLayout = new CardView(context);
        quickShortcutFloatButtonLayout.setRadius((float)(quickShortcutButtonSize/2));

        quickShortcutFloatButtonImage = new ImageView(context);
        quickShortcutFloatButtonImage.setPadding(10,10,10,10);
        quickShortcutFloatButtonImage.setImageResource(R.drawable.volume_up_35);
        quickShortcutFloatButtonImage.setBackgroundColor(Color.parseColor("#6701FF"));
        quickShortcutFloatButtonImage.setColorFilter(Color.LTGRAY);

        quickShortcutFloatButtonLayout.addView(quickShortcutFloatButtonImage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        LinearLayout.LayoutParams topBottomLeft1pxView = new LinearLayout.LayoutParams(1,1);
        viewTopLeft1px = new LinearLayout(context);
        viewTopLeft1px.setLayoutParams(topBottomLeft1pxView);
        viewBottomLeft1px = new LinearLayout(context);
        viewBottomLeft1px.setLayoutParams(topBottomLeft1pxView);
        topRight1px = new LinearLayout(context);
        topRight1px.setLayoutParams(topBottomLeft1pxView);
        bottomRight1px = new LinearLayout(context);
        bottomRight1px.setLayoutParams(topBottomLeft1pxView);

        screenLayoutOrietationChangeListener = () -> {
            updateScreenSizeAndPosition();
            if (orientationChanged) {
                count += 1;
            }
            if (count == 2) {
                orientationChanged = false;
                count = 0;
            }
        };

        bottomRight1px.getViewTreeObserver().addOnGlobalLayoutListener(screenLayoutOrietationChangeListener);


        WindowManager.LayoutParams topLeft1pxViewLayout = new WindowManager.LayoutParams(1, 1,
                LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        topLeft1pxViewLayout.gravity = Gravity.TOP | Gravity.START;
        WindowManager.LayoutParams bottomLeft1pxViewLayout = new WindowManager.LayoutParams(1, 1,
                LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        bottomLeft1pxViewLayout.gravity = Gravity.BOTTOM | Gravity.START;
        WindowManager.LayoutParams topRight1pxLayout = new WindowManager.LayoutParams(1, 1,
                LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        topRight1pxLayout.gravity = Gravity.TOP | Gravity.END;
        WindowManager.LayoutParams bottomRight1pxLayout = new WindowManager.LayoutParams(1, 1,
                LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        bottomRight1pxLayout.gravity = Gravity.BOTTOM | Gravity.END;

        windowManager.addView(viewTopLeft1px, topLeft1pxViewLayout);
        windowManager.addView(viewBottomLeft1px, bottomLeft1pxViewLayout);
        windowManager.addView(topRight1px, topRight1pxLayout);
        windowManager.addView(bottomRight1px, bottomRight1pxLayout);

        windowManagerLayoutParams = new WindowManager.LayoutParams(quickShortcutButtonSize, quickShortcutButtonSize, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        windowManagerLayoutParams.gravity = Gravity.TOP | Gravity.START;

        initLayouts();

        quickShortcutFloatButtonLayout.setOnTouchListener(new View.OnTouchListener() {

            int startingTouchCoordinateX;
            int startingTouchCoordinateY;
            int startingTouchCoordinateRawX;
            int startingTouchCoordinateRawY;

            boolean touchconsume;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        startingTouchCoordinateX = (int) event.getX();
                        startingTouchCoordinateY = (int) event.getY();
                        startingTouchCoordinateRawX = (int) event.getRawX();
                        startingTouchCoordinateRawY = (int) event.getRawY();
                        touchconsume = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int currentXCord = (int) event.getRawX();
                        int currentYCord = (int) event.getRawY();

                        if (currentXCord != startingTouchCoordinateRawX && currentYCord != startingTouchCoordinateRawY) {

                            windowManagerLayoutParams.x = currentXCord - topXLimit - startingTouchCoordinateX;
                            windowManagerLayoutParams.y = currentYCord - topYLimit - startingTouchCoordinateY;

                            windowManager.updateViewLayout(quickShortcutFloatButtonLayout, windowManagerLayoutParams);
                            touchconsume = true;

                        } else {

                            touchconsume = false;

                        }
                        break;

                    case MotionEvent.ACTION_UP:

                        if (sharedPreferencesSettings.getBoolean("quickButtonStickToSides", false)) {
                            if (windowManagerLayoutParams.x + (quickShortcutButtonSize/2) >= width/2) {
                                windowManagerLayoutParams.x =  width - quickShortcutButtonSize;
                            } else {
                                windowManagerLayoutParams.x = 0;
                            }
                        }

                        if (windowManagerLayoutParams.x <= 0) {
                            windowManagerLayoutParams.x = 0;
                        } else if (windowManagerLayoutParams.x >= width - quickShortcutButtonSize) {
                            windowManagerLayoutParams.x = width - quickShortcutButtonSize;
                        }

                        if (windowManagerLayoutParams.y <= 0) {
                            windowManagerLayoutParams.y = 0;
                        } else if (windowManagerLayoutParams.y >= height - quickShortcutButtonSize) {
                            windowManagerLayoutParams.y = height - quickShortcutButtonSize;
                        }

                        windowManager.updateViewLayout(quickShortcutFloatButtonLayout, windowManagerLayoutParams);
                        break;

                }
                return touchconsume;
            }

        });

        quickShortcutFloatButtonLayout.setOnClickListener(v -> {
            int quickButtonTapAction = sharedPreferencesSettings.getInt("quickShortcutButtonAction", 2);
            switch (quickButtonTapAction) {
                case 0:
                    Intent intent1 = getApplicationContext().getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    break;
                case 1:
                    audioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                    break;
                case 2:
                    showBasicVolumeButtons(windowManagerLayoutParams.x, windowManagerLayoutParams.y, quickShortcutButtonSize);
                    break;
            }
        });

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaRouter.removeCallback(callback);
        mediaRouter = null;

        bottomRight1px.getViewTreeObserver().removeOnGlobalLayoutListener(screenLayoutOrietationChangeListener);

        dialogs = null;

        isQuickShortcutButtonServiceRunning = false;

        if (stopThisServiceBroadcast != null) {
            unregisterReceiver(stopThisServiceBroadcast);
        }
        if (musicButtonIconToHeadphone != null) {
            unregisterReceiver(musicButtonIconToHeadphone);
        }

        sharedPreferencesService.edit().putBoolean("isButtonInit", false).apply();

        if (quickShortcutFloatButtonLayout.isShown()) {
            windowManager.removeView(quickShortcutFloatButtonLayout);
        }
        if (floatButtonContainer != null) {
            if (floatButtonContainer.isShown()) {
                windowManager.removeView(floatButtonContainer);
            }
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            stopForeground(true);
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }

    }


    public void showBasicVolumeButtons(float x, float y, int iconSize) {

        ConstraintLayout.LayoutParams imageViewButtonLayout;

        WindowManager.LayoutParams wlp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
        if (x > (float)((width/2))) {
            wlp.x = (int) (width - (x + iconSize));
            wlp.gravity = Gravity.TOP | Gravity.END;
            if (side.equals("left") && floatButtonContainer != null) {
                refresh = true;
            }
            side = "right";
        } else {
            wlp.x = (int) x;
            wlp.gravity = Gravity.TOP | Gravity.START;
            if (side.equals("right") && floatButtonContainer != null) {
                refresh = true;
            }
            side = "left";
        }
        wlp.y = (int) y - (iconSize * 3);

        if (floatButtonContainer == null | refresh) {

            if (!refresh) {
                floatButtonContainer = new ConstraintLayout(getBaseContext());
                floatButtonContainer.setClickable(false);
                floatButtonContainer.setId(View.generateViewId());

                floatButtonContainer.setOnTouchListener((v, event) -> {

                    switch(event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            if (floatButtonContainer.isAttachedToWindow()) {
                                windowManager.addView(quickShortcutFloatButtonLayout, windowManagerLayoutParams);
                                windowManager.removeView(floatButtonContainer);
                            }
                            break;
                        case MotionEvent.ACTION_OUTSIDE:
                            if (floatButtonContainer.isAttachedToWindow()) {
                                windowManager.addView(quickShortcutFloatButtonLayout, windowManagerLayoutParams);
                                windowManager.removeView(floatButtonContainer);
                            }
                            break;
                    }

                    return true;
                });


                closeButtonCntr = new CardView(getBaseContext());
                closeButton = new ImageView(getBaseContext());
                closeButton.setImageResource(R.drawable.close_35);
                closeButton.setBackgroundColor(Color.parseColor("#6701FF"));
                closeButton.setColorFilter(Color.WHITE);
                closeButton.setPadding(10, 10, 10, 10);
                closeButtonCntr.setId(View.generateViewId());
                closeButtonCntr.addView(closeButton);
                closeButtonCntr.setRadius((float)(iconSize/2));

                musicButtonCntr = new CardView(getBaseContext());
                musicButton = new ImageView(getBaseContext());
                musicButton.setColorFilter(Color.WHITE);
                musicButton.setBackgroundColor(Color.parseColor("#6701FF"));
                musicButton.setPadding(10,10,10,10);
                musicButtonCntr.setId(View.generateViewId());
                musicButtonCntr.addView(musicButton);
                musicButtonCntr.setRadius((float)(iconSize/2));
                musicButtonIconToHeadphone = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intentl) {
                        if (isInitialStickyBroadcast()) {

                        } else {
                            if (musicButton.isShown()) {
                                broadcastAndOtherCommonMethods.setMusicIconWhenLayoutRefreshed(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), musicButton);
                            }
                        }
                    }
                };
                registerReceiver(musicButtonIconToHeadphone, new IntentFilter(Intent.ACTION_HEADSET_PLUG));


                silentWithoutDNDCntr = new CardView(getBaseContext());
                silentWithoutDNDButton = new ImageView(getBaseContext());
                silentWithoutDNDButton.setColorFilter(Color.WHITE);
                silentWithoutDNDButton.setImageResource(R.drawable.notifications_off_35);
                silentWithoutDNDButton.setBackgroundColor(Color.parseColor("#6701FF"));
                silentWithoutDNDButton.setPadding(10,10,10,10);
                silentWithoutDNDCntr.setId(View.generateViewId());
                silentWithoutDNDCntr.addView(silentWithoutDNDButton);
                silentWithoutDNDCntr.setRadius((float)(iconSize/2));


                vibrateButtonCntr = new CardView(getBaseContext());
                vibrateButton = new ImageView(getBaseContext());
                vibrateButton.setColorFilter(Color.WHITE);
                vibrateButton.setImageResource(R.drawable.vibration_35);
                vibrateButton.setBackgroundColor(Color.parseColor("#6701FF"));
                vibrateButton.setPadding(10, 10, 10, 10);
                vibrateButtonCntr.setId(View.generateViewId());
                vibrateButtonCntr.addView(vibrateButton);
                vibrateButtonCntr.setRadius((float)(iconSize/2));

                imageViewCntr = new CardView(getBaseContext());
                mainButton = new ImageView(getBaseContext());
                mainButton.setImageResource(R.drawable.volume_up_35);
                mainButton.setPadding(10, 10, 10, 10);
                mainButton.setColorFilter(Color.parseColor("#6701FF"));
                mainButton.setBackgroundColor(Color.LTGRAY);
                imageViewCntr.setRadius((float)(iconSize/2));
                imageViewCntr.setId(View.generateViewId());
                imageViewCntr.addView(mainButton);

                imageViewCntr.setOnClickListener(v -> {
                    windowManager.addView(quickShortcutFloatButtonLayout, windowManagerLayoutParams);
                    windowManager.removeView(floatButtonContainer);
                });

                normalButtonCntr = new CardView(getBaseContext());
                normalButton = new ImageView(getBaseContext());
                normalButton.setColorFilter(Color.WHITE);
                normalButton.setImageResource(R.drawable.notifications_on_35);
                normalButton.setBackgroundColor(Color.parseColor("#6701FF"));
                normalButton.setPadding(10, 10, 10, 10);
                normalButtonCntr.setId(View.generateViewId());
                normalButtonCntr.addView(normalButton);
                normalButtonCntr.setRadius((float)(iconSize/2));

                silentButtonCntr = new CardView(getBaseContext());
                silentButton = new ImageView(getBaseContext());
                silentButton.setColorFilter(Color.WHITE);
                if (notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_ALL) {
                    silentButton.setImageResource(R.drawable.do_not_disturb_on_35);
                } else {
                    silentButton.setImageResource(R.drawable.baseline_do_not_disturb_off_35);
                }
                silentButton.setBackgroundColor(Color.parseColor("#6701FF"));
                silentButton.setPadding(10, 10, 10, 10);
                silentButtonCntr.setId(View.generateViewId());
                silentButtonCntr.addView(silentButton);
                silentButtonCntr.setRadius((float)(iconSize/2));

                homeButtonCntr = new CardView(getBaseContext());
                homeButton = new ImageView(getBaseContext());
                homeButton.setColorFilter(Color.WHITE);
                homeButton.setImageResource(R.drawable.home_35);
                homeButton.setBackgroundColor(Color.parseColor("#6701FF"));
                homeButton.setPadding(10, 10, 10, 10);
                homeButtonCntr.setId(View.generateViewId());
                homeButtonCntr.addView(homeButton);
                homeButtonCntr.setRadius((float)(iconSize/2));

                closeButtonCntr.setOnClickListener(v -> {
                    stopSelf();
                    windowManager.removeView(floatButtonContainer);
                    if (Second_Fragment.isSecondFragmentResumed) {
                        sendBroadcast(new Intent("com.EasySoundController.stopQuickButtonService"));
                    }
                });

                musicButtonCntr.setOnClickListener(v -> audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI));



                silentButtonCntr.setOnClickListener(v -> {

                    if (notificationManager.isNotificationPolicyAccessGranted()) {
                        if (notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_NONE) {
                            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                            silentButton.setImageResource(R.drawable.do_not_disturb_on_35);
                            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                            }
                        } else {
                            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                            silentButton.setImageResource(R.drawable.baseline_do_not_disturb_off_35);
                        }
                    } else {
                        dialogs.needsDnDPermissionDialog(context, null, false);
                    }

                });

                silentWithoutDNDCntr.setOnClickListener(v -> {
                    if (notificationManager.isNotificationPolicyAccessGranted()) {
                        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                    } else {
                        dialogs.needsDnDPermissionDialog(context, null, true);
                    }
                });

                vibrateButtonCntr.setOnClickListener(v -> {
                    if (notificationManager.isNotificationPolicyAccessGranted() | audioManager.getRingerMode() != 0) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    } else {
                        dialogs.needsDnDPermissionDialog(context, null, true);
                    }
                });

                normalButtonCntr.setOnClickListener(v -> {
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

                homeButtonCntr.setOnClickListener(v -> {
                    if (!MainActivity.isMainActivityRunning) {
                        Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });


                floatButtonContainer.addView(closeButtonCntr);
                floatButtonContainer.addView(musicButtonCntr);
                floatButtonContainer.addView(silentButtonCntr);
                floatButtonContainer.addView(silentWithoutDNDCntr);
                floatButtonContainer.addView(vibrateButtonCntr);
                floatButtonContainer.addView(imageViewCntr);
                floatButtonContainer.addView(normalButtonCntr);
                floatButtonContainer.addView(homeButtonCntr);

            }
            refresh = false;
            ConstraintLayout.LayoutParams closeButtonLayout = new ConstraintLayout.LayoutParams(iconSize, iconSize);
            closeButtonLayout.topToTop = floatButtonContainer.getId();
            closeButtonLayout.leftToLeft = imageViewCntr.getId();
            closeButtonLayout.rightToRight = imageViewCntr.getId();
            closeButtonCntr.setLayoutParams(closeButtonLayout);

            ConstraintLayout.LayoutParams musicButtonLayout = new ConstraintLayout.LayoutParams(iconSize, iconSize);
            musicButtonLayout.topToBottom = closeButtonCntr.getId();
            musicButtonLayout.bottomToTop = silentWithoutDNDCntr.getId();
            if (x > (float)(width/2)) {
                musicButtonLayout.leftToRight = silentWithoutDNDCntr.getId();
                musicButtonLayout.rightToLeft = closeButtonCntr.getId();
            } else {
                musicButtonLayout.rightToLeft = silentWithoutDNDCntr.getId();
                musicButtonLayout.leftToRight = closeButtonCntr.getId();
            }
            musicButtonCntr.setLayoutParams(musicButtonLayout);

            ConstraintLayout.LayoutParams silentWithoutDNDButtonLayout = new ConstraintLayout.LayoutParams(iconSize, iconSize);
            silentWithoutDNDButtonLayout.topToBottom = musicButtonCntr.getId();
            silentWithoutDNDButtonLayout.bottomToTop = vibrateButtonCntr.getId();
            if (x > (float)(width/2)) {
                silentWithoutDNDButtonLayout.rightToLeft = closeButtonCntr.getId();
                silentWithoutDNDButtonLayout.rightMargin = 40;
            } else {
                silentWithoutDNDButtonLayout.leftToRight = closeButtonCntr.getId();
                silentWithoutDNDButtonLayout.leftMargin = 40;
            }
            silentWithoutDNDCntr.setLayoutParams(silentWithoutDNDButtonLayout);


            ConstraintLayout.LayoutParams vibrateButtonLayout = new ConstraintLayout.LayoutParams(iconSize, iconSize);
            vibrateButtonLayout.topToTop = imageViewCntr.getId();
            vibrateButtonLayout.bottomToBottom = imageViewCntr.getId();
            if (x > (float)(width/2)) {
                vibrateButtonLayout.leftToLeft = floatButtonContainer.getId();
                vibrateButtonLayout.leftMargin = 30;
            } else {
                vibrateButtonLayout.rightToRight = floatButtonContainer.getId();
                vibrateButtonLayout.rightMargin = 30;
            }
            vibrateButtonCntr.setLayoutParams(vibrateButtonLayout);

            imageViewButtonLayout = new ConstraintLayout.LayoutParams(iconSize, iconSize);
            imageViewButtonLayout.topToBottom = closeButtonCntr.getId();
            imageViewButtonLayout.bottomToTop = homeButtonCntr.getId();
            if (x > (float)(width/2)) {
                imageViewButtonLayout.leftToRight = vibrateButtonCntr.getId();
                imageViewButtonLayout.leftMargin = 100;
            } else {
                imageViewButtonLayout.rightToLeft = vibrateButtonCntr.getId();
                imageViewButtonLayout.rightMargin = 100;
            }
            imageViewCntr.setLayoutParams(imageViewButtonLayout);

            ConstraintLayout.LayoutParams normalButtonLayout = new ConstraintLayout.LayoutParams(iconSize, iconSize);
            normalButtonLayout.topToBottom = vibrateButtonCntr.getId();
            normalButtonLayout.bottomToTop = silentButtonCntr.getId();
            if (x > (float)(width/2)) {
                normalButtonLayout.rightToLeft = homeButtonCntr.getId();
                normalButtonLayout.rightMargin = 40;
            } else {
                normalButtonLayout.leftToRight = homeButtonCntr.getId();
                normalButtonLayout.leftMargin = 40;
            }
            normalButtonCntr.setLayoutParams(normalButtonLayout);

            ConstraintLayout.LayoutParams silentButtonLayout = new ConstraintLayout.LayoutParams(iconSize, iconSize);
            silentButtonLayout.topToBottom = normalButtonCntr.getId();
            silentButtonLayout.bottomToTop = homeButtonCntr.getId();
            if (x > (float)(width/2)) {
                silentButtonLayout.leftToRight = normalButtonCntr.getId();
                silentButtonLayout.rightToLeft = imageViewCntr.getId();
            } else {
                silentButtonLayout.rightToLeft = normalButtonCntr.getId();
                silentButtonLayout.leftToRight = imageViewCntr.getId();
            }
            silentButtonCntr.setLayoutParams(silentButtonLayout);

            ConstraintLayout.LayoutParams homeButtonLayout = new ConstraintLayout.LayoutParams(iconSize, iconSize);
            homeButtonLayout.bottomToBottom = floatButtonContainer.getId();
            homeButtonLayout.leftToLeft = imageViewCntr.getId();
            homeButtonLayout.rightToRight = imageViewCntr.getId();
            homeButtonCntr.setLayoutParams(homeButtonLayout);



            boolean fbcAttached = floatButtonContainer.isAttachedToWindow();

            if (fbcAttached) {
                windowManager.updateViewLayout(floatButtonContainer, wlp);
            } else {
                windowManager.addView(floatButtonContainer, wlp);
            }
            broadcastAndOtherCommonMethods.setMusicIconWhenLayoutRefreshed(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), musicButton);

        } else {
            if (notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_ALL) {
                silentButton.setImageResource(R.drawable.do_not_disturb_on_35);
            } else {
                silentButton.setImageResource(R.drawable.baseline_do_not_disturb_off_35);
            }

            WindowManager.LayoutParams fbcUpdate = (WindowManager.LayoutParams) floatButtonContainer.getLayoutParams();
            fbcUpdate.y = (int) y - (iconSize * 3);
            if (x > (float) (width / 2)) {
                fbcUpdate.x = (int) (width - (x + iconSize));
                fbcUpdate.gravity = Gravity.TOP | Gravity.END;
                if (side.equals("left")) {
                    refresh = true;
                    showBasicVolumeButtons(x, y, iconSize);
                }
            } else {

                fbcUpdate.gravity = Gravity.TOP | Gravity.START;
                fbcUpdate.x = (int) x;
                if (side.equals("right")) {
                    refresh = true;
                    showBasicVolumeButtons(x, y, iconSize);
                }
            }
            windowManager.addView(floatButtonContainer, fbcUpdate);
            broadcastAndOtherCommonMethods.setMusicIconWhenLayoutRefreshed(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), musicButton);
        }
        windowManager.removeView(quickShortcutFloatButtonLayout);
    }




}
