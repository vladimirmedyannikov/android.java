package ru.mos.polls.geotarget.manager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.common.controller.LocationController;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.geotarget.GeotargetApiController;
import ru.mos.polls.geotarget.model.Area;

/**
 * @since 2.3.0
 */

public class GeotargetManager extends BroadcastReceiver {

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    /**
     * Интервал времени срабатывания проверки попадания пользователя в одну из геозон {@link Area}
     */
    private static final long UPDATE_INTERVAL = 10 * SECOND;

    public static void requestIgnoreBatteryOptimization(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
            }
        }
    }

    /**
     * Использовать для запуска механизма геотаргетированных голосований
     * @param context {@link Context}
     */
    public static void start(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, GeotargetManager.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        long durationTime = SystemClock.elapsedRealtime() + UPDATE_INTERVAL;
        if (Build.VERSION.SDK_INT >= 23) {
            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, durationTime, pi);
        } else if (Build.VERSION.SDK_INT >= 19) {
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, durationTime, pi);
        } else {
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, durationTime, pi);
        }
    }

    /**
     * Использовать для отсановки механизма геотаргетированных голосований
     * @param context
     */
    public static void stop(Context context) {
        Intent intent = new Intent(context, GeotargetManager.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public static boolean hasFineLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasCoarseLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private LocationManager locationManager;
    private Context context;
    private PowerManager.WakeLock wakeLock;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {Position position = new Position(location);
            Log.d("Geotarget manager", position.asJson().toString());
            processUserInArea(position);
            unSubscribeOnLocation();
            wakeLock.release();
            start(context);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && pm.isDeviceIdleMode()) {
            Log.d("Geotarget manager", "doze mode = idle");
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (hasFineLocationPermission(context) && hasCoarseLocationPermission(context)) {
                Position position = new Position(locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                );
                Log.d("Geotarget manager", position.asJson().toString());
                processUserInArea(position);
            }
            wakeLock.release();
            start(context);
        } else {
            Log.d("Geotarget manager", "doze mode active");
            subscribeOnLocation();
        }
    }

    /**
     * Поиск {@link AreasManager} геозоны {@link Area}, в которой  может находиться пользлователь
     *
     * @param position текущее местоположение пользователя {@link Position}
     */
    private void processUserInArea(Position position) {
        /**
         * поиск попадания в одну из зон
         */
        AreasManager areasManager = new PrefsAreasManager(context);
        List<Area> areas = areasManager.get();
        List<Area> selectedAreas = new ArrayList<>();
        for (Area area : areas) {
            int distance = Position.distance(area.getPosition(), position);
            if (area.getR() >= distance) {
                selectedAreas.add(area);
            }
        }
        /**
         * информирование о том, что пользователь в указанной зоне
         */
        GeotargetApiController.OnNotifyUserInAreaListener listener = new GeotargetApiController.OnNotifyUserInAreaListener() {
            @Override
            public void onSuccess() {
            }
        };
        GeotargetApiController.notifyAboutUserInArea(context,
                selectedAreas,
                listener);

    }

    private void subscribeOnLocation() {
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (hasFineLocationPermission(context) && hasCoarseLocationPermission(context)) {
                String provider = LocationController.isLocationGPSProviderEnabled(context) ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
                locationManager.requestLocationUpdates(provider,
                        UPDATE_INTERVAL,
                        0,
                        locationListener);
            }
        }
    }

    private void unSubscribeOnLocation() {
        if (locationManager != null && hasFineLocationPermission(context) && hasCoarseLocationPermission(context)) {
            locationManager.removeUpdates(locationListener);
        }
    }

}
