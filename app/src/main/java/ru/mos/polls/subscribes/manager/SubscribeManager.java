package ru.mos.polls.subscribes.manager;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;


public class SubscribeManager {
    public static final String PREFS = "subscribe_prefs";
    private static final String SUBSCRIBE = "subscribe_%s";
    private static final String DEFAULT_VALUE = "";

    public static void save(Context context, long pollId, boolean isHearing, List<Subscription> subscriptions) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String key = getKey(pollId);
        String lastKey = getLastKey();
        JSONArray subscriptionsArray = new JSONArray();
        long[] pollIds = new long[]{pollId};
        for (Subscription subscription : subscriptions) {
            JSONObject subscriptionJson = subscription.asJson();
            addPollIdsToSubscription(subscriptionJson, pollIds, isHearing);
            subscriptionsArray.put(subscriptionJson);
        }
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("subscriptions", subscriptionsArray);
        } catch (JSONException ignored) {
        }
        String value = resultJson.toString();
        sharedPreferences.edit().putString(key, value).putString(lastKey, value).commit();
    }

    public static JSONObject getSubscribe(Context context, long pollId, boolean isHearing) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String key = getKey(pollId);
        String subscriptionsJson = sharedPreferences.getString(key, DEFAULT_VALUE);
        JSONObject result = null;
        try {
            result = new JSONObject(subscriptionsJson);
        } catch (JSONException ignored) {
        }
        if (result == null) {
            result = getLastSubscribe(context);
            JSONArray subscriptionsJsonArray = result.optJSONArray("subscriptions");
            for (int i = 0; i < subscriptionsJsonArray.length(); i++) {
                JSONObject subscribeJsonObject = subscriptionsJsonArray.optJSONObject(i);
                addPollIdsToSubscription(subscribeJsonObject, new long[]{pollId}, isHearing);
            }
        }
        return result;
    }

    /**
     * ВОзвращает последнию конфигурацию подписок. Если таковой нет, то конфигурацию по умолчанию.
     *
     * @param context
     * @return
     */
    private static JSONObject getLastSubscribe(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String key = getLastKey();
        String subscriptionsJson = sharedPreferences.getString(key, DEFAULT_VALUE);
        JSONObject result = null;
        try {
            result = new JSONObject(subscriptionsJson);
        } catch (JSONException ignored) {
        }
        if (result == null) {
            result = getDefaultSubscribe();
        }
        return result;
    }

    private static JSONObject getDefaultSubscribe() {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        List<Channel> channels = new ArrayList<Channel>();
        channels.add(new Channel(Channel.CHANNEL_EMAIL, false));
        channels.add(new Channel(Channel.CHANNEL_PUSH, true));
        subscriptions.add(new Subscription(Subscription.TYPE_POLL_DECISIONS, channels));
        subscriptions.add(new Subscription(Subscription.TYPE_POLL_EFFECTED, channels));
        subscriptions.add(new Subscription(Subscription.TYPE_POLL_RESULTS, channels));

        JSONArray subscriptionsArray = new JSONArray();

        for (Subscription subscription : subscriptions) {
            JSONObject subscriptionJson = subscription.asJson();
            subscriptionsArray.put(subscriptionJson);
        }
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("subscriptions", subscriptionsArray);
        } catch (JSONException ignored) {
        }
        return resultJson;
    }

    public static void clear(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }

    public static void clear(Context context, long pollId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String key = getKey(pollId);
        sharedPreferences.edit().remove(key);
    }

    private static String getKey(long pollId) {
        String key = String.format(SUBSCRIBE, pollId);
        return key;
    }

    /**
     * С этим ключом хранится предыдущее значение
     *
     * @return
     */
    private static String getLastKey() {
        String key = String.format(SUBSCRIBE, "last");
        return key;
    }

    private static void addPollIdsToSubscription(JSONObject subscriptionJson, long[] pollIds, boolean isHearing) {
        if (pollIds != null) {
            JSONArray pollIdArray = new JSONArray();
            for (long id : pollIds) {
                pollIdArray.put(id);
            }
            JSONObject paramsJson = new JSONObject();
            try {
                paramsJson.put(isHearing ? "hearing_ids" : "poll_ids", pollIdArray);
                subscriptionJson.put("params", paramsJson);
            } catch (JSONException ignored) {
            }
        }
    }

}
