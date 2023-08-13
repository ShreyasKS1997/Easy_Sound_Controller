package com.EasySoundController;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RemoteViews;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.EasySoundController.databinding.SecondFragmentBinding;

public class Second_Fragment extends Fragment {

    static boolean isSecondFragmentResumed;
    private SecondFragmentBinding binding;
    Dialogs dialogs = new Dialogs();
    Context context;
    SharedPreferences sharedPreferences;
    NotificationManager notificationManager;
    private BroadcastReceiver stopQuickButtonService;
    private BroadcastReceiver stopNotifyRingModeChangeSwitchOff;
    private BroadcastReceiver enableNotificationSwitchOff;
    SettingsData settingsData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stopQuickButtonService = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {

                } else {
                    if (isAdded()) {
                        binding.switch1.setChecked(false);
                        if (binding.quickButtonSize.getVisibility() == View.VISIBLE) {
                            binding.quickButtonSize.setVisibility(View.GONE);
                        }
                    }
                }
            }
        };
        requireActivity().registerReceiver(stopQuickButtonService, new IntentFilter("com.EasySoundController.stopQuickButtonService"));


        stopNotifyRingModeChangeSwitchOff = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {

                } else {
                    if (isAdded()) {
                        binding.switch4.setChecked(false);
                    }
                }
            }
        };
        requireActivity().registerReceiver(stopNotifyRingModeChangeSwitchOff, new IntentFilter("com.EasySoundController.stopNotifyRingModeService"));


        enableNotificationSwitchOff = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {

                } else {
                    if (isAdded()) {
                        binding.switch3.setChecked(false);
                    }
                }
            }
        };
        requireActivity().registerReceiver(enableNotificationSwitchOff, new IntentFilter("com.EasySoundController.switch4off"));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            binding = SecondFragmentBinding.inflate(inflater, container, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        isSecondFragmentResumed = true;

        binding.scrollDownSettings.setTranslationY(0);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(binding.scrollDownSettings, "translationY", -50f);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setRepeatCount(11);
        objectAnimator.start();

        binding.googleAdLayout2.addView(MainActivity.adView);

        binding.switch1.setChecked(Quick_Shortcut_Button_Service.isQuickShortcutButtonServiceRunning);

        binding.switch2.setChecked(sharedPreferences.getBoolean("quickButtonStickToSides", false));

        if(binding.switch1.isChecked()) {
            binding.quickButtonSize.setVisibility(View.VISIBLE);
        } else {
            binding.quickButtonSize.setVisibility(View.GONE);
        }

        binding.switch3.setChecked(settingsData.isNotificationVolumeControlRunning(notificationManager));

        binding.switch4.setChecked(notifyRingMode.isRingModeNotifyServiceEnabled);

    }

    private PendingIntent NotiRegisterReceiver(String action) {
        Intent intent = new Intent(context, notificationClickReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(MainActivity.minterstitialAd != null && MainActivity.adCount == 0) {
            MainActivity.minterstitialAd.show(requireActivity());
        }

        MainActivity.adCount += 1;
        if (MainActivity.adCount == 3) {
            broadcastAndOtherCommonMethods.loadInAd(requireContext());
            MainActivity.adCount = 0;
        }

        binding.scrollDownSettings.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.scrollDownSettings.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (binding.settingsMainScrollView.canScrollVertically(-1)) {
                    binding.scrollUpSettings.setVisibility(View.VISIBLE);
                } else if (binding.settingsMainScrollView.canScrollVertically(1)) {
                    binding.scrollDownSettings.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.settingsMainScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!v.canScrollVertically(1)) {
                binding.scrollDownSettings.setVisibility(View.GONE);
            } else {
                binding.scrollDownSettings.setVisibility(View.VISIBLE);
            }
            if (!v.canScrollVertically(-1)) {
                binding.scrollUpSettings.setVisibility(View.GONE);
            } else {
                binding.scrollUpSettings.setVisibility(View.VISIBLE);
            }
        });

        binding.toolbar2.setNavigationIcon(R.drawable.navigate_before_35);

        binding.toolbar2.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            navController.navigateUp();
        });

        context = requireContext();
        sharedPreferences = requireActivity().getSharedPreferences("SettingsValues", Context.MODE_PRIVATE);
        notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        settingsData = new SettingsData();

        settingsData.ManageAppPermissions(binding, dialogs, context);
        settingsData.volumesToDisplayOnMainScreen(binding, dialogs, context, sharedPreferences);
        settingsData.appTheme(binding, dialogs, context, sharedPreferences);
        settingsData.quickShortcutFloatingButtonAction(binding, dialogs, context, sharedPreferences);
        settingsData.enableQuickShortcutFloatingButton(binding, dialogs, context, sharedPreferences, requireActivity());
        settingsData.stickQuickFloatButtonToSides(binding, sharedPreferences);
        settingsData.enableNotificationQuickVolume(binding);



        RemoteViews remoteViews = new RemoteViews(requireActivity().getPackageName(), R.layout.notification_layout);
        remoteViews.setOnClickPendingIntent(R.id.imageView17, NotiRegisterReceiver("com.example.soundcon.closeNotification"));
        remoteViews.setOnClickPendingIntent(R.id.imageView22, NotiRegisterReceiver("com.example.soundcon.home"));
        remoteViews.setOnClickPendingIntent(R.id.imageView18, NotiRegisterReceiver("com.example.soundcon.Music"));
        remoteViews.setOnClickPendingIntent(R.id.imageView21, NotiRegisterReceiver("com.example.soundcon.NotificationUnmute"));
        remoteViews.setOnClickPendingIntent(R.id.imageView19, NotiRegisterReceiver("com.example.soundcon.silent"));
        remoteViews.setOnClickPendingIntent(R.id.imageView20, NotiRegisterReceiver("com.example.soundcon.vibrate"));

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "Esc_Ch_id_1");
        notification.setGroup("easySoundControllerNonDismissableNotification");
        notification.setSmallIcon(R.drawable.volume_up_35);
        notification.setCustomBigContentView(remoteViews);
        notification.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        notification.setCustomContentView(remoteViews);
        notification.setOngoing(true);

        Notification notiBuilt = notification.build();


        SwitchCompat notificationSwitch = binding.switch3;


        ActivityResultLauncher<String> requestPostNotificationPermission =  registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                notificationSwitch.setChecked(true);
                notificationManager.notify(1, notiBuilt);
            } else {
                int notificationDeniedCount = sharedPreferences.getInt("notificationDeniedCount", 0);
                notificationSwitch.setChecked(false);
                if (notificationDeniedCount <= 1) {
                    sharedPreferences.edit().putInt("notificationDeniedCount", notificationDeniedCount + 1).apply();
                } else {
                    dialogs.notificationPermissionDialog(context, null, true, notificationSwitch);
                }
            }
        });

        notificationSwitch.setOnClickListener(v -> {
            if (notificationSwitch.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        notificationManager.notify(1, notiBuilt);
                    } else if (requireActivity().shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        dialogs.notificationPermissionDialog(context, requestPostNotificationPermission, false, notificationSwitch);
                        sharedPreferences.edit().putBoolean("permissionDeniedOnce", true).apply();
                    } else {
                        requestPostNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
                        if (!sharedPreferences.getBoolean("permissionDeniedOnce", false)) {
                            sharedPreferences.edit().putInt("notificationDeniedCount", 0).apply();
                        }
                    }
                } else {
                    notificationManager.notify(1, notiBuilt);
                }
            } else {
                notificationManager.cancel(1);
                notificationSwitch.setChecked(false);
            }
        });


        settingsData.iconNotificationWhenRingModeChanges(binding, dialogs, context, sharedPreferences, requireActivity());

        if (!sharedPreferences.getBoolean("autostartAlertCheckBox", false)) {
            dialogs.autostartAlert(context, sharedPreferences);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        isSecondFragmentResumed = false;
        if (MainActivity.adView.isAttachedToWindow()) {
            binding.googleAdLayout2.removeView(MainActivity.adView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (stopQuickButtonService != null) {
            requireActivity().unregisterReceiver(stopQuickButtonService);
        }
        if (stopNotifyRingModeChangeSwitchOff != null) {
            requireActivity().unregisterReceiver(stopNotifyRingModeChangeSwitchOff);
        }
        if (enableNotificationSwitchOff != null) {
            requireActivity().unregisterReceiver(enableNotificationSwitchOff);
        }

        dialogs = null;

        binding = null;

    }
}
