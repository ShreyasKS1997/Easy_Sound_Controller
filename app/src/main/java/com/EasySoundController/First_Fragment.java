package com.EasySoundController;

import static android.content.Context.MEDIA_ROUTER_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.media.MediaRouter.CALLBACK_FLAG_UNFILTERED_EVENTS;
import static android.media.MediaRouter.ROUTE_TYPE_USER;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.EasySoundController.databinding.FirstFragmentBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

public class First_Fragment extends Fragment {

    static boolean isFirstFragmentResumed = false;
    AudioData audioData;
    AudioManager audioManager;
    private FirstFragmentBinding binding;
    Dialogs dialogs = new Dialogs();
    SharedPreferences sharedPreferencesVolumeLevels;
    private TextView noVolumesCheckedMessage;

    private BroadcastReceiver broadcastReceiver;

    private BroadcastReceiver headsetPluggedUnpluggedBroadcast;

    private BroadcastReceiver refreshFirstFragmentSeekbars;
    NotificationManager notificationManager;
    SharedPreferences settingsSharedPreferences;
    boolean isFirstFragmentInitialized = false;
    MediaRouter mediaRouter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationManager = (NotificationManager) requireContext().getSystemService(NOTIFICATION_SERVICE);

        headsetPluggedUnpluggedBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {

                } else {
                    broadcastAndOtherCommonMethods.setMusicIcon(audioManager);
                    if (binding.imageView != null) {
                        broadcastAndOtherCommonMethods.setMusicIconWhenLayoutRefreshed(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), binding.imageView);
                    }
                    if (binding.seekBar != null) {
                        binding.seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    }
                    MainActivity.lastVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                }
            }
        };

        requireActivity().registerReceiver(headsetPluggedUnpluggedBroadcast, new IntentFilter(Intent.ACTION_HEADSET_PLUG));


        refreshFirstFragmentSeekbars = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                binding.seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                binding.seekBar2.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
                binding.seekBar3.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
                binding.seekBar4.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
                binding.seekBar5.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                binding.seekBar6.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                binding.seekBar7.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_DTMF));
            }
        };

        requireActivity().registerReceiver(refreshFirstFragmentSeekbars, new IntentFilter("refreshFirstFragmentSeekBars"));

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {

                } else {
                    if (isFirstFragmentResumed) {
                        int ringerMode = audioManager.getRingerMode();
                        int currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

                        if (ringerMode == 0 | (currentMusicVolume == 0 && ringerMode != 1)) {
                            binding.imageView3.setImageResource(R.drawable.notifications_off_35);
                            binding.seekBar3.setProgress(0);
                        } else if (ringerMode == 1) {
                            binding.imageView3.setImageResource(R.drawable.vibration_35);
                            binding.seekBar3.setProgress(0);
                        } else if (ringerMode == 2) {
                            binding.imageView3.setImageResource(R.drawable.notifications_on_35);
                            binding.seekBar3.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
                        }
                        if (notificationManager.getCurrentInterruptionFilter() != NotificationManager.INTERRUPTION_FILTER_ALL) {
                            binding.imageButton.setImageResource(R.drawable.baseline_do_not_disturb_off_35);
                        } else {
                            binding.imageButton.setImageResource(R.drawable.do_not_disturb_on_35);
                        }
                        binding.seekBar5.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                        binding.seekBar6.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                        binding.seekBar7.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_DTMF));
                    }
                }
            }
        };
        requireActivity().registerReceiver(broadcastReceiver, new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            binding = FirstFragmentBinding.inflate(inflater, container, false);
        }
        return binding.getRoot();
    }

    public void initializeAudio() {

        audioData.volumeMedia(requireContext(), audioManager, binding.seekBar, binding.textViewPercent);
        audioData.setupCallback(binding.seekBar, binding.imageView);
        audioData.volumeCall(audioManager, binding.seekBar2, binding.textViewPercent2, dialogs, requireContext());
        audioData.volumeRing(audioManager, binding.seekBar3, binding.textViewPercent3, binding.imageView3, notificationManager, dialogs, requireContext(),
                binding.seekBar5, binding.seekBar6, binding.seekBar7);
        audioData.volumeAlarm(audioManager, binding.seekBar4, binding.textViewPercent4, dialogs, requireContext());
        audioData.volumeNotification(audioManager, binding.seekBar3, binding.textViewPercent5, notificationManager, dialogs, requireContext(), binding.seekBar5
        ,binding.seekBar6, binding.seekBar7);
        audioData.volumeSystem(audioManager, binding.seekBar3, binding.textViewPercent6, notificationManager, dialogs, requireContext(), binding.seekBar5
                ,binding.seekBar6, binding.seekBar7);
        audioData.volumeDTMF(audioManager, binding.seekBar3, binding.textViewPercent7, notificationManager, dialogs, requireContext(), binding.seekBar5
                ,binding.seekBar6, binding.seekBar7);


        audioData.DnDButtonFF(notificationManager, binding.imageButton, audioManager, dialogs, requireContext());
        audioData.silentButtonFF(audioManager, binding.imageButton2, notificationManager, dialogs, requireContext());
        audioData.vibrateButtonFF(audioManager, notificationManager, binding.imageButton3, dialogs, requireContext());
        audioData.normalRingVolumeButtonFF(audioManager, notificationManager, binding.imageButton4, dialogs, requireContext());
        audioData.musicButtonFF(audioManager, binding.imageButton5);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar1.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                if (menuItem.getItemId() == R.id.second_fragment) {
                    navController.navigate(R.id.action_first_Fragment_to_second_fragment);
                    return true;
                } else if (menuItem.getItemId() == R.id.about_menu) {
                    navController.navigate(R.id.action_first_Fragment_to_about_menu);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        if (!isFirstFragmentInitialized) {

            binding.scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (binding.scrollView.canScrollVertically(-1)) {
                        binding.scrollUpArrow.setVisibility(View.VISIBLE);
                    } else if (binding.scrollView.canScrollVertically(1)) {
                        binding.scrollDownArrow.setVisibility(View.VISIBLE);
                    }
                }
            });

            binding.scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (!v.canScrollVertically(1)) {
                    binding.scrollDownArrow.setVisibility(View.GONE);
                } else {
                    binding.scrollDownArrow.setVisibility(View.VISIBLE);
                }
                if (!v.canScrollVertically(-1)) {
                    binding.scrollUpArrow.setVisibility(View.GONE);
                } else {
                    binding.scrollUpArrow.setVisibility(View.VISIBLE);
                }
            });

            settingsSharedPreferences = requireContext().getSharedPreferences("SettingsValues", Context.MODE_PRIVATE);

            audioManager = MainActivity.am;
            sharedPreferencesVolumeLevels = requireActivity().getSharedPreferences("volumeLevels", Context.MODE_PRIVATE);

            audioData = new AudioData();

            initializeAudio();

            mediaRouter = (MediaRouter) requireContext().getSystemService(MEDIA_ROUTER_SERVICE);

            binding.button.setOnClickListener(v -> dialogs.capturePresetButton(getContext(), sharedPreferencesVolumeLevels, audioManager));

            binding.button2.setOnClickListener(v -> dialogs.chooseSavedProfile(requireContext(), sharedPreferencesVolumeLevels, audioManager, MainActivity.nm));

            int selectedAppTheme = settingsSharedPreferences.getInt("appTheme", 2);
            if (selectedAppTheme == 0) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (selectedAppTheme == 1) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            isFirstFragmentInitialized = true;
        }
    }

    public List<Boolean> volumeBarsVisibility() {
        List<Boolean> allVolumeBarVisibility = new ArrayList<>();
        boolean anyOneVolumeVisible = false;
        for (String volumeType : MainActivity.volumesShowedInMainScreen) {
            boolean volume = settingsSharedPreferences.getBoolean(volumeType, true);
            if (volume && !anyOneVolumeVisible) {
                anyOneVolumeVisible = true;
            }
            allVolumeBarVisibility.add(volume);
        }
        allVolumeBarVisibility.add(anyOneVolumeVisible);
        return allVolumeBarVisibility;
    }

    @Override
    public void onResume() {
        super.onResume();

        isFirstFragmentResumed = true;

        mediaRouter.addCallback(ROUTE_TYPE_USER, audioData.callback, CALLBACK_FLAG_UNFILTERED_EVENTS);

        binding.scrollDownArrow.setTranslationY(0);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(binding.scrollDownArrow, "translationY", -50f);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setRepeatCount(11);
        objectAnimator.start();

        List<Boolean> allVolumesVisibility = volumeBarsVisibility();
        if (allVolumesVisibility.get(0)) {
            binding.musicVolume.setVisibility(View.VISIBLE);
        } else {
            binding.musicVolume.setVisibility(View.GONE);
        }
        if (allVolumesVisibility.get(1)) {
            binding.callVolume.setVisibility(View.VISIBLE);
        } else {
            binding.callVolume.setVisibility(View.GONE);
        }
        if (allVolumesVisibility.get(2)) {
            binding.ringVolume.setVisibility(View.VISIBLE);
        } else {
            binding.ringVolume.setVisibility(View.GONE);
        }
        if (allVolumesVisibility.get(3)) {
            binding.alarmVolume.setVisibility(View.VISIBLE);
        } else {
            binding.alarmVolume.setVisibility(View.GONE);
        }
        if (allVolumesVisibility.get(4)) {
            binding.notificationVolume.setVisibility(View.VISIBLE);
        } else {
            binding.notificationVolume.setVisibility(View.GONE);
        }
        if (allVolumesVisibility.get(5)) {
            binding.systemVolume.setVisibility(View.VISIBLE);
        } else {
            binding.systemVolume.setVisibility(View.GONE);
        }
        if (allVolumesVisibility.get(6)) {
            binding.dtmfVolume.setVisibility(View.VISIBLE);
        } else {
            binding.dtmfVolume.setVisibility(View.GONE);
        }

        if (!allVolumesVisibility.get(7)) {
            if (noVolumesCheckedMessage == null) {
                noVolumesCheckedMessage = new TextView(requireContext());
                noVolumesCheckedMessage.setTypeface(Typeface.DEFAULT_BOLD);
                noVolumesCheckedMessage.setTextSize(16.0f);
                noVolumesCheckedMessage.setGravity(Gravity.CENTER);
                noVolumesCheckedMessage.setText("No Volumes Selected in Settings. Please select the Volumes in Settings that you want to see and control here.");
                binding.cardView.addView(noVolumesCheckedMessage);
            } else {
                if (!noVolumesCheckedMessage.isAttachedToWindow()) {
                    binding.cardView.addView(noVolumesCheckedMessage);
                }
            }
        } else {
            binding.cardView.removeView(noVolumesCheckedMessage);
        }


        requireActivity().sendBroadcast(new Intent("refreshFirstFragmentSeekBars"));

        int ringerMode = audioManager.getRingerMode();
        if (ringerMode == 0 | (audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0 && ringerMode != 1)) {
            binding.imageView3.setImageResource(R.drawable.notifications_off_35);
        } else if (ringerMode == 1) {
            binding.imageView3.setImageResource(R.drawable.vibration_35);
        } else if (ringerMode == 2) {
            binding.imageView3.setImageResource(R.drawable.notifications_on_35);
        }


        if (notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_ALL) {
            binding.imageButton.setImageResource(R.drawable.do_not_disturb_on_35);
        } else {
            binding.imageButton.setImageResource(R.drawable.baseline_do_not_disturb_off_35);
        }

        if (!MainActivity.adView.isAttachedToWindow()) {
            binding.adFrameLayout.addView(MainActivity.adView);
        }

        broadcastAndOtherCommonMethods.setMusicIconWhenLayoutRefreshed(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), binding.imageView);

    }

    @Override
    public void onPause() {
        super.onPause();

        isFirstFragmentResumed = false;

        mediaRouter.removeCallback(audioData.callback);

        if (MainActivity.adView.isAttachedToWindow()) {
            binding.adFrameLayout.removeView(MainActivity.adView);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null) {
            requireActivity().unregisterReceiver(broadcastReceiver);
        }
        if (headsetPluggedUnpluggedBroadcast != null) {
            requireActivity().unregisterReceiver(headsetPluggedUnpluggedBroadcast);
        }
        if (refreshFirstFragmentSeekbars != null ) {
            requireActivity().unregisterReceiver(refreshFirstFragmentSeekbars);
        }

        audioData.callback = null;
        audioData = null;
        binding = null;
        audioManager = null;
        dialogs = null;
        sharedPreferencesVolumeLevels = null;
        noVolumesCheckedMessage = null;
        notificationManager = null;
        settingsSharedPreferences = null;
        mediaRouter = null;
    }
}
