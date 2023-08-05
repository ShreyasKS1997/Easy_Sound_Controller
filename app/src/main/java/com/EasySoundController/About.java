package com.EasySoundController;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.EasySoundController.databinding.AboutBinding;

public class About extends Fragment {

    AboutBinding binding;
    Dialogs dialogs = new Dialogs();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            binding = AboutBinding.inflate(inflater, container, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar3.setNavigationIcon(R.drawable.navigate_before_35);
        binding.toolbar3.setNavigationContentDescription("Go back to home screen");

        binding.toolbar3.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            navController.navigateUp();
        });

        binding.textView21.setOnClickListener(v -> dialogs.rateUsDialog(requireContext()));

        ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        binding.textView23.setOnLongClickListener(v -> {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Easy Sound Controller contact Email", ((TextView)v).getText().toString()));
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                Toast.makeText(getContext(), "Copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        binding.termsAndConditionsLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/e/2PACX-1vQ3mM-tLm1v-VgVeECpvH_h-JI3PXmDmcINOT9ouvVc5zromR6tLh6zvQ4-zcPQqVcxFriHEPLrWKXV/pub"));
            startActivity(browserIntent);
        });
        binding.privacyPolicy.setOnClickListener(view1 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/e/2PACX-1vQQeaqZQxN68FodNMD4f3mUNjRyh9Rn75WCMmRiE2S4Vyrr3sAY_gbdp915L9Gjj9IG3DTGwNcFvOeI/pub"));
            startActivity(browserIntent);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.googleAdLayout3.addView(MainActivity.adView);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.googleAdLayout3.removeView(MainActivity.adView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialogs = null;
        binding = null;
    }
}
