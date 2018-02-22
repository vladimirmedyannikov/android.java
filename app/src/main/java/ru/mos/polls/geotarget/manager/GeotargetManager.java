package ru.mos.polls.geotarget.manager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import ru.mos.polls.common.controller.LocationController;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.geotarget.GeotargetApiControllerRX;
import ru.mos.polls.geotarget.model.Area;

/**
 * Not use, see {@link ru.mos.polls.geotarget.job.GeotargetJobManager}
 * @since 2.3.0
 */
@Deprecated
public class GeotargetManager extends BroadcastReceiver {

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    /**
     * Интервал времени срабатывания проверки попадания пользователя в одну из геозон {@link Area}
     */
    private static final long UPDATE_INTERVAL = 10 * SECOND;

    /**
     * Использовать для запуска механизма геотаргетированных голосований
     * @param context {@link Context}
     */
    public static void start(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, GeotargetManager.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        long durationTime = SystemClock.elapsedRealtime() + UPDATE_INTERVAL;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, durationTime, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, durationTime, pi);
        } else {
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, durationTime, pi);
        }
    }

    /**
     * Использовать для остановки механизма геотаргетированных голосований
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
            logData(position, "active");
            processUserInArea(position);
            unSubscribeOnLocation();
//            wakeLock.release();
//            start(context);
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
                logData(position, "idle");
                processUserInArea(position);
            }
        } else {
            Log.d("Geotarget manager", "doze mode active");
            subscribeOnLocation();
        }

        wakeLock.release();
        start(context);
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
        GeotargetApiControllerRX.OnNotifyUserInAreaListener listener = new GeotargetApiControllerRX.OnNotifyUserInAreaListener() {
            @Override
            public void onSuccess(boolean success, List<Integer> disableAreaIds) {

            }
        };
        GeotargetApiControllerRX.notifyAboutUserInArea(new CompositeDisposable(), // TODO: 22.02.18 как оформить его здесь?
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

    private void logData(Position position, String state) {
        Manager.Data data = new Manager.Data();
        data.setDateTime()
                .setNetworkEnable(context)
                .setState(state)
                .setLocation(position);

        List<Manager.Data> logs = Manager.get(context);
        logs.add(data);
        Manager.save(context, logs);
    }

    /**
     * только для теста
     */
    public static class Manager {
        private static final String PREFS = "GeotargetManagerPrefs";
        private static final String DATA = "data";

        public static void save(Context context, List<Data> data) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            prefs.edit().putString(DATA, Data.from(data).toString()).commit();
        }

        public static List<Data> get(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            List<Data> result = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(prefs.getString(DATA, ""));
                result = Data.from(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        public static class Data {
            private String state;
            private String dateTime;
            private String location;
            private boolean networkEnable;

            public Data(JSONObject json) {
                if (json != null) {
                    state = json.optString("state");
                    dateTime = json.optString("dateTime");
                    location = json.optString("location");
                    networkEnable =  json.optBoolean("networkEnable", false);
                }
            }

            public Data() {
            }

            public JSONObject asJson() {
                JSONObject result = new JSONObject();
                try {
                    result.put("state", state);
                    result.put("dateTime", dateTime);
                    result.put("location", location);
                    result.put("networkEnable", networkEnable);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            public static List<Data> from(JSONArray array) {
                List<Data> result = new ArrayList<>();
                if (array != null) {
                    for (int i = 0; i < array.length(); ++i) {
                        result.add(new Data(array.optJSONObject(i)));
                    }
                }
                return result;
            }

            public static JSONArray from(List<Data> data) {
                JSONArray result = new JSONArray();
                if (data != null) {
                    for (Data iterator : data) {
                        result.put(iterator.asJson());
                    }
                }
                return result;
            }

            public Data setState(String state) {
                this.state = state;
                return this;
            }

            public Data setLocation(Position position) {
                this.location = position.asJson().toString();
                return this;
            }

            public Data setNetworkEnable(Context context) {
                ConnectivityManager cm =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                this.networkEnable = netInfo != null && netInfo.isConnectedOrConnecting();
                return this;
            }

            public Data setDateTime() {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                this.dateTime = sdf.format(System.currentTimeMillis());
                return this;
            }
        }
    }

}
