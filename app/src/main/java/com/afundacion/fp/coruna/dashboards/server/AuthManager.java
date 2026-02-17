package com.afundacion.fp.coruna.dashboards.server;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    // Singleton
    private static AuthManager instance = null;
    private SharedPreferences preferences;

    private static String TOKEN_KEY = "TOKEN";

    // private constructor
    private AuthManager(Context context) {
        this.preferences = context.getSharedPreferences("DASHBOARDS_PREFS", Context.MODE_PRIVATE);
    }

    // public methods
    public static AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    public void saveSessionToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.commit();
    }

    public String getSessionToken() {
        return preferences.getString(TOKEN_KEY, null);
    }

    public boolean isLoggedIn() {
        return getSessionToken() != null;
    }
}
