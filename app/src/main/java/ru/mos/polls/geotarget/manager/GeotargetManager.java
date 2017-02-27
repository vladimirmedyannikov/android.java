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
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;

import ru.mos.polls.common.controller.LocationController;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.geotarget.GeotargetApiController;
import ru.mos.polls.geotarget.model.Area;

/**
 * @since 2.3.0
 */

public class GeotargetManager extends BroadcastReceiver {
    private static final long UPDATE_INTERVAL = 10 * 1000;
    private LocationManager locationManager;
    private Context context;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Position position = new Position(location);
            Log.d("Geotarget manager", position.asJson().toString());
            /**
             * поиск попадания в одну из зон
             */
            AreasManager areasManager = new PrefsAreasManager(context);
            List<Area> areas = areasManager.get();
            Area selected = null;
            for (Area area : areas) {
                int distance = Position.distance(area.getPosition(), position);
                if (area.getR() >= distance) {
                    selected = area;
                    break;
                }
            }
            /**
             * информирование о том, что пользователь в указанной зоне
             */
            if (selected != null) {
                GeotargetApiController.OnNotifyUserInAreaListener listener = new GeotargetApiController.OnNotifyUserInAreaListener() {
                    @Override
                    public void onSuccess() {
                    }
                };
                GeotargetApiController.notifyAboutUserInArea(context,
                        selected.getId(),
                        listener);
            }
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
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        execute(context);
        wl.release();
    }

    private void execute(final Context context) {
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

    public static void start(Context context) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, GeotargetManager.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), UPDATE_INTERVAL, pi);
    }

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
}
