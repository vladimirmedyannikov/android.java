package ru.mos.polls.rxhttp.api.session;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class Session {
    public static final String ACTION_UNAUTHORIZED = "action unauthorized";
    public static final int ERROR_CODE_UNAUTHORIZED = 401;

    private static Session instance = null;

    public static void init(Context context) {
        instance = new Session(context);
    }

    public static SessionStorage get() {
        return instance.sessionStorage;
    }

    public static void notifyAboutSessionIsEmpty(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_UNAUTHORIZED);
        LocalBroadcastManager
                .getInstance(context)
                .sendBroadcast(intent);
    }

    private final SessionStorage sessionStorage;

    private Session(Context context) {
        sessionStorage = new SharedPrefsSessionStorage(context);
    }

}
