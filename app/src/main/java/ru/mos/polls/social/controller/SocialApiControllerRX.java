package ru.mos.polls.social.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.Statistics;
import ru.mos.polls.AGApplication;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;
import ru.mos.polls.social.controller.service.GetSocialProfile;
import ru.mos.polls.social.controller.service.LoadPostingData;
import ru.mos.polls.social.controller.service.NotifyAboutPosting;
import ru.mos.polls.social.controller.service.ProfileUpdateSocial;
import ru.mos.polls.social.model.AppPostItem;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.social.model.AppSocial;
import ru.mos.polls.social.model.Message;
import ru.mos.polls.social.storable.AppStorable;
import ru.mos.social.model.Configurator;

/**
 * Инкапсулирует работу с сервисами АГ для социальных сетей:
 * 1) отправка уведомления на сс, что пост выполнен:
 * 2) привязка данных социальной сети к аккаунту АГ;
 * 3) получение данных социальных сетей, привязанных к аккаунту АГ
 * <p/>
 */
public class SocialApiControllerRX {
    private static ProgressDialog pd;

    /**
     * Получение данных для постинга для главного экрана
     *
     * @param listener callback для отображения списка соц сетей
     */
    public static void loadPostingData(CompositeDisposable disposable, Context context, SocialPostValueListener listener, Progressable progressable) {
        loadPostingData(disposable, context, -1, AppPostValue.Type.TASK, listener, progressable);
    }

    public static void loadPostingDataForAchievement(CompositeDisposable disposable, Context context, String achievementId, SocialPostValueListener listener, Progressable progressable) {
        loadPostingData(disposable, context, achievementId, AppPostValue.Type.ACHIEVEMENT, listener, progressable);
    }

    public static void loadPostingDataForNovelty(CompositeDisposable disposable, Context context, long noveltyId, SocialPostValueListener listener, Progressable progressable) {
        loadPostingData(disposable, context, noveltyId, AppPostValue.Type.NOVELTY, listener, progressable);
    }

    /**
     * Получение данных для постинга о прохождении голосования
     *
     * @param pollId   идентификатор голосования
     * @param listener callback для отображения списка соц сетей
     */
    public static void loadPostingDataForPoll(CompositeDisposable disposable, Context context, long pollId, boolean isHearing, SocialPostValueListener listener, Progressable progressable) {
        loadPostingData(disposable, context, pollId, isHearing ? AppPostValue.Type.HEARING : AppPostValue.Type.POLL, listener, progressable);
    }

    /**
     * Получение данных для постинга об отметкина мероприятии
     *
     * @param eventId  идентификатор мероприятия, на котором отметились
     * @param listener callback для отображения списка соц сетей
     */
    public static void loadPostingDataForEvent(CompositeDisposable disposable, Context context, long eventId, SocialPostValueListener listener, Progressable progressable) {
        loadPostingData(disposable, context, eventId, AppPostValue.Type.CHECK_IN, listener, progressable);
    }

