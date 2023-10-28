package com.EasySoundController;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;
import java.util.Objects;

public class Dialogs {

    public void CallOrAlarmCannotBeZeroDialog(Context context, SeekBar seekBar, AudioManager audioManager, int streamType){
        CheckBox checkBox = new android.widget.CheckBox(context);
        checkBox.setText("Tap here to not show this message again.");
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.addView(checkBox);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(60, 30, 60, 0);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        String setMessage = "";
        if (streamType == AudioManager.STREAM_VOICE_CALL) {
            setMessage = "You cannot mute or make the Call Volume to 0% as your device doesn't support it";
        } else if (streamType == AudioManager.STREAM_ALARM) {
            setMessage = "You cannot mute or make the Alarm Volume to 0% as your device doesn't support it";
        }
        alertDialog.setMessage(setMessage);
        alertDialog.create();
        alertDialog.setView(linearLayout);
        alertDialog.setPositiveButton("OK", (dialogInterface, i) -> {
            audioManager.setStreamVolume(streamType, 1, 0);
            seekBar.setProgress(1);
        });
        alertDialog.show();
    }

    public void takeToDnDAccessSettings(Context context) {
        final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
        final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_FRAGMENT_ARG_KEY, context.getPackageName());
        context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void needsDnDPermissionDialog(Context context,SeekBar actionSeekBar, boolean overlayDialog) {
        AlertDialog.Builder alb = new AlertDialog.Builder(context);
        alb.setMessage("Need access to adjust this volume. Tap 'Take me there' button below to go directly to that Settings and allow or enable the access for this App.");
        alb.setPositiveButton("Take me there", (dialogInterface, i) -> {
            if (actionSeekBar != null) {
                actionSeekBar.setEnabled(true);
            }
            takeToDnDAccessSettings(context);
        });
        alb.setNegativeButton("Cancel", (dialogInterface, i) -> {
            if (actionSeekBar != null) {
                actionSeekBar.setEnabled(true);
            }
        });
        AlertDialog albBuilt = alb.create();
        if (overlayDialog) {
            if (Settings.canDrawOverlays(context)) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                    albBuilt.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                } else {
                    albBuilt.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                }
            } else {
                screenOverlayDialog(context);
            }
        }
        albBuilt.show();
    }

    public void capturePresetButton(Context context, SharedPreferences sharedPreferences, AudioManager audioManager) {
        AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(context);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50,50,50,0);

        TextView textView = new TextView(context);
        textView.setText("Enter the name of your choice to capture the current Volume Settings and save it as a Profile.");
        textView.setTextSize(15.0f);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        TextInputEditText capturedName = new TextInputEditText(context);

        linearLayout.addView(textView);
        linearLayout.addView(capturedName);

        alerDialogBuilder.setView(linearLayout);
        alerDialogBuilder.setPositiveButton("Save", (dialog, which) -> {

        });
        alerDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        AlertDialog saveButtonAlertDialog = alerDialogBuilder.create();
        saveButtonAlertDialog.show();

        saveButtonAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            String musicVolumeLevel = String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            String callVolumeLevel = String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
            String ringVolumeLevel = String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_RING));
            String ringerMode = String.valueOf(audioManager.getRingerMode());
            String alarmVolumeLevel = String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
            String notificationVolumeLevel = String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
            String systemVolumeLevel = String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
            String dtmfVolumeLevel = String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_DTMF));

            String volumesLevel = musicVolumeLevel + "," + callVolumeLevel + "," + ringVolumeLevel + "," + ringerMode + "," + alarmVolumeLevel + "," + notificationVolumeLevel + "," + systemVolumeLevel + "," + dtmfVolumeLevel;

            String enteredText = Objects.requireNonNull(capturedName.getText()).toString();

            if (sharedPreferences.contains(enteredText)) {
                AlertDialog.Builder overwriteConfirmationDialog = new AlertDialog.Builder(context);
                overwriteConfirmationDialog.setTitle("Profile name already exist. Do you want to overwrite it?");
                overwriteConfirmationDialog.setPositiveButton("Yes", (dialog, which) -> {
                    sharedPreferences.edit().putString(capturedName.getText().toString(), volumesLevel).apply();
                    if (sharedPreferences.contains(enteredText)) {
                        Toast toastSuccess = Toast.makeText(context, "Profile successfully created", Toast.LENGTH_LONG);
                        toastSuccess.show();
                    } else {
                        Toast toastFailure = Toast.makeText(context, "Something went wrong. Please contact us.", Toast.LENGTH_LONG);
                        toastFailure.show();
                    }

                    saveButtonAlertDialog.dismiss();

                });
                overwriteConfirmationDialog.setNegativeButton("No", (dialog, which) -> {

                });
                AlertDialog alertDialog = overwriteConfirmationDialog.create();
                alertDialog.show();
            } else {
                sharedPreferences.edit().putString(capturedName.getText().toString(), volumesLevel).apply();

                if (sharedPreferences.contains(enteredText)) {
                    Toast toastSuccess = Toast.makeText(context, "Profile successfully created", Toast.LENGTH_LONG);
                    toastSuccess.show();
                } else {
                    Toast toastFailure = Toast.makeText(context, "Something went wrong. Please contact us.", Toast.LENGTH_LONG);
                    toastFailure.show();
                }
                saveButtonAlertDialog.dismiss();
            }
        });

    }

    AlertDialog alertDialogCreate;
    AlertDialog renameDialogCreate;
    AlertDialog renameRemoveDialogCreate;
    AlertDialog overwriteDialogCreate;
    String newProfileNameInString;


    private void AlertDialogCommonForChoosePreset(Context context, String string, String[] savedProfileStringArray, Integer pos,
                                                  SharedPreferences sharedPreferencesVolumeLevels, NotificationManager notificationManager,
                                                  AudioManager audioManager) {

        switch (string) {
            case "renameRemove": {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setItems(new String[]{"Rename", "Remove"}, (dialog, which) -> {

                });

                renameRemoveDialogCreate = alertDialogBuilder.create();

                renameRemoveDialogCreate.show();

                renameRemoveDialogCreate.getListView().setOnItemClickListener((parent, view, position, id) -> {
                    if (position == 0) {
                        AlertDialogCommonForChoosePreset(context, "renameDialog", savedProfileStringArray, pos, sharedPreferencesVolumeLevels, notificationManager, audioManager);
                    } else {
                        AlertDialogCommonForChoosePreset(context, "removeDialog", savedProfileStringArray, pos, sharedPreferencesVolumeLevels, notificationManager, audioManager);
                    }
                });
                /*------------------------------------------------------------------------------------------------------------------------------------------------*/
                break;
            }
            case "renameDialog": {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setTitle("Enter the new name to rename the Profile");
                LinearLayout textInputEditTextLayout = new LinearLayout(context);
                textInputEditTextLayout.setOrientation(LinearLayout.VERTICAL);
                textInputEditTextLayout.setPadding(50, 10, 50, 0);
                TextInputEditText newProfileName = new TextInputEditText(context);
                textInputEditTextLayout.addView(newProfileName);
                alertDialogBuilder.setView(textInputEditTextLayout);


                alertDialogBuilder.setPositiveButton("Rename", (dialog, which) -> {

                });
                alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                });

                renameDialogCreate = alertDialogBuilder.create();

                renameDialogCreate.show();
                renameDialogCreate.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {

                    newProfileNameInString = Objects.requireNonNull(newProfileName.getText()).toString();

                    if (newProfileNameInString.equals("")) {
                        Toast.makeText(context, "Please enter something to rename", Toast.LENGTH_LONG).show();
                    } else if (sharedPreferencesVolumeLevels.contains(newProfileNameInString)) {
                        AlertDialogCommonForChoosePreset(context, "overwriteConfirmDialog", savedProfileStringArray, pos, sharedPreferencesVolumeLevels, notificationManager, audioManager);
                    } else {
                        renameProfile(context, savedProfileStringArray, pos, newProfileNameInString, sharedPreferencesVolumeLevels, audioManager, notificationManager);
                    }
                });
                /*------------------------------------------------------------------------------------------------------------------------------------------------*/
                break;
            }
            case "removeDialog":

                if (sharedPreferencesVolumeLevels.contains(savedProfileStringArray[pos])) {
                    sharedPreferencesVolumeLevels.edit().remove(savedProfileStringArray[pos]).apply();
                    if (sharedPreferencesVolumeLevels.contains(savedProfileStringArray[pos])) {
                        Toast.makeText(context, "Something went wrong. Please contact us", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Profile removed Successfully", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Something went wrong. Please contact us", Toast.LENGTH_LONG).show();
                }
                renameRemoveDialogCreate.dismiss();
                alertDialogCreate.dismiss();
                chooseSavedProfile(context, sharedPreferencesVolumeLevels, audioManager, notificationManager);
                /*-------------------------------------------------------------------------------------------------------------------------------------------------*/
                break;
            case "overwriteConfirmDialog": {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setTitle("Profile name already exist. Do you want to overwrite it?");

                alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> renameProfile(context, savedProfileStringArray, pos, newProfileNameInString, sharedPreferencesVolumeLevels, audioManager, notificationManager));
                alertDialogBuilder.setNegativeButton("No", (dialog, which) -> overwriteDialogCreate.dismiss());

                overwriteDialogCreate = alertDialogBuilder.create();

                overwriteDialogCreate.show();

                break;
            }
        }
    }

    public void renameProfile(Context context, String[] savedProfilesInStringArray, int position, String newProfileNameInString,
                              SharedPreferences sharedPreferencesVolumeLevels, AudioManager audioManager, NotificationManager notificationManager) {
        String selectedCurrentVolumeValue = sharedPreferencesVolumeLevels.getString(savedProfilesInStringArray[position], "");
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferencesVolumeLevels.edit();
        sharedPreferencesEditor.remove(savedProfilesInStringArray[position]).apply();
        if (sharedPreferencesVolumeLevels.contains(savedProfilesInStringArray[position])) {
            Toast.makeText(context, "Something went wrong. Please contact us", Toast.LENGTH_LONG).show();
        } else {
            sharedPreferencesEditor.putString(newProfileNameInString, selectedCurrentVolumeValue).apply();
            if (sharedPreferencesVolumeLevels.contains(newProfileNameInString)) {
                alertDialogCreate.dismiss();
                chooseSavedProfile(context, sharedPreferencesVolumeLevels, audioManager, notificationManager);
                Toast.makeText(context, "Successfully Renamed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Something went wrong. Please contact us", Toast.LENGTH_LONG).show();
            }
        }
        renameDialogCreate.dismiss();
        renameRemoveDialogCreate.dismiss();
    }



    public void chooseSavedProfile(Context context, SharedPreferences sharedPreferences, AudioManager audioManager, NotificationManager notificationManager) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        Map<String, ?> savedProfileList = sharedPreferences.getAll();
        String[] savedProfilesInStringArray = savedProfileList.keySet().toArray(new String[0]);
        if (savedProfilesInStringArray.length == 0) {
            alertDialogBuilder.setMessage("No Profile created yet. Please create a Profile first by tapping 'CREATE THE ABOVE VOLUME PRESET' Button, Enter the name and save it.");
        } else {
            TextView customTitle = new TextView(context);
            customTitle.setPadding(50,60, 0,0);
            customTitle.setTypeface(Typeface.DEFAULT_BOLD);
            customTitle.setText("Tap to select the Profile or long press to modify the Profile");
            alertDialogBuilder.setCustomTitle(customTitle);
        }
        alertDialogBuilder.setItems(savedProfilesInStringArray, (dialog, which) -> {
            String volumeToBeAppliedInString = (String) savedProfileList.get(savedProfilesInStringArray[which]);
            String[] volumeDataConverted = Objects.requireNonNull(volumeToBeAppliedInString).split(",");

            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.parseInt(volumeDataConverted[0]), 0);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, Integer.parseInt(volumeDataConverted[1]), 0);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, Integer.parseInt(volumeDataConverted[4]), 0);
            try {
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, Integer.parseInt(volumeDataConverted[5]), 0);
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, Integer.parseInt(volumeDataConverted[6]), 0);
                audioManager.setStreamVolume(AudioManager.STREAM_DTMF, Integer.parseInt(volumeDataConverted[7]), 0);
            } catch (SecurityException e) {
                Toast.makeText(context, "Please enable access to DnD", Toast.LENGTH_LONG).show();
            }
            if (Integer.parseInt(volumeDataConverted[2]) == 0 && Integer.parseInt(volumeDataConverted[3]) != 1) {
                if (notificationManager.isNotificationPolicyAccessGranted()) {
                    if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                } else {
                    needsDnDPermissionDialog(context, null, false);
                }
            } else if (Integer.parseInt(volumeDataConverted[2]) == 0 && Integer.parseInt(volumeDataConverted[3]) == 1) {
                if (notificationManager.isNotificationPolicyAccessGranted() | audioManager.getRingerMode() != 0) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                } else {
                    needsDnDPermissionDialog(context, null, false);
                }
            } else {
                if (notificationManager.isNotificationPolicyAccessGranted() | audioManager.getRingerMode() != 0) {
                    if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL && audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    }
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI);
                } else {
                    needsDnDPermissionDialog(context, null, true);
                }
            }


            Intent refreshFirstFragmentSeekBars = new Intent("refreshFirstFragmentSeekBars");
            context.sendBroadcast(refreshFirstFragmentSeekBars);


            String validateSettingProfile = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + "," +
                    audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) + "," +
                    audioManager.getStreamVolume(AudioManager.STREAM_RING) + "," +
                    audioManager.getRingerMode() + "," +
                    audioManager.getStreamVolume(AudioManager.STREAM_ALARM) + "," +
                    audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) + "," +
                    audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) + "," +
                    audioManager.getStreamVolume(AudioManager.STREAM_DTMF);

            if (validateSettingProfile.equals(volumeToBeAppliedInString)) {
                Toast.makeText(context, "Profile Applied Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Something went wrong. Please contact us.", Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNegativeButton("Close", (dialog, which) -> {

        });

        alertDialogCreate = alertDialogBuilder.create();
        alertDialogCreate.getListView().setOnItemLongClickListener((parent, view, position, id) -> {

            AlertDialogCommonForChoosePreset(context, "renameRemove", savedProfilesInStringArray, position, sharedPreferences, notificationManager, audioManager );
            return true;
        });
        alertDialogCreate.show();
    }

    public void takeToOverlayPermission(Context context) {
        Intent overlayPermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        overlayPermissionIntent.setData(uri);
        context.startActivity(overlayPermissionIntent);
    }

    public void appPermissionDialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Manage Permissions");
        alertDialogBuilder.setItems(new String[]{"Tap here to enable or disable DnD access to the App","Tap here to enable or disable this App's access to Display Over App", "Tap here to enable or disable the permission for Notification"}, (dialog, which) -> {

            final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
            final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";

            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_FRAGMENT_ARG_KEY, context.getPackageName());

            if (which == 0) {
                Intent lintent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                lintent.putExtra(EXTRA_FRAGMENT_ARG_KEY, context.getPackageName());
                lintent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle);
                context.startActivity(lintent);
            } else if (which == 1) {
                takeToOverlayPermission(context);
            } else {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                } else {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", context.getPackageName());
                    intent.putExtra("app_uid", context.getApplicationInfo().uid);
                }
                context.startActivity(intent);
            }
        });
        alertDialogBuilder.setNegativeButton("Close", (dialog, which) -> {

        });
        alertDialogBuilder.show();
    }

    public void selectVolumesToDisplay(Context context, SharedPreferences sharedPreferences) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        TextView textView = new TextView(context);
        textView.setTextSize(17.0f);
        textView.setPadding(50,60, 50,0);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText("Select all the Volumes that you want to adjust in the Main Screen");
        alertDialogBuilder.setCustomTitle(textView);

        String[] volumesToDisplayArray = new String[]{"Music Volume", "Call Volume", "Ring Volume",
                "Alarm Volume", "Notification Volume", "System Volume", "Dial Tone Volume"};

        boolean[] volumesToDisplayArrayValues = new boolean[7];
        for (int i = 0; i <= 6; i++) {
            volumesToDisplayArrayValues[i] = sharedPreferences.getBoolean(volumesToDisplayArray[i], true);
        }

        alertDialogBuilder.setMultiChoiceItems(volumesToDisplayArray, volumesToDisplayArrayValues,
                (dialog, which, isChecked) -> sharedPreferences.edit().putBoolean(volumesToDisplayArray[which], isChecked).apply());
        alertDialogBuilder.setNegativeButton("Close", (dialog, which) -> {

        });
        alertDialogBuilder.show();
    }

    public void quickShortcutButtonActionChoice(Context context, SharedPreferences sharedPreferences) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        TextView title = new TextView(context);
        title.setText("Choose the action to perform when you tap the Quick Shortcut Floating Button");
        title.setPadding(50,50,50,0);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextSize(17.0f);
        alertDialogBuilder.setCustomTitle(title);
        alertDialogBuilder.setSingleChoiceItems(new String[]{"Get back to the App", "Show System Default Volume Dialog Panel", "Show Media, Vibration and Silent Toggle Buttons"}, sharedPreferences.getInt("quickShortcutButtonAction", 2), (dialog, which) -> sharedPreferences.edit().putInt("quickShortcutButtonAction", which).apply());
        alertDialogBuilder.setNegativeButton("Close", (dialog, which) -> {

        });
        alertDialogBuilder.show();
    }

    AlertDialog alertDialog;
    public void chooseAppTheme(Context context, SharedPreferences sharedPreferences) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Choose the App Theme");
        alertDialogBuilder.setSingleChoiceItems(new String[]{"Dark", "Light", "System Default"}, sharedPreferences.getInt("appTheme", 2), (dialog, which) -> {
            sharedPreferences.edit().putInt("appTheme", which).apply();
            alertDialog.dismiss();
            if (which == 0) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (which == 1) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });
        alertDialogBuilder.setNegativeButton("Close", (dialog, which) -> {

        });
        alertDialog =  alertDialogBuilder.create();
        alertDialog.show();
    }

    public void screenOverlayDialog(Context context) {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
        alertdialog.setMessage("Need access to Screen Overlay. Tap 'Take me there' to go to that Settings and allow or enable the access to this App");
        alertdialog.setPositiveButton("Take me there", (dialogInterface, i) -> {
            Intent overlayAppPermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            overlayAppPermissionIntent.setData(uri);
            context.startActivity(overlayAppPermissionIntent);
        });
        alertdialog.setNegativeButton("Close", (dialogInterface, i) -> {

        });
        alertdialog.create().show();
    }


    public void notificationPermissionDialog(Context context, @Nullable ActivityResultLauncher<String> requestPostNotificationPermission,
                                             boolean isPermanentlyDenied, SwitchCompat switchToDisable) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Please allow Notification Permission, If you want to control Volumes from Notification");
        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            if (isPermanentlyDenied) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                } else {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", context.getPackageName());
                    intent.putExtra("app_uid", context.getApplicationInfo().uid);
                }
                context.startActivity(intent);
            } else {
                SharedPreferences sharedPreferences = context.getSharedPreferences("SettingsValues", Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt("notificationDeniedCount", 1).apply();
                Objects.requireNonNull(requestPostNotificationPermission).launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        });
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> switchToDisable.setChecked(false));
        alertDialogBuilder.show();
    }

    public void alertNotificationIncompatibility(Context context, SharedPreferences sharedPreferences) {
        AlertDialog.Builder alb = new AlertDialog.Builder(context);
        alb.setMessage("Note: You cannot disable individual Notification in this Version of Android." +
                " If you disable Notification, Then no Notification will be displayed including Volume controls" +
                " on Notification. But you can still continue using Quick Shortcut Floating Button and Notify Ring Mode Changes" +
                " Service without Notification. (Enable or disable Notification Permission by tapping " +
                "'Manage Permissions' or disable it by tapping 'Disable' Button below)");
        LinearLayout checkBoxWrap = new LinearLayout(context);
        checkBoxWrap.setPadding(100,0,0,0);
        CheckBox checkBox = new CheckBox(context);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> sharedPreferences.edit().putBoolean("notificationIncompatibilityWithAndroid", isChecked).apply());
        checkBox.setText("Don't show this Alert again");
        checkBoxWrap.addView(checkBox);
        alb.setView(checkBoxWrap);
        alb.setPositiveButton("Disable", (dialog, which) -> {
            Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        alb.setNegativeButton("Close", (dialog, which) -> {

        });
        AlertDialog alertDialog = alb.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        alertDialog.show();
    }

    public void autostartAlert(Context context, SharedPreferences sharedPreferences) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("If Quick Shortcut Floating Button Service or Notify Ring Mode Changes Service or Volume control on Notification disappears after this App is removed from 'Recent Apps' or 'App History', You may need to enable Autostart Permission or disable Battery Optimization for this App in your Phone Settings");
        LinearLayout checkBoxWrap = new LinearLayout(context);
        CheckBox checkBox = new CheckBox(context);
        checkBox.setTypeface(Typeface.DEFAULT_BOLD);
        checkBox.setText("Don't show this Alert again");
        checkBoxWrap.addView(checkBox);
        checkBoxWrap.setPadding(50,0,0,0);
        alertDialogBuilder.setView(checkBoxWrap);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> sharedPreferences.edit().putBoolean("autostartAlertCheckBox", isChecked).apply());
        alertDialogBuilder.setNegativeButton("Close", (dialog, which) -> {

        });
        alertDialogBuilder.show();
    }

    public void rateUsDialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Rate us");
        alertDialogBuilder.setItems(new String[]{"Google Play Store", "Amazon App Store"}, (dialog, which) -> {
            if (which == 0) {
                broadcastAndOtherCommonMethods.openOnAppStores(context, "market://details?id=", "http://play.google.com/store/apps/details?id=");
            } else if (which == 1) {
                broadcastAndOtherCommonMethods.openOnAppStores(context, "amzn://apps/android?p=", "http://www.amazon.com/gp/mas/dl/android?p=");
            }
        });
        alertDialogBuilder.setNegativeButton("Close", (dialog, which) -> {

        });
        alertDialogBuilder.show();
    }

}
