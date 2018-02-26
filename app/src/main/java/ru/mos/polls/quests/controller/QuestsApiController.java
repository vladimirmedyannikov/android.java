package ru.mos.polls.quests.controller;


import android.app.ProgressDialog;
import android.util.Log;

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
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.api.API;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.quests.model.quest.AchievementQuest;
import ru.mos.polls.quests.model.quest.AdvertisementQuest;
import ru.mos.polls.quests.model.quest.BackQuest;
import ru.mos.polls.quests.model.quest.NewsQuest;
import ru.mos.polls.quests.model.quest.Quest;
import ru.mos.polls.quests.model.quest.SocialQuest;

@Deprecated
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

    public static void hideAllNews(BaseActivity elkActivity, List<Quest> quests, final HideQuestListner listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.HIDE_GROUP));
        JSONObject requestJson = new JSONObject();
        final ProgressDialog pd = new ProgressDialog(elkActivity, R.style.ProgressBar);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.show();
        ArrayList<String> idsList = null;
        try {
            requestJson.put("type", "allNews");
            requestJson.put("hide", "true");
            JSONObject authJson = new JSONObject();
            authJson.put(Session.SESSION_ID, Session.getSession(elkActivity));
            requestJson.put(Session.AUTH, authJson);
            idsList = new ArrayList<>();
            for (Quest q : quests) {
                String type = ((BackQuest) q).getType();
                if (type.equals("news") || type.equals("results")) {
                    idsList.add(((BackQuest) q).getId());
                }
            }
            JSONArray ids = new JSONArray(idsList);
            requestJson.put("ids", ids);
        } catch (JSONException ignored) {
        }
        final ArrayList<String> finalIdsList = idsList;
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (pd != null) {
                    pd.dismiss();
                }
                listener.hideQuests(finalIdsList);
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (pd != null) {
                    pd.dismiss();
                }
                Log.d("volleyError", volleyError.getMessage());
            }
        };
        elkActivity.addRequest(new JsonObjectRequest(url, requestJson, responseListener, errorListener));
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
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
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

    public interface HideQuestListner {
        void hideQuests(ArrayList<String> idsList);
    }
}
