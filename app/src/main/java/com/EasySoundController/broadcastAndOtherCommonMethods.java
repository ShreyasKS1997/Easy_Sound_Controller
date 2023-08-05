package com.EasySoundController;

 import android.content.Context;
import android.content.Intent;
 import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.Uri;
 import android.widget.ImageView;

 import androidx.annotation.NonNull;

 import com.google.android.gms.ads.AdRequest;
 import com.google.android.gms.ads.FullScreenContentCallback;
 import com.google.android.gms.ads.LoadAdError;
 import com.google.android.gms.ads.interstitial.InterstitialAd;
 import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class broadcastAndOtherCommonMethods {


    private static String headPhoneHeadsetOrSpeaker(AudioManager audioManagerl) {
        AudioDeviceInfo[] audioDeviceList = audioManagerl.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        String type = "none";
        for (AudioDeviceInfo i : audioDeviceList) {
            int id = i.getType();
            if (id == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) {
                type = "headphone";
            } else if (id == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                type = "headset";
            } else if (id == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                type = "bluetoothHeadset";
            }
        }

        return type;
    }

    private static String headsetPluggedBroadcast(AudioManager audioManager) {
        String headPhoneHeadsetOrNone = headPhoneHeadsetOrSpeaker(audioManager);
        switch (headPhoneHeadsetOrNone) {
            case "headphone":
                return "headphone";
            case "headset":
                return "headset";
            case "bluetoothHeadset":
                return "bluetoothHeadset";
            default:
                return "musicIcon";
        }
    }

    static void setMusicIcon(AudioManager audioManager, ImageView imageView) {
        String headSetHeadphoneOrNone = headsetPluggedBroadcast(audioManager);
        if (imageView != null) {
            switch (headSetHeadphoneOrNone) {
                case "headphone":
                    imageView.setImageResource(R.drawable.headphones_35);
                    MainActivity.audioOutputType = "headphone";
                    break;
                case "headset":
                    imageView.setImageResource(R.drawable.headset_mic_35);
                    MainActivity.audioOutputType = "headset";
                    break;
                case "bluetoothHeadset":
                    imageView.setImageResource(R.drawable.bluetooth_audio_35);
                    MainActivity.audioOutputType = "bluetoothHeadset";
                    break;
                default:
                    if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) >= 1) {
                        imageView.setImageResource(R.drawable.music_on_35);
                    } else {
                        imageView.setImageResource(R.drawable.music_off_35);
                    }
                    MainActivity.audioOutputType = "musicIcon";
                    break;
            }
        }
    }

    public static void openOnAppStores(Context context, String marketURL, String webURL) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketURL + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webURL + context.getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    static void loadInAd(Context context) {
        AdRequest interstitialAdRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, "ca-app-pub-3940256099942544/1033173712", interstitialAdRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                MainActivity.minterstitialAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                MainActivity.minterstitialAd = interstitialAd;
                MainActivity.minterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }
                });
            }
        });
    }

}
