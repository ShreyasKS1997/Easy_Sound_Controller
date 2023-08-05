package com.EasySoundController;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.SeekBar;


import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.EasySoundController.databinding.SecondFragmentBinding;

public class SettingsData {

    protected void ManageAppPermissions(SecondFragmentBinding binding, Dialogs dialogs, Context context) {
        binding.appPermission.setOnClickListener(v -> dialogs.appPermissionDialog(context));
    }

    protected void volumesToDisplayOnMainScreen(SecondFragmentBinding binding, Dialogs dialogs, Context context
    , SharedPreferences sharedPreferences) {
        binding.volumesToDisplayOnMainScreen.setOnClickListener(v -> dialogs.selectVolumesToDisplay(context, sharedPreferences));
    }

    protected void appTheme(SecondFragmentBinding binding, Dialogs dialogs, Context context
            , SharedPreferences sharedPreferences) {
        binding.appThemeSelect.setOnClickListener(v -> dialogs.chooseAppTheme(context, sharedPreferences));
    }

    protected void quickShortcutFloatingButtonAction(SecondFragmentBinding binding, Dialogs dialogs, Context context
            , SharedPreferences sharedPreferences) {
        binding.quickShortcutButtonTapAction.setOnClickListener(v -> dialogs.quickShortcutButtonActionChoice(context, sharedPreferences));
    }

    protected void enableQuickShortcutFloatingButton(SecondFragmentBinding binding, Dialogs dialogs, Context context
            , SharedPreferences sharedPreferences, FragmentActivity activity) {

        SwitchCompat enableQuickShortcutButtonService = binding.switch1;

        binding.quickShortcutButtonFullLayout.setOnClickListener(v -> binding.switch1.performClick());

        int quickSizeButtonSize = sharedPreferences.getInt("quickShortcutButtonSize", 2);

        Intent startQuickFloatButtonService = new Intent(context, Quick_Shortcut_Button_Service.class);
        startQuickFloatButtonService.putExtra("quickSizeButtonSize", quickSizeButtonSize);

        ConstraintLayout quickButtonSizeLayout = binding.quickButtonSize;
        SeekBar quickShortcutButtonSeekBar = binding.seekBar8;
        quickShortcutButtonSeekBar.setMax(4);
        quickShortcutButtonSeekBar.setProgress(quickSizeButtonSize);
        quickShortcutButtonSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressl = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressl = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sharedPreferences.edit().putInt("quickShortcutButtonSize", progressl).apply();
                activity.stopService(startQuickFloatButtonService);
                activity.startService(startQuickFloatButtonService);
            }
        });

        enableQuickShortcutButtonService.setOnClickListener(v -> {
            if (Settings.canDrawOverlays(context)) {
                if (enableQuickShortcutButtonService.isChecked()) {
                    if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                        if (!sharedPreferences.getBoolean("notificationIncompatibilityWithAndroid", false)) {
                            dialogs.alertNotificationIncompatibility(context, sharedPreferences);
                        }
                    }
                    activity.startService(startQuickFloatButtonService);
                    quickButtonSizeLayout.setVisibility(View.VISIBLE);
                } else {
                    activity.stopService(startQuickFloatButtonService);
                    quickButtonSizeLayout.setVisibility(View.GONE);
                }
            } else {
                dialogs.screenOverlayDialog(context);
                enableQuickShortcutButtonService.setChecked(false);
            }
        });
    }

    protected void stickQuickFloatButtonToSides(SecondFragmentBinding binding, SharedPreferences sharedPreferences) {
        binding.stickQuickButtonToSidesFullLayout.setOnClickListener(v -> binding.switch2.performClick());

        binding.switch2.setOnCheckedChangeListener((buttonView, isChecked) -> sharedPreferences.edit().putBoolean("quickButtonStickToSides", isChecked).apply());

    }

    protected void enableNotificationQuickVolume(SecondFragmentBinding binding) {

        binding.notificationVolumesControlSwitchFullLayout.setOnClickListener(v -> binding.switch3.performClick());

    }

    protected boolean isNotificationVolumeControlRunning(NotificationManager notificationManager) {
        StatusBarNotification[] statusBarManagers = notificationManager.getActiveNotifications();
        boolean isNotificationRunning = false;
        for (StatusBarNotification isNotificationActive : statusBarManagers) {
            if (isNotificationActive.getId() == 1) {
                return true;
            }
        }
        return isNotificationRunning;
    }

    protected void iconNotificationWhenRingModeChanges(SecondFragmentBinding binding, Dialogs dialogs, Context context
            , SharedPreferences sharedPreferences, FragmentActivity activity) {

        binding.RingModeChangeIconNotifyFullLayout.setOnClickListener(v -> binding.switch4.performClick());

        binding.switch4.setOnClickListener(v -> {
            if (Settings.canDrawOverlays(context)) {
                if (binding.switch4.isChecked()) {
                    if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                        if (!sharedPreferences.getBoolean("notificationIncompatibilityWithAndroid", false)) {
                            dialogs.alertNotificationIncompatibility(context, sharedPreferences);
                        }
                    }
                    activity.startService(new Intent(context, notifyRingMode.class));
                } else {
                    activity.stopService(new Intent(context, notifyRingMode.class));
                }
            } else {
                dialogs.screenOverlayDialog(context);
                binding.switch4.setChecked(false);
            }
        });
    }

}
