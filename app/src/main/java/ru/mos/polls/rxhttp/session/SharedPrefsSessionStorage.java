package ru.mos.polls.rxhttp.session;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

class SharedPrefsSessionStorage implements SessionStorage {

    private static final String PREFS = "session_storage_prefs";
    private static final String SESSION = "session";
    private Context context;

    public SharedPrefsSessionStorage(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public String getSession() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(SESSION, "");
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void setSession(@Nullable String session) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(SESSION, session).commit();
    }

    @Override
    public boolean hasSession() {
        String session = getSession();
        return !TextUtils.isEmpty(session);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void clear() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

}
