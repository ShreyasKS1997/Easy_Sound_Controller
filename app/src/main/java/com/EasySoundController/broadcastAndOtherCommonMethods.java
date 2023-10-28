package com.EasySoundController;

 import android.content.Context;
import android.content.Intent;
 import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.Uri;
 import android.widget.ImageView;
 import android.widget.Toast;

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

    static void setMusicIcon(AudioManager audioManager, Context context) {
        String headSetHeadphoneOrNone = headsetPluggedBroadcast(audioManager);
            switch (headSetHeadphoneOrNone) {
                case "headphone":
                    MainActivity.audioOutputType = "headphone";
                    break;
                case "headset":
                    MainActivity.audioOutputType = "headset";
                    break;
                case "bluetoothHeadset":
                    MainActivity.audioOutputType = "bluetoothHeadset";
                    break;
                default:
                    MainActivity.audioOutputType = "musicIcon";
                    break;
            }
    }

    static void setMusicIconWhenVolumeChanged(int lastVolume, int musicVolume, ImageView musicIconImageView) {
        if (musicVolume == 0) {
            musicIconImageView.setImageResource(R.drawable.music_off_35);
        } else if (lastVolume == 0 && musicVolume >= 1) {
            switch (MainActivity.audioOutputType) {
                case "headphone":
                    musicIconImageView.setImageResource(R.drawable.headphones_35);
                    break;
                case "headset":
                    musicIconImageView.setImageResource(R.drawable.headset_mic_35);
                    break;
                case "bluetoothHeadset":
                    musicIconImageView.setImageResource(R.drawable.bluetooth_audio_35);
                    break;
                default:
                    musicIconImageView.setImageResource(R.drawable.music_on_35);
                    break;
            }
        }
    }

    static void setMusicIconWhenLayoutRefreshed(int currentVolume, ImageView musicImageView) {
        if (currentVolume == 0) {
            musicImageView.setImageResource(R.drawable.music_off_35);
        } else {
            switch (MainActivity.audioOutputType) {
                case "headphone":
                    musicImageView.setImageResource(R.drawable.headphones_35);
                    break;
                case "headset":
                    musicImageView.setImageResource(R.drawable.headset_mic_35);
                    break;
                case "bluetoothHeadset":
                    musicImageView.setImageResource(R.drawable.bluetooth_audio_35);
                    break;
                default:
                    if (currentVolume >= 1) {
                        musicImageView.setImageResource(R.drawable.music_on_35);
                    } else {
                        musicImageView.setImageResource(R.drawable.music_off_35);
                    }
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

}
