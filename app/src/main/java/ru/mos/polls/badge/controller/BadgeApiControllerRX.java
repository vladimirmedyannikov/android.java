package ru.mos.polls.badge.controller;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.badge.controller.service.BadgesGet;
import ru.mos.polls.badge.controller.service.BadgesUpdate;
import ru.mos.polls.badge.model.Badge;
import ru.mos.polls.badge.model.State;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;


public class BadgeApiControllerRX {
    /**
     * Получаем текущие данные по бейджам с сервера
     *
     * @param listener    callback
     */
    public static void refresh(CompositeDisposable disposable, final BadgesListener listener) {
        HandlerApiResponseSubscriber<BadgesGet.Response.Result> handler = new HandlerApiResponseSubscriber<BadgesGet.Response.Result>() {
            @Override
            protected void onResult(BadgesGet.Response.Result result) {
                if (listener != null)
                    listener.onLoaded(result.getBadgeState());
            }

            @Override
            public void onError(Throwable throwable) {
                //super.onError(throwable);
            }
        };
        disposable.add(AGApplication
                .api
                .getBadges(new BadgesGet.Request())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public static void updateNews(CompositeDisposable disposable, long[] ids) {
        update(disposable, ids, Badge.Type.NEWS);
    }

    public static void updateFriends(CompositeDisposable disposable, long[] ids) {
        update(disposable,ids, Badge.Type.FRIENDS);
    }

    /**
     * Помечаем бейджи как непросмотренные
     *
     * @param ids список идентификаторов нерпосмотренных бейджей
     */
    public static void update(CompositeDisposable disposable, long[] ids, Badge.Type type) {

        HandlerApiResponseSubscriber<BadgesUpdate.Response.Result> handler = new HandlerApiResponseSubscriber<BadgesUpdate.Response.Result>() {
            @Override
            protected void onResult(BadgesUpdate.Response.Result result) {

            }

            @Override
            public void onError(Throwable throwable) {
                //super.onError(throwable);
            }
        };
        disposable.add(AGApplication
                .api
                .updateBadges(new BadgesUpdate.Request(ids, type))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public interface BadgesListener {
        void onLoaded(State state);
    }
}
