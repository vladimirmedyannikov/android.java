package ru.mos.polls.geotarget.job;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.List;

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
    private LocationController locationController;

    @Override
    public boolean onStartJob(JobParameters job) {
        locationController = LocationController.getInstance(this);
        locationController.connect();
        locationController.setOnPositionListener(new LocationController.OnPositionListener() {
            @Override
            public void onGet(Position position) {
                processUserInArea(position);
            }
        });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationController.disconnect();
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
