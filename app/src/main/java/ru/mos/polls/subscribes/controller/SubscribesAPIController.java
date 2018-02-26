package ru.mos.polls.subscribes.controller;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.UrlManager;
import ru.mos.polls.api.API;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.subscribes.manager.SubscribeManager;
import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;

@Deprecated
public class SubscribesAPIController {

    public void setEmailsSubscribe(BaseActivity elkActivity, String email, List<Subscription> subscriptions, final SaveListener saveListener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.SET_USER_EMAIL));
        JSONObject requestJsonObject = new JSONObject();
        try {
            /**
             * добавление типов подписок
             */
            if (subscriptions != null) {
                JSONArray jsonArrayTypes = new JSONArray();
                for (Subscription subscription : subscriptions) {
                    if (subscription.isEmailChannelEnable()) {
                        jsonArrayTypes.put(subscription.getType());
                    }
                }
                requestJsonObject.put("subscription_types", jsonArrayTypes);
            }
            JSONObject personalJson = new JSONObject();
            personalJson.put("email", email);
            requestJsonObject.put("personal", personalJson);
            Session.addSession(requestJsonObject);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if (saveListener != null) {
                    saveListener.onSaved(jsonObject);
                }
            }

        };
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (saveListener != null) {
                    saveListener.onError(volleyError);
                }
            }

        };
        JsonObjectRequest request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
        elkActivity.addRequest(request);
    }

    public void loadAllSubscribes(BaseActivity activity, final StateListener stateListener) {
        loadSubscribes(activity, null, -1, -1, stateListener);
    }

    /**
     * подписки пользвоателя
     *
     * @param activity
     * @param stateListener
     */
    public void loadEventSubscribes(BaseActivity activity, long eventId, final StateListener stateListener) {
        String[] types = new String[]{Subscription.TYPE_EVENT_APPROACHING};
        loadSubscribes(activity, types, -1, eventId, stateListener);
    }

    /**
     * GПолучаем подписки пользвоателя
     *
     * @param activity
     * @param types
     * @param stateListener
     */
    public void loadSubscribes(BaseActivity activity, String[] types, long pollId, long eventId, final StateListener stateListener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.GET_SUBSCRIPTIONS));
        JSONObject requestJsonObject = new JSONObject();
        /**
         * добавления типа подписки, если тип не задан, то
         * возвращаем подписки по всем типам
         */
        try {
            /**
             * добавление типов подписок
             */
            if (types != null) {
                JSONArray jsonArrayTypes = new JSONArray();
                for (String type : types) {
                    jsonArrayTypes.put(type);
                }
                requestJsonObject.put("subscription_types", jsonArrayTypes);
            }
            if (pollId != -1) {
                requestJsonObject.put("poll_id", pollId);
            }
            if (eventId != -1) {
                requestJsonObject.put("event_id", eventId);
            }
            Session.addSession(requestJsonObject);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                List<Subscription> subscriptions = new ArrayList<Subscription>();
                if (jsonObject != null) {
                    JSONArray subscriptionsJsonArray = jsonObject.optJSONArray("subscriptions");
                    for (int i = 0; i < subscriptionsJsonArray.length(); i++) {
                        JSONObject subscriptionJsonObject = subscriptionsJsonArray.optJSONObject(i);
                        Subscription subscription = Subscription.fromJson(subscriptionJsonObject);
                        subscriptions.add(subscription);
                    }
                }
                if (stateListener != null) {
                    stateListener.onSubscriptionsState(subscriptions);
                }
            }

        };
        Response.ErrorListener errorListener = new StandartErrorListener(activity, R.string.error_occurs) {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (stateListener != null) {
                    stateListener.onError();
                }
            }

        };
        JsonObjectRequest request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
        activity.addRequest(request);
    }

    public void saveAllSubscribes(BaseActivity activity, List<Subscription> subscriptions) {
        saveSubscribes(activity, subscriptions, null, null, null);
    }

    /**
     * Подписка на все голосования
     *
     * @param activity
     * @param subscriptions
     */
    public void saveAllSubscribes(BaseActivity activity, List<Subscription> subscriptions, SaveListener saveListener) {
        saveSubscribes(activity, subscriptions, null, null, saveListener);
    }

    public void saveSubscribesForPolls(BaseActivity activity, List<Subscription> subscriptions, long[] pollIds, final SaveListener saveListener) {
        saveSubscribes(activity, subscriptions, pollIds, null, saveListener);
    }

    public void saveSubscribesForEvents(BaseActivity activity, List<Subscription> subscriptions, long[] eventIds, final SaveListener saveListener) {
        saveSubscribes(activity, subscriptions, null, eventIds, saveListener);
    }

    /**
     * подписка на голосвания или мепроприятия, указанных идентификаторов
     *
     * @param activity
     * @param subscriptions
     * @param pollIds
     */
    public void saveSubscribes(final BaseActivity activity, List<Subscription> subscriptions, long[] pollIds, long[] eventIds, final SaveListener saveListener) {
        final ProgressDialog progressDialogSub = new ProgressDialog(activity);
        progressDialogSub.setCancelable(false);
        progressDialogSub.show();
        String url = API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.SET_SUBSCRIPTIONS));
        JSONObject requestJsonObject = new JSONObject();
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
        try {
            requestJsonObject.put("subscriptions", subscriptionsJsonArray);
            Session.addSession(requestJsonObject);
        } catch (JSONException ignored) {

        }
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (progressDialogSub != null) {
                    progressDialogSub.dismiss();
                }
                if (saveListener != null) {
                    saveListener.onSaved(jsonObject);
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(activity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (progressDialogSub != null) {
                    progressDialogSub.dismiss();
                }
                Toast.makeText(activity,
                        String.format(activity.getString(R.string.error_occurs), volleyError.getMessage()),
                        Toast.LENGTH_LONG).show();
                if (saveListener != null) {
                    saveListener.onError(volleyError);
                }
            }
        };
        JsonObjectRequest request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
        activity.addRequest(request);
    }

    public void saveSubscribes(final BaseActivity activity, long pollId, boolean isHearing, final SaveListener saveListener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.SET_SUBSCRIPTIONS));
        JSONObject requestJsonObject = SubscribeManager.getSubscribe(activity, pollId, isHearing);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (saveListener != null) {
                    saveListener.onSaved(jsonObject);
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(activity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                Toast.makeText(activity,
                        String.format(activity.getString(R.string.error_occurs), volleyError.getMessage()),
                        Toast.LENGTH_LONG).show();
                if (saveListener != null) {
                    saveListener.onError(volleyError);
                }
            }
        };
        JsonObjectRequest request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
        activity.addRequest(request);
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

    public interface StateListener {

        void onSubscriptionsState(List<Subscription> typeChanells);

        void onError();

    }

    public interface SaveListener {

        void onSaved(JSONObject jsonObject);

        void onError(VolleyError volleyError);

    }

}
