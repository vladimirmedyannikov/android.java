package ru.mos.polls.geotarget.job;

import android.Manifest;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.polls.common.controller.LocationController;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.geotarget.GeotargetApiController;
import ru.mos.polls.geotarget.manager.AreasManager;
import ru.mos.polls.geotarget.manager.PrefsAreasManager;
import ru.mos.polls.geotarget.model.Area;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 21.03.17 12:07.
 */

public class GeotargetJobService extends JobService {
    public static final String TAG = "geotarget job service";
    public static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private LocationController locationController;
    private boolean isYetLocationSent;

    @Override
    public boolean onStartJob(JobParameters job) {
        toLog("start");
        isYetLocationSent = false;
        boolean isLocationEnable = LocationController.isLocationNetworkProviderEnabled(this) || LocationController.isLocationGPSProviderEnabled(this);
        boolean hasGPSPermissions = EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        toLog(String.format("settings location enable = %s, has GPS permission = %s", isLocationEnable, hasGPSPermissions));
        if (isLocationEnable && hasGPSPermissions) {
            locationController = LocationController.getInstance(this);
            locationController.connect();
            locationController.setOnPositionListener(new LocationController.OnPositionListener() {
                @Override
                public void onGet(Position position) {
                    toLog(position != null ? position.asJson().toString() : "location null");
                    toLog("location receive in " + SDF.format(System.currentTimeMillis()));
                    if (!isYetLocationSent) {
                        isYetLocationSent = true;
                        try {
                            processUserInArea(position);
                            locationController.disconnect();
                            locationController = null;
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
        } else {
            stopSelf();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationController != null) {
            locationController.disconnect();
        }
        toLog("stop");
    }

    private void toLog(String state) {
        Log.d(TAG, state + " in " + SDF.format(System.currentTimeMillis()));
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

        AreasManager areasManager = new PrefsAreasManager(this);
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
                stopSelf();
            }
        };
        GeotargetApiController.notifyAboutUserInArea(this,
                selectedAreas,
                listener);
    }
}
