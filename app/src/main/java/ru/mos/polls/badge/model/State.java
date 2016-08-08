package ru.mos.polls.badge.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Структура,описывающая:
 * 1) состояние бейджей {@link ru.mos.polls.badge.model.Badge},
 * 2) данные пользователя {@link ru.mos.polls.badge.model.Personal},
 * 3) текущий баланс
 *
 * Данные структуры можно кешировать {@link #save(android.content.Context)}
 * и соответственно читать из кеша {@link #State(android.content.Context)}
 *
 * При разлогине, чтобы удалить данные по бейджам,
 * необходимо вызвать статичный метод {@link #clear(android.content.Context)}
 *
 * @since 1.9.2
 */
public class State {
    private static final String PREFS = "badges_prefs";
    private static final String BADGES = "badres";
    private static final String POINTS_COUNT = "points_count";
    private static final String PERSONAL = "personal";

    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    private Personal personal;
    private int pointsCount;
    private List<Badge> badges;

    public State(JSONObject json) {
        if (json != null) {
            badges = Badge.fromJson(json.optJSONArray("badges"));
            personal = new Personal(json.optJSONObject("personal"));
            pointsCount = json.optInt("points_count");
        }
    }

    public State(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        try {
            JSONObject json = new JSONObject(prefs.getString(PERSONAL, ""));
            personal = new Personal(json);
            pointsCount = prefs.getInt(POINTS_COUNT, 0);
            JSONArray array = new JSONArray(prefs.getString(BADGES, ""));
            badges = Badge.fromJson(array);
        } catch (JSONException ignored) {
        }
    }

    public void save(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(PERSONAL, personal.asJson().toString())
                .putString(BADGES, badgesAsJson().toString())
                .putInt(POINTS_COUNT, pointsCount)
                .apply();
    }

    public Personal getPersonal() {
        return personal;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public List<Badge> getBadges() {
        return badges;
    }

    private JSONArray badgesAsJson() {
        JSONArray array = new JSONArray();
        for (Badge badge : badges) {
            array.put(badge.asJson());
        }
        return array;
    }
}
