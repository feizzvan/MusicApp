package com.example.musicapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREFS_NAME = "prefs";
    private static final String KEY_TOKEN = "token";
    private final SharedPreferences mSharedPreferences;

    public TokenManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        mSharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return mSharedPreferences.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        mSharedPreferences.edit().remove(KEY_TOKEN).apply();
    }
}
