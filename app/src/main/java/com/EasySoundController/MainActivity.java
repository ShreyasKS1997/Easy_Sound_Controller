package com.EasySoundController;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;

import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.EasySoundController.databinding.ActivityMainBinding;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    static AudioManager am;
    static NotificationManager nm;
    private boolean initialLayoutComplete = false;
    static AdView adView;

    static boolean isMainActivityRunning;

    final static String[] volumesShowedInMainScreen = new String[]{"Music Volume", "Call Volume", "Ring Volume",
            "Alarm Volume", "Notification Volume", "System Volume", "Dial Tone Volume"};

    static String audioOutputType = "musicIcon";
    static int lastVolume = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isMainActivityRunning = true;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        broadcastAndOtherCommonMethods.setMusicIcon(am, getApplicationContext());

        adView = new AdView(this);
        adView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        adView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!initialLayoutComplete) {
                    initialLayoutComplete = true;
                    adView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    float adWidthPixels = adView.getWidth();
                    float density = getResources().getDisplayMetrics().density;
                    int adWidth = (int) (adWidthPixels / density);
                    AdSize adwidth = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(MainActivity.this, adWidth);
                    adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");
                    adView.setAdSize(adwidth);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("Esc_Ch_id_1","Quick Volumes Control Notification", "Control Sound quickly from Notification Bar");
            createNotificationChannel("Esc_Ch_id_2","Quick Shortcut Floating Button Service", "Control Sound quickly from Quick Shortcut Floating Button");
            createNotificationChannel("Esc_Ch_id_3","Ring Mode Change Notify Service", "Notify Ring Mode Changes");
        }

    }

    public void createNotificationChannel(String channelId, String channelName, String channelDescription) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isMainActivityRunning = false;
        adView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        am = null;
        nm = null;
        adView.destroy();
        adView = null;
        binding = null;
    }
}
