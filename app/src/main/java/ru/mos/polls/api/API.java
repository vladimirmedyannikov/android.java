package ru.mos.polls.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.UUID;

import ru.mos.polls.push.AutoLoadService;
import ru.mos.polls.push.GCMHelper;


public class API {
    private static boolean isDebug = true;
    private static Token token = Token.AG;
    private static final String HOST = "https://emp.mos.ru:443/?token=";
    private static final String CLIENT_REQ_ID = "&client_req_id=";
    /**
     * с версии 2.0.0 версионность методов сервисов привязана к версиям релизов<br/>
     * в клиентском коде в классе {@link android.app.Application} необходимо вызвать метод {@link #setBuildVersionName(String)}
     */
    private static String buildVersionName = "2.5.0";

    public static void setToken(Token token) {
        API.token = token;
    }

    public static void setIsDebug(boolean isDebug) {
        API.isDebug = isDebug;
    }

    public static void setBuildVersionName(String buildVersionName) {
        API.buildVersionName = buildVersionName;
    }

    public static String getURL(String path) {
        Uri.Builder builder = Uri.parse(HOST + token.getToken(isDebug) + CLIENT_REQ_ID + getUUID()).buildUpon();
        builder.appendEncodedPath(path);

        return builder.build().toString();
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String uuidInString = uuid.toString();
        return uuidInString;
    }

    public static void registerPush(Context context) {
        if (context.checkCallingOrSelfPermission("com.google.android.c2dm.permission.RECEIVE") != PackageManager.PERMISSION_GRANTED)
            return;
        Intent intent = new Intent(context, AutoLoadService.class);
        intent.putExtra(AutoLoadService.TASK, AutoLoadService.GCM_REGISTER);
        context.startService(intent);
    }

    public static void refreshData(Context context) {
        Intent intent = new Intent(context, AutoLoadService.class);
        intent.putExtra(AutoLoadService.TASK, AutoLoadService.REFRESH_DATA);
        context.startService(intent);
    }

    public static String getGUID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(GCMHelper.PREFERENCES, Context.MODE_PRIVATE);
        String guid;
        if (prefs.contains(GCMHelper.GUID))
            guid = prefs.getString(GCMHelper.GUID, null);
        else {
            guid = UUID.randomUUID().toString();
            prefs.edit().putString(GCMHelper.GUID, guid).commit();
        }

        return guid;
    }

    public static String url(String controller, String method) {
        return String.format("v%s/%s/%s", buildVersionName, controller, method);
    }

    public interface Controller {
        String AGPROFILE = "agprofile";
    }

    public interface Methods {
        String GET_PROFILE = "getProfile";
        String SET_PROFILE = "setProfile";
    }
}
