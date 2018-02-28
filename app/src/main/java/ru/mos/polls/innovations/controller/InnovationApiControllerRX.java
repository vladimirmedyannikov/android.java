package ru.mos.polls.innovations.controller;

import android.content.Context;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.innovations.model.InnovationDetails;
import ru.mos.polls.innovations.model.Rating;
import ru.mos.polls.innovations.service.NoveltyFill;
import ru.mos.polls.innovations.service.NoveltyGet;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

public class InnovationApiControllerRX {

    /**
     * Получение детальной информации о новинке по ее идентификаторы
     *
     * @param noveltyId идентификатор объекта городской новинки
     */
    public static void get(CompositeDisposable disposable, Context context, final long noveltyId, final InnovationApiControllerRX.InnovationListener innovationListener) {
        HandlerApiResponseSubscriber<NoveltyGet.Response.Result> handler = new HandlerApiResponseSubscriber<NoveltyGet.Response.Result>(context) {

            @Override
            protected void onResult(NoveltyGet.Response.Result result) {
                if (result.getDetails() != null) {
                    innovationListener.onLoaded(result.getDetails());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                innovationListener.onError();
            }
        };
        disposable.add(AGApplication
                .api
                .noveltyGet(new NoveltyGet.Request(noveltyId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public static void fill(CompositeDisposable disposable, Context context, long noveltyId, int rating, final FillNoveltyListener fillNoveltyListener) {
        HandlerApiResponseSubscriber<NoveltyFill.Response.Result> handler = new HandlerApiResponseSubscriber<NoveltyFill.Response.Result>(context) {

            @Override
            protected void onResult(NoveltyFill.Response.Result result) {
                fillNoveltyListener.onSuccess(result.getRating(), result.getMessage(), result.getStatus().getCurrentPoints());
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                fillNoveltyListener.onError();
            }
        };
        disposable.add(AGApplication
                .api
                .noveltyFill(new NoveltyFill.Request().setNoveltyId(noveltyId).setUserRating(rating))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public interface InnovationListener {
        void onLoaded(InnovationDetails innovationDetails);

        void onError();
    }

    public interface FillNoveltyListener {
        void onSuccess(Rating rating, QuestMessage message, int allPoints);

        void onError();
    }
}
