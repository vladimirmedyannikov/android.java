package ru.mos.polls.profile.controller;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.profile.controller.service.GetAchievement;
import ru.mos.polls.profile.model.Achievement;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

/**
 * Инкапсулирует работу с сервисом по профилю
 * Здесь собраны далеко не все методы сервиса
 * <p/>
 */

public class ProfileApiControllerRX {
    public static void loadAchievement(CompositeDisposable disposable, String achievementId, final AchievementListener achievementListener) {
        HandlerApiResponseSubscriber<Achievement> handler = new HandlerApiResponseSubscriber<Achievement>() {
            @Override
            protected void onResult(Achievement result) {
                if (achievementListener != null) {
                    achievementListener.onLoaded(result);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (achievementListener != null) {
                    achievementListener.onError();
                }
            }
        };
        disposable.add(AGApplication
                .api
                .getAchievement(new GetAchievement.Request(achievementId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public interface AchievementListener {
        void onLoaded(Achievement achievement);

        void onError();
    }
}