    /**
     * Получение данных для постинга с сс
     *
     * @param id       идентификатор пройденного голосования
     * @param type     тип постинга
     * @param listener callback для отображения списка соц сетей
     */
    private static void loadPostingData(CompositeDisposable disposable, Context context, final Object id, final AppPostValue.Type type, final SocialPostValueListener listener, Progressable progressable) {
        if (progressable == null) {
            progressable = new Progressable() {
                @Override
                public void begin() {
                    showProgress(context, "Загружаем...");
                }

                @Override
                public void end() {
                    hideProgress();
                }
            };
        }
        HandlerApiResponseSubscriber<LoadPostingData.Response.Result> handler = new HandlerApiResponseSubscriber<LoadPostingData.Response.Result>(context, progressable) {
            @Override
            protected void onResult(LoadPostingData.Response.Result result) {
                if (listener != null)
                    listener.onLoaded(result.getSocial().getPostItems(context, type, id));
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        };
        disposable.add(AGApplication
                .api
                .getSocialInfo(new LoadPostingData.Request())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    /**
     * Оповещение сс Аг о постинге в соцсеть
     *
     * @param context      не elk ActionBarActivity, а обычный контекст, поэтому делаем обычный запрос
     * @param appPostValue данные, которые запостили
     * @param listener     callback
     */
    public static void notifyAboutPosting(CompositeDisposable disposable, final Context context,
                                          final AppPostValue appPostValue, final PostingNotifyListener listener, Progressable progressable) {
        if (progressable == null) {
            progressable = new Progressable() {
                @Override
                public void begin() {
                    showProgress(context, null);
                }

                @Override
                public void end() {
                    hideProgress();
                }
            };
        }
        HandlerApiResponseSubscriber<NotifyAboutPosting.Response.Result> handler = new HandlerApiResponseSubscriber<NotifyAboutPosting.Response.Result>(context, progressable) {
            @Override
            protected void onResult(NotifyAboutPosting.Response.Result result) {
                if (appPostValue != null) {
                    appPostValue.setEnable(false);
                    SocialUIController.SocialAdapterHolder currentSocialAdapter
                            = SocialUIController.getCurrentSocialAdapterHolder();
                    if (currentSocialAdapter != null) {
                        currentSocialAdapter.refreshSocialListView(appPostValue);
                    }
                }

                if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                    if (listener != null) {
                        listener.onNotified(result.getMessage());
                    }
                    return;
                }
                /**
                 * если кастомный диалог не отобразили, то показываем то, что в теге result
                 */
                Map<String, String> stParams = new HashMap<String, String>();
                stParams.put("name", AppSocial.getStatisticsKey(appPostValue.getSocialId()));
                Statistics.customEvent("social post", stParams);
                int points = result.getStatus().getCurrentPoints();
                String pointsTitle = PointsManager.getPointUnitString(context, points);
                String text = String.format(context.getString(R.string.post_added), String.valueOf(points), pointsTitle);
                if (listener != null) {
                    listener.onNotified(text);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                /**
                 * пользователю не выводим ошибку,
                 * так как ему об этом знать не надо
                 */
            }
        };
        disposable.add(AGApplication
                .api
                .notifyAboutPosting(new NotifyAboutPosting.Request(appPostValue))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));

    }

    /**
     * Получение сохранненых ранее соц сетей
     *
     * @param listener
     */
    public static void loadSocials(CompositeDisposable disposable, final Context context, final LoadSocialListener listener, Progressable progressable) {
        if (progressable == null) {
            progressable = new Progressable() {
                @Override
                public void begin() {
                    showProgress(context, "Загружаем...");
                }

                @Override
                public void end() {
                    hideProgress();
                }
            };
        }
        HandlerApiResponseSubscriber<GetSocialProfile.Response.Result> handler = new HandlerApiResponseSubscriber<GetSocialProfile.Response.Result>(context, progressable) {
            @Override
            protected void onResult(GetSocialProfile.Response.Result result) {
                List<AppSocial> savedSocial = ((AppStorable) Configurator.getInstance(context).getStorable()).getAll();
                List<AppSocial> newSocials = new ArrayList<AppSocial>();
                add(newSocials, AppSocial.findFbSocial(savedSocial), result.getFb());
                add(newSocials, AppSocial.findVkSocial(savedSocial), result.getVk());
                add(newSocials, AppSocial.findTwSocial(savedSocial), result.getTwitter());
                add(newSocials, AppSocial.findOkSocial(savedSocial), result.getOk());
                if (listener != null) {
                    listener.onLoaded(newSocials);
                }
            }

            private void add(List<AppSocial> newSocials, AppSocial savedSocial, AppSocial newSocial) {
                if (!newSocial.getToken().isEmpty()) {
                    Configurator.getInstance(context).getStorable().save(newSocial);
                    newSocials.add(newSocial);
                } else {
                    savedSocial.setIsLogin(false);
                    Configurator.getInstance(context).getStorable().save(newSocial);
                    newSocials.add(savedSocial);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (listener != null) {
                    listener.onError();
                }
            }
        };
        disposable.add(AGApplication
                .api
                .getSocials(new GetSocialProfile.Request())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    /**
     * Отвязать указанную соцсеть от профиля аг
     *
     * @param social
     * @param listener
     */
    public static void unbindSocialFromAg(CompositeDisposable disposable, final Context context, AppSocial social, SaveSocialListener listener, Progressable progressable) {
        binding(disposable, context, social, false, listener, progressable);
    }

    /**
     * Привязка указанной соцсети к профилю аг
     *
     * @param social
     * @param listener
     */
    public static void bindSocialToAg(CompositeDisposable disposable, final Context context, AppSocial social, SaveSocialListener listener, Progressable progressable) {
        binding(disposable, context, social, true, listener, progressable);
    }


    /**
     * Привязка данных социальной сети к аккаунту АГ
     *
     * @param social   данные соцсети
     * @param listener callback
     * @param forBind  признак привязки или отвязки соцсети
     */
    public static void binding(CompositeDisposable disposable, final Context context, final AppSocial social, final boolean forBind, final SaveSocialListener listener, Progressable progressable) {
        if (progressable == null) {
            progressable = new Progressable() {
                @Override
                public void begin() {
                    showProgress(context, "Связываем...");
                }

                @Override
                public void end() {
                    hideProgress();
                }
            };
        }
        HandlerApiResponseSubscriber<ProfileUpdateSocial.Response.Result> handler = new HandlerApiResponseSubscriber<ProfileUpdateSocial.Response.Result>(context, progressable) {
            @Override
            protected void onResult(ProfileUpdateSocial.Response.Result result) {
                if (!forBind) {
                    if (listener != null)
                        listener.onSaved(social, 0, 0, 0, 0, "", result.getPercentFillProfile());
                } else {
                    if (listener != null) {
                        try {
                            listener.onSaved(social, result.getStatus().getFreezedPoints(),
                                    result.getStatus().getSpentPoints(),
                                    (int) result.getStatus().getAllPoints(),
                                    result.getStatus().getCurrentPoints(),
                                    result.getStatus().getState(),
                                    result.getPercentFillProfile());
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (listener != null) {
                    listener.onError(social);
                }
            }
        };
        disposable.add(AGApplication
                .api
                .updateSocial(new ProfileUpdateSocial.Request(social, forBind))
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

    /**
     * callback получения данных для постинга
     */
    public interface SocialPostValueListener {
        void onLoaded(List<AppPostItem> appPostItems);
    }

    /**
     * callback уведомления сс АГ о постинге
     */
    public interface PostingNotifyListener {
        void onNotified(Message customMessage);

        void onNotified(String message);
    }

    /**
     * callback получения списка привязанных соцсетей к аккакнту АГ
     */
    public interface LoadSocialListener {
        void onLoaded(List<AppSocial> socials);

        void onError();
    }

    /**
     * callback привязки данных соцсетей к аккаунту АГ
     */
    public interface SaveSocialListener {
        void onSaved(AppSocial social, int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state, int percentFill);

        void onError(AppSocial social);
    }
}
