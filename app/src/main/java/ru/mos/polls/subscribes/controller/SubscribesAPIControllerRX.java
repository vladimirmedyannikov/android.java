package ru.mos.polls.subscribes.controller;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.Statistics;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.profile.service.model.EmptyResult;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.subscribes.manager.SubscribeManager;
import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;
import ru.mos.polls.subscribes.service.SubscriptionService;

/**
 * Created by Trunks on 22.02.2018.
 */

public class SubscribesAPIControllerRX {

    public static void setEmailsSubscribe(CompositeDisposable disposable, Context context, String email, List<Subscription> subscriptions, final SetEmailListener listener) {
        HandlerApiResponseSubscriber<SubscriptionService.Response[]> handler = new HandlerApiResponseSubscriber<SubscriptionService.Response[]>(context, null) {
            @Override
            protected void onResult(SubscriptionService.Response[] responses) {
                if (listener != null) {
                    listener.onSaved(null);
                }
            }

            @Override
            public void onErrorListener() {
                if (listener != null) {
                    listener.onError();
                }
            }
        };
        SubscriptionService.RequestSetEmail requestSetEmail = new SubscriptionService.RequestSetEmail();
        requestSetEmail.setPersonal(email);
        requestSetEmail.setSubscriptions(subscriptions);
        disposable.add(AGApplication
                .api
                .setUserEMail(requestSetEmail)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public static void loadSubscribes(CompositeDisposable disposable, Context context, String[] types, long pollId, long eventId, final StateListener stateListener) {
        HandlerApiResponseSubscriber<JsonObject> handler = new HandlerApiResponseSubscriber<JsonObject>(context, null) {
            @Override
            protected void onResult(JsonObject result) {
                List<Subscription> subscriptions = new ArrayList<Subscription>();
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.toString());
                        JSONArray subscriptionsJsonArray = jsonObject.optJSONArray("subscriptions");
                        for (int i = 0; i < subscriptionsJsonArray.length(); i++) {
                            JSONObject subscriptionJsonObject = subscriptionsJsonArray.optJSONObject(i);
                            Subscription subscription = Subscription.fromJson(subscriptionJsonObject);
                            subscriptions.add(subscription);
                        }
                    } catch (JSONException e) {
                        if (stateListener != null) {
                            stateListener.onError();
                        }
                    }
                }
                if (stateListener != null) {
                    stateListener.onSubscriptionsState(subscriptions);
                }
            }

            @Override
            public void onErrorListener() {
                if (stateListener != null) {
                    stateListener.onError();
                }
            }
        };

