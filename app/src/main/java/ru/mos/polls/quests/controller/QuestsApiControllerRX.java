package ru.mos.polls.quests.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.quests.controller.service.Hide;
import ru.mos.polls.quests.controller.service.HideNews;
import ru.mos.polls.quests.model.quest.AchievementQuest;
import ru.mos.polls.quests.model.quest.AdvertisementQuest;
import ru.mos.polls.quests.model.quest.BackQuest;
import ru.mos.polls.quests.model.quest.NewsQuest;
import ru.mos.polls.quests.model.quest.Quest;
import ru.mos.polls.quests.model.quest.SocialQuest;
import ru.mos.polls.rxhttp.rxapi.handle.error.ResponseErrorHandler;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;


public class QuestsApiControllerRX {
    private static ProgressDialog pd;

    public static void hideRateThisApp(CompositeDisposable disposable, Context context, HideListener hideListener, Progressable progressable) {
        setVisibilityQuest(disposable, context, SocialQuest.TYPE_SOCIAL, SocialQuest.ID_RATE_THIS_APP, true, hideListener, progressable);
    }

    public static void hideNews(CompositeDisposable disposable, Context context, long id, HideListener hideListener, Progressable progressable) {
        setVisibilityQuest(disposable, context, NewsQuest.TYPE, String.valueOf(id), true, hideListener, progressable);
    }

    public static void hideAchievement(CompositeDisposable disposable, Context context, String id, HideListener hideListener, Progressable progressable) {
        setVisibilityQuest(disposable, context, AchievementQuest.TYPE, id, true, hideListener, progressable);
    }

    public static void hideAdvertisement(CompositeDisposable disposable, Context context, long id, HideListener hideListener, Progressable progressable) {
        setVisibilityQuest(disposable, context, AdvertisementQuest.TYPE, String.valueOf(id), true, hideListener, progressable);
    }

    public static void hideAllNews(CompositeDisposable disposable, Context context, List<Quest> quests, final HideQuestListner listener, Progressable progressable) {
        if (progressable == null) {
            progressable = new Progressable() {
                @Override
                public void begin() {
                    showProgress(context, "Скрываем...");
                }

                @Override
                public void end() {
                    hideProgress();
                }
            };
        }
        final ArrayList<String> idsList = new ArrayList<>();
        for (Quest q : quests) {
            String type = ((BackQuest) q).getType();
            if (type.equals("news") || type.equals("results")) {
                idsList.add(((BackQuest) q).getId());
            }
        }
        HandlerApiResponseSubscriber<HideNews.Response.Result> handler = new HandlerApiResponseSubscriber<HideNews.Response.Result>(context, progressable) {
            @Override
            protected void onResult(HideNews.Response.Result result) {
                if (listener != null)
                    listener.hideQuests(idsList);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        };
        disposable.add(AGApplication
                .api
                .questsHideNews(new HideNews.Request(idsList))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public static void hide(CompositeDisposable disposable, Context context, BackQuest quest, HideListener listener, Progressable progressable) {
        setVisibilityQuest(disposable, context, quest, true, listener, progressable);
    }

    public static void show(CompositeDisposable disposable, Context context, BackQuest quest, HideListener listener, Progressable progressable) {
        setVisibilityQuest(disposable, context, quest, false, listener, progressable);
    }

    public static void setVisibilityQuest(CompositeDisposable disposable, Context context, BackQuest quest, boolean willBeHide, HideListener listener, Progressable progressable) {
        setVisibilityQuest(disposable, context, quest.getType(), quest.getId(), willBeHide, listener, progressable);
    }

    public static void setVisibilityQuest(CompositeDisposable disposable, Context context, String type, String id, boolean willBeHide, final HideListener listener, Progressable progressable) {
        if (progressable == null) {
            progressable = new Progressable() {
                @Override
                public void begin() {
                    showProgress(context, "Скрываем...");
                }

                @Override
                public void end() {
                    hideProgress();
                }
            };
        }
        HandlerApiResponseSubscriber<Hide.Response.Result> handler = new HandlerApiResponseSubscriber<Hide.Response.Result>(ResponseErrorHandler.STUB, progressable) {
            @Override
            protected void onResult(Hide.Response.Result result) {
                if (listener != null)
                    listener.onHide(true);
            }

            @Override
            public void onError(Throwable throwable) {
                //super.onError(throwable);
                if (listener != null)
                    listener.onHide(false);
            }
        };
        disposable.add(AGApplication
                .api
                .questsHide(new Hide.Request(type, id, willBeHide))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    private static void showProgress(Context context, String message) {
        pd = new ProgressDialog(context);
        if (!TextUtils.isEmpty(message) && !"null".equalsIgnoreCase(message)) {
            pd.setMessage(message);
        }
        pd.show();
    }

    private static void hideProgress() {
        try {
            if (pd != null) {
                pd.dismiss();
            }
        } catch (Exception ignored) {
        }
    }

    public interface HideListener {
        void onHide(boolean isHide);
    }

    public interface HideQuestListner {
        void hideQuests(ArrayList<String> idsList);
    }
}
