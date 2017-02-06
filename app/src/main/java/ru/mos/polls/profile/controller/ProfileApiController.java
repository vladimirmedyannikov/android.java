package ru.mos.polls.profile.controller;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.Statistics;
import ru.mos.polls.UrlManager;
import ru.mos.polls.profile.model.Achievement;

/**
 * Инкапсулирует работу с сервисом по профилю
 * Здесь собраны далеко не все методы сервиса
 * <p/>
 */
public abstract class ProfileApiController {
    public static void updateProfile(BaseActivity elkActivity, JSONObject requestJsonObject) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.PROFILE_UPDATE_PERSONAL));
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Statistics.profileFillPersonal();
                GoogleStatistics.AGNavigation.profileFillPersonal();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        };
        elkActivity.addRequest(new JsonObjectRequest(url, requestJsonObject, responseListener, errorListener));
    }

    /**
     * Получение статистики поьзователя
     *
     * @param elkActivity
     * @param statisticsListener
     */
    public static void loadStatistics(BaseActivity elkActivity, final StatisticListener statisticsListener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.GET_STATISTICS));
        JSONObject requestJsonObject = new JSONObject();
        Session.addSession(requestJsonObject);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null && statisticsListener != null) {
                    ru.mos.polls.profile.model.Statistics statistics = new ru.mos.polls.profile.model.Statistics(jsonObject);
                    statisticsListener.onLoaded(statistics);
                }
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (statisticsListener != null) {
                    statisticsListener.onError(volleyError);
                }
            }

        };
        JsonObjectRequest request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
        elkActivity.addRequest(request);
    }

    /**
     * Получение списка достижений пользователя
     *
     * @param elkActivity
     * @param achievementsListener
     */
    public static void loadAchievements(BaseActivity elkActivity, final AchievementsListener achievementsListener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.SELECT_ACHIEVEMENTS));
        JSONObject requestJsonObject = new JSONObject();
        Session.addSession(requestJsonObject);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    JSONArray jsonArray = jsonObject.optJSONArray("achievements");
                    if (jsonArray != null && achievementsListener != null) {
                        List<Achievement> achievements = Achievement.fromJsonArray(jsonArray);
                        achievementsListener.onLoaded(achievements);
                    }
                }
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (achievementsListener != null) {
                    achievementsListener.onError(volleyError);
                }
            }

        };
        JsonObjectRequest request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
        elkActivity.addRequest(request);
    }

    public static void loadAchievement(BaseActivity elkActivity, final Achievement achievement, final AchievementListener achievementListener) {
        String achievementId = "";
        if (achievement != null) {
            achievementId = achievement.getId();
        }
        loadAchievement(elkActivity, achievementId, achievementListener);
    }

    /**
     * Получение детальной информации о достижении
     *
     * @param elkActivity
     * @param achievementId       идентификатор достижения
     * @param achievementListener
     */
    public static void loadAchievement(BaseActivity elkActivity, String achievementId, final AchievementListener achievementListener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.GET_ACHIEVEMENT));
        JSONObject requestJsonObject = new JSONObject();
        Session.addSession(requestJsonObject);
        try {
            requestJsonObject.put("achievement_id", achievementId);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null && achievementListener != null) {
                    Achievement result = new Achievement(jsonObject);
                    achievementListener.onLoaded(result);
                }
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (achievementListener != null) {
                    achievementListener.onError(volleyError);
                }

                //TODO убрать заглушку для 1.9, раскоментировать код выше
////                JSONObject jsonObject = StubHelper.getAchievement();
//                if (jsonObject != null && achievementListener != null) {
//                    Achievement result = new Achievement(jsonObject);
//                    achievementListener.onLoaded(result);
//                }
            }

        };
        JsonObjectRequest request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
        elkActivity.addRequest(request);
    }

    public interface StatisticListener {
        void onLoaded(ru.mos.polls.profile.model.Statistics statistics);

        void onError(VolleyError volleyError);
    }

    public interface AchievementsListener {
        void onLoaded(List<Achievement> achievements);

        void onError(VolleyError volleyError);
    }

    public interface AchievementListener {
        void onLoaded(Achievement achievement);

        void onError(VolleyError volleyError);
    }
}