        SubscriptionService.LoadSubscription request = new SubscriptionService.LoadSubscription();
        if (types != null) {
            request.setSubscriptionTypes(types);
        }
        if (pollId != -1) {
            request.setPollId(pollId);
        }
        if (eventId != -1) {
            request.setEventId(eventId);
        }
        disposable.add(AGApplication
                .api
                .getSubscriptions(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public static void saveAllSubscribes(CompositeDisposable disposable, Context context, List<Subscription> subscriptions) {
        saveSubscribes(disposable, context, subscriptions, null, null, null);
    }

    public static void saveSubscribesForPolls(CompositeDisposable disposable, Context context, List<Subscription> subscriptions, long[] pollIds, final SaveListener saveListener) {
        saveSubscribes(disposable, context, subscriptions, pollIds, null, saveListener);
    }

    public static void saveSubscribesForEvents(CompositeDisposable disposable, Context context, List<Subscription> subscriptions, long[] eventIds, final SaveListener saveListener) {
        saveSubscribes(disposable, context, subscriptions, null, eventIds, saveListener);
    }

    public static void loadAllSubscribes(CompositeDisposable disposable, Context context, final StateListener stateListener) {
        loadSubscribes(disposable, context, null, -1, -1, stateListener);
    }

    public static void loadEventSubscribes(CompositeDisposable disposable, Context context, long eventId, final StateListener stateListener) {
        String[] types = new String[]{Subscription.TYPE_EVENT_APPROACHING};
        loadSubscribes(disposable, context, types, -1, eventId, stateListener);
    }

    /**
     * подписка на голосвания или мепроприятия, указанных идентификаторов
     *
     * @param subscriptions
     * @param pollIds
     */
    public static void saveSubscribes(CompositeDisposable disposable, Context context, List<Subscription> subscriptions, long[] pollIds, long[] eventIds, final SaveListener saveListener) {
        JSONArray subscriptionsJsonArray = new JSONArray();
        for (Subscription subscription : subscriptions) {
            JSONObject subscriptionJsonObject = subscription.asJson();
            /**
             * Список голосований, на которые подписывается пользователь
             */
            if (pollIds != null) {
                JSONArray pollIdArray = new JSONArray();
                for (long id : pollIds) {
                    pollIdArray.put(id);
                }
                JSONObject paramsJson = new JSONObject();
                try {
                    paramsJson.put("poll_ids", pollIdArray);
                    subscriptionJsonObject.put("params", paramsJson);
                } catch (JSONException ignored) {
                }
            }
            /**
             * Список мероприятий , на которые подписывается пользователь
             */
            if (eventIds != null) {
                JSONArray pollIdArray = new JSONArray();
                for (long id : eventIds) {
                    pollIdArray.put(id);
                }
                JSONObject paramsJson = new JSONObject();
                try {
                    paramsJson.put("event_ids", pollIdArray);
                    subscriptionJsonObject.put("params", paramsJson);
                } catch (JSONException ignored) {
                }
            }
            subscriptionsJsonArray.put(subscriptionJsonObject);
        }
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonObject = (JsonArray) jsonParser.parse(subscriptionsJsonArray.toString());

        HandlerApiResponseSubscriber<EmptyResult[]> handler = new HandlerApiResponseSubscriber<EmptyResult[]>(context) {
            @Override
            protected void onResult(EmptyResult[] result) {
                if (saveListener != null) {
                    saveListener.onSaved();
                }
            }

            @Override
            public void onErrorListener() {
                if (saveListener != null) {
                    saveListener.onError();
                }
            }
        };
        SubscriptionService.SaveSubscription request = new SubscriptionService.SaveSubscription();
        request.setSubscriptions(jsonObject);
        disposable.add(AGApplication.api
                .setSubscriptions(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public static void saveSubscribes(CompositeDisposable disposable, Context context, long pollId, boolean isHearing, final SaveListener saveListener) {
        JSONObject requestJsonObject = SubscribeManager.getSubscribe(context, pollId, isHearing);
        HandlerApiResponseSubscriber<EmptyResult[]> handler = new HandlerApiResponseSubscriber<EmptyResult[]>(context) {
            @Override
            protected void onResult(EmptyResult[] result) {
                if (saveListener != null) {
                    saveListener.onSaved();
                }
            }

            @Override
            public void onErrorListener() {
                if (saveListener != null) {
                    saveListener.onError();
                }
            }
        };
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(requestJsonObject.optJSONArray("subscriptions").toString());
        SubscriptionService.SaveSubscription request = new SubscriptionService.SaveSubscription();
        request.setSubscriptions(jsonArray);
        disposable.add(AGApplication.api
                .setSubscriptions(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    /**
     * Отправка статистики на Flurry для экрана подписок из профиля
     *
     * @param subscriptions - список подписок, осхраненных на СС
     */
    public static void sendStatisticsForProfile(List<Subscription> subscriptions) {
        for (Subscription subscription : subscriptions) {
            if (subscription != null) {
                for (Channel channel : subscription.getChannels()) {
                    if (channel != null) {
                        Statistics.subscriptionsProfile(subscription, channel);
                        GoogleStatistics.Subscribe.subscriptionsProfile(subscription, channel);
                    }
                }
            }
        }
    }

    /**
     * Отправка статистики на Flurry для диалога подписок при прохождении опроса
     *
     * @param subscriptions - список подписок
     * @param pollId        - идентификатор опроса, для которого сохранены подписки
     */
    public static void sendStatisticsForPoll(List<Subscription> subscriptions, long pollId) {
        for (Subscription subscription : subscriptions) {
            if (subscription != null) {
                for (Channel channel : subscription.getChannels()) {
                    if (channel != null) {
                        Statistics.subscriptionsPolls(subscription, channel, pollId);
                        GoogleStatistics.Subscribe.subscriptionsPolls(subscription, channel, pollId);
                    }
                }
            }
        }
    }

    public interface SaveListener {

        void onSaved();

        void onError();

    }

    public interface OnErrorInterface {
        void onError();
    }

    public interface SetEmailListener extends OnErrorInterface {
        void onSaved(QuestMessage message);
    }

    public interface StateListener {
        void onSubscriptionsState(List<Subscription> typeChanells);

        void onError();
    }
}
