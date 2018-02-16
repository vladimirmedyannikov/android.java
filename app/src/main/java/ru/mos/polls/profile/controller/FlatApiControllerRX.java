package ru.mos.polls.profile.controller;


import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.profile.controller.service.GetDistrictArea;
import ru.mos.polls.profile.controller.service.GetReference;
import ru.mos.polls.profile.model.DistrictArea;
import ru.mos.polls.profile.model.Reference;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

/**
 * Контролер для Округов и Районов
 *
 * @since 2.0.0
 */

public class FlatApiControllerRX {

    /**
     * получаем округ по номеру района
     *
     * @since 2.0.0
     */
    public static void getDistrictByArea(String areaNumber, final DistrictAreaListener districtAreaListener) {
        HandlerApiResponseSubscriber<GetDistrictArea.Response.Result> handler = new HandlerApiResponseSubscriber<GetDistrictArea.Response.Result>() {
            @Override
            protected void onResult(GetDistrictArea.Response.Result result) {
                districtAreaListener.onLoaded(result.getDistrictArea());
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                districtAreaListener.onError();
            }
        };
        AGApplication
                .api
                .getDistrictArea(new GetDistrictArea.Request(areaNumber))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    /**
     * Получаем список Округов
     * String value может быть null
     *
     * @param referenceListener
     * @since 2.0.0
     */
    public static void getDistricts(final ReferenceListener referenceListener) {
        HandlerApiResponseSubscriber<List<Reference>> handler = new HandlerApiResponseSubscriber<List<Reference>>() {
            @Override
            protected void onResult(List<Reference> result) {
                referenceListener.onLoaded(result);

            }

            @Override
            public void onError(Throwable throwable) {
                referenceListener.onError();
            }
        };
        AGApplication
                .api
                .getDistricts()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    public static void getAreas(final ReferenceListener referenceListener, String value) {
        HandlerApiResponseSubscriber<List<Reference>> handler = new HandlerApiResponseSubscriber<List<Reference>>() {
            @Override
            protected void onResult(List<Reference> result) {
                referenceListener.onLoaded(result);

            }

            @Override
            public void onError(Throwable throwable) {
                referenceListener.onError();
            }
        };

        AGApplication
                .api
                .getAreas(new GetReference.Request(value))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }


    public interface ReferenceListener {
        void onLoaded(List<Reference> referenceList);

        void onError();
    }

    public interface DistrictAreaListener {
        void onLoaded(DistrictArea districtArea);

        void onError();
    }
}
