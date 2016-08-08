package ru.mos.polls.badge.controller;

import com.android.volley2.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.UrlManager;
import ru.mos.polls.badge.model.State;

/**
 * Икапсуляция получения бейджей с сервера
 *
 * @since 1.9.2
 */
public abstract class BadgeApiController {
    /**
     * Получаем текущие данные по бейджам с сервера
     *
     * @param elkActivity elk ActionBarActivity
     * @param listener    callback
     */
    public static void refresh(final BaseActivity elkActivity, final BadgesListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL_BADGES, UrlManager.Methods.GET));

        JSONObject requestJson = new JSONObject();
        Session.addSession(requestJson);

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null && listener != null) {
                    State state = new State(jsonObject);
                    listener.onLoaded(state);
                }
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, requestJson, responseListener, null);
        elkActivity.addRequest(jsonObjectRequest);
    }

    /**
     * Обновляение количества непрочитанных новостей
     *
     * @param elkActivity активити elk
     * @param ids список идентификаторов новостей
     */
    public static void updateNews(final BaseActivity elkActivity, long[] ids) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL_BADGES, UrlManager.Methods.UPDATE));
        JSONObject requestJson = new JSONObject();
        try {
            JSONArray badgesJsonArray = new JSONArray();
            JSONObject badgeJsonObject = new JSONObject();
            badgeJsonObject.put("type", "news");
            JSONArray newsIdsJsonArray = new JSONArray();
            for (long id : ids) {
                newsIdsJsonArray.put(id);
            }
            badgeJsonObject.put("ids", newsIdsJsonArray);
            badgesJsonArray.put(badgeJsonObject);
            requestJson.put("badges", badgesJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Session.addSession(requestJson);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            }
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, requestJson, responseListener, null);
        elkActivity.addRequest(jsonObjectRequest);
    }

    public interface BadgesListener {
        void onLoaded(State state);
    }
}
