package ru.mos.polls.quests.controller;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.UrlManager;
import ru.mos.polls.quests.quest.AchievementQuest;
import ru.mos.polls.quests.quest.AdvertisementQuest;
import ru.mos.polls.quests.quest.BackQuest;
import ru.mos.polls.quests.quest.NewsQuest;
import ru.mos.polls.quests.quest.SocialQuest;


public abstract class QuestsApiController {
    public static void hideRateThisApp(BaseActivity elkActivity, HideListener hideListener) {
        setVisibilityQuest(elkActivity, SocialQuest.TYPE_SOCIAL, SocialQuest.ID_RATE_THIS_APP, true, hideListener);
    }

    public static void hideNews(BaseActivity elkActivity, long id, HideListener hideListener) {
        setVisibilityQuest(elkActivity, NewsQuest.TYPE, String.valueOf(id), true, hideListener);
    }

    public static void hideAchievement(BaseActivity elkActivity, String id, HideListener hideListener) {
        setVisibilityQuest(elkActivity, AchievementQuest.TYPE, id, true, hideListener);
    }

    public static void hideAdvertisement(BaseActivity elkActivity, long id, HideListener hideListener) {
        setVisibilityQuest(elkActivity, AdvertisementQuest.TYPE, String.valueOf(id), true, hideListener);
    }

    public static void setVisibilityQuest(BaseActivity elkActivity, String type, String id, boolean willBeHide, HideListener listener) {
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("id", id);
            requestJson.put("type", type);
            requestJson.put("hide", willBeHide);
            JSONObject authJson = new JSONObject();
            authJson.put(Session.SESSION_ID, Session.getSession(elkActivity));
            requestJson.put(Session.AUTH, authJson);
        } catch (JSONException ignored) {
        }
        setVisibilityQuest(elkActivity, requestJson, listener);
    }

    public static void hide(BaseActivity elkActivity, BackQuest quest, HideListener listener) {
        setVisibilityQuest(elkActivity, quest, true, listener);
    }

    public static void show(BaseActivity elkActivity, BackQuest quest, HideListener listener) {
        setVisibilityQuest(elkActivity, quest, false, listener);
    }

    public static void setVisibilityQuest(BaseActivity elkActivity, BackQuest quest, boolean willBeHide, HideListener listener) {
        JSONObject requestJson = new JSONObject();
        try {
            quest.addJsonForHide(requestJson, willBeHide);
            JSONObject authJson = new JSONObject();
            authJson.put(Session.SESSION_ID, Session.getSession(elkActivity));
            requestJson.put(Session.AUTH, authJson);
        } catch (JSONException ignored) {
        }
        setVisibilityQuest(elkActivity, requestJson, listener);
    }

    private static void setVisibilityQuest(BaseActivity elkActivity, JSONObject requestJson, final HideListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.HIDE));
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    if (listener != null) {
                        listener.onHide(true);
                    }
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (listener != null) {
                    listener.onHide(false);
                }
            }
        };
        elkActivity.addRequest(new JsonObjectRequest(url, requestJson, responseListener, errorListener));
    }

    public interface HideListener {
        void onHide(boolean isHide);
    }
}
