package ru.mos.polls.poll.controller;

import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.common.model.PageInfo;
import ru.mos.polls.common.model.UserStatus;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.poll.model.Poll;

/**
 * Икапсулирует работу с сервисом полученис списков опросов
 * <p/>
 */
public abstract class PollApiController {
    /**
     * Получение списка голосований в зависимости от значние фильтра
     *
     * @param elkActivity
     * @param pageInfo    информация о странице запроса
     * @param filter      тип фильтра, возможные значения AVAILABLE, PASSED, OLD
     * @param listener    callback возвращаюший список объектов Poll
     */
    public static void loadPolls(final BaseActivity elkActivity, final PageInfo pageInfo, Filter[] filter, List<Kind> kinds, final PollGroupListener listener) {
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseJson) {
                if (responseJson != null) {
                    List<Poll> polls = new ArrayList<Poll>();
                    JSONArray pollsJsonArray = responseJson.optJSONArray("polls");
                    polls = new ArrayList<Poll>(pollsJsonArray.length());
                    for (int i = 0; i < pollsJsonArray.length(); ++i) {
                        JSONObject pollJson = (JSONObject) pollsJsonArray.opt(i);
                        Poll poll = new Poll(pollJson);
                        polls.add(poll);
                    }
                    JSONObject userStatusJsonObject = responseJson.optJSONObject("status");
                    UserStatus userStatus = new UserStatus(userStatusJsonObject);
                    if (listener != null) {
                        listener.onLoad(polls, userStatus, pageInfo);
                    }

                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError != null) {
                    String errorMessage = volleyError.getMessage();
                    if (errorMessage == null
                            || TextUtils.isEmpty(errorMessage)
                            || "null".equalsIgnoreCase(errorMessage)) {
                        errorMessage = elkActivity.getString(R.string.rempte_service_not_work);
                    }
                    Toast.makeText(elkActivity, errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (listener != null) {
                    listener.onError(volleyError);
                }
            }
        };
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.SELECT));
        JSONObject jsonRequest = new JSONObject();
        try {
            if (pageInfo != null) {
                jsonRequest.put("count_per_page", pageInfo.getCountPerPage());
                jsonRequest.put("page_number", pageInfo.getPageNumber());
            }
            if (filter != null) {
                JSONArray jsonArray = new JSONArray();
                for (Filter item : filter) {
                    jsonArray.put(item.toString());
                }
                jsonRequest.put("filters", jsonArray);
            }
            if (kinds != null) {
                JSONArray kindJson = new JSONArray();
                for (Kind kind : kinds) {
                    kindJson.put(kind.getKind());
                }
                jsonRequest.put("kind_filters", kindJson);
            }
            /**
             * передаем сессию
             */
            JSONObject authJson = new JSONObject();
            authJson.put(Session.SESSION_ID, Session.getSession(elkActivity));
            jsonRequest.put(Session.AUTH, authJson);
        } catch (JSONException ignored) {
        }

        elkActivity.addRequest(new JsonObjectRequest(url, jsonRequest, responseListener, errorListener));
    }

    public enum Filter {
        AVAILABLE("available"),
        PASSED("passed"),
        OLD("old");

        private String filter;

        Filter(String filter) {
            this.filter = filter;
        }


        @Override
        public String toString() {
            return filter;
        }
    }

    public interface PollGroupListener {
        void onLoad(List<Poll> polls, UserStatus userStatus, PageInfo pageInfo);

        void onError(VolleyError volleyError);
    }

}
