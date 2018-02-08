package ru.mos.polls.profile.controller;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.api.API;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.profile.model.Achievement;

/**
 * Инкапсулирует работу с сервисом по профилю
 * Здесь собраны далеко не все методы сервиса
 * <p/>
 */
public abstract class ProfileApiController {
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
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (achievementListener != null) {
                    achievementListener.onError(volleyError);
                }
            }

        };
        JsonObjectRequest request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
        elkActivity.addRequest(request);
    }

    public interface AchievementListener {
        void onLoaded(Achievement achievement);

        void onError(VolleyError volleyError);
    }
}
