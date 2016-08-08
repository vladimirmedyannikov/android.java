package ru.mos.polls.shop;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistentConfig {

    private static final String PREFS_NAME = "cookies_store";

    private SharedPreferences settings;

    public PersistentConfig(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public String getCookieString() {
        return settings.getString("cookies", "");
    }

    public void setCookie(String cookie) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("cookies", cookie);
        editor.commit();
    }

}

