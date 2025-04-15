package com.example.musicapp.ui.settings;

import android.app.UiModeManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.musicapp.R;
import com.example.musicapp.utils.AppUtils;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String KEY_PREF_DARK_MODE = "dark_mode";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupPreferences();
    }

    private void setupPreferences() {
        SwitchPreferenceCompat darkModePref = findPreference(KEY_PREF_DARK_MODE);
        if (darkModePref != null) {
            darkModePref.setOnPreferenceChangeListener((preference, newValue) -> {
                changeTheme(preference, newValue);
                return true;
            });
        }
    }

    private void changeTheme(Preference preference, Object newValue) {
        SharedPreferences sharedPreferences = preference.getSharedPreferences();
        boolean oldDarkMode = false;
        if (sharedPreferences != null) {
            oldDarkMode = sharedPreferences.getBoolean(KEY_PREF_DARK_MODE, false);
        }
        boolean newDarkMode = Boolean.parseBoolean(newValue.toString());
        if (newDarkMode != oldDarkMode) {
            AppUtils.sIsConfigChanged = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                UiModeManager uiModeManager = requireActivity().getSystemService(UiModeManager.class);
                int uiMode = newDarkMode ? UiModeManager.MODE_NIGHT_YES : UiModeManager.MODE_NIGHT_NO;
                uiModeManager.setApplicationNightMode(uiMode);
            } else {
                int mode = newDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
                AppCompatDelegate.setDefaultNightMode(mode);
            }
        }

    }
}