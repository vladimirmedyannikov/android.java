package ru.mos.polls.geotarget.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @since 2.3.0
 */

public class GpsRequestPermsManager {
    private static final String PREFS = "gps_request_perms_manager_prefs";
    private static final String SYNC_TIME = "sync_time";
    private static final long GPS_PERMISSION_REQUEST = 14 * 24 * 60 * 60 * 1000;

    public static boolean isNeedRequestGps(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long syncTime =  prefs.getLong(SYNC_TIME, System.currentTimeMillis());
        return System.currentTimeMillis() >= syncTime;
    }

    public static void incrementSyncTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putLong(SYNC_TIME, System.currentTimeMillis() + GPS_PERMISSION_REQUEST)
                .apply();
    }
}
