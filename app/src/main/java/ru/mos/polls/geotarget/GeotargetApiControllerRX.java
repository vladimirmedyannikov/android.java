package ru.mos.polls.geotarget;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.geotarget.model.Area;
import ru.mos.polls.geotarget.service.GetAreas;
import ru.mos.polls.geotarget.service.UserInArea;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;


public class GeotargetApiControllerRX {

    public static void loadAreas(CompositeDisposable disposable, final OnAreasListener listener) {
        HandlerApiResponseSubscriber<GetAreas.Response.Result> handler = new HandlerApiResponseSubscriber<GetAreas.Response.Result>() {
            @Override
            protected void onResult(GetAreas.Response.Result result) {
                if (listener != null)
                    listener.onLoaded(result.getAreas());
            }

            @Override
            public void onError(Throwable throwable) {
//                super.onError(throwable);
            }
        };
        disposable.add(AGApplication
                .api
                .getAreas(new GetAreas.Request())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public static void notifyAboutUserInArea(CompositeDisposable disposable, final List<Area> areas, final OnNotifyUserInAreaListener listener) {
        HandlerApiResponseSubscriber<UserInArea.Response.Result> handler = new HandlerApiResponseSubscriber<UserInArea.Response.Result>() {
            @Override
            protected void onResult(UserInArea.Response.Result result) {
                if (listener != null)
                    listener.onSuccess(result.isSuccess(), result.getDisableAreaIds());
            }

            @Override
            public void onError(Throwable throwable) {
//                super.onError(throwable);
            }
        };
        disposable.add(AGApplication
                .api
                .userInArea(new UserInArea.Request(areas))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public interface OnAreasListener {
        void onLoaded(List<Area> loadedAreas);
    }

    public interface OnNotifyUserInAreaListener {
        void onSuccess(boolean success, List<Integer> disableAreaIds);
    }
}
