package ru.mos.polls.survey.experts;

import android.app.ProgressDialog;
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
import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.questions.SurveyQuestion;

/**
 * Инкапсулирует  работу с сервисом "мнение экспертов"
 * Для запроса мнений эксперта по голосованию передавать pollId {@link Survey#getId()},
 * по вопросу - questionId {@link SurveyQuestion#getId()}<br/>
 * Для запроса мненией экспертов для публичных слушаний необходимо передать
 * признак публичного слушания{@link Kind#isHearing()}
 *
 * @since 1.8
 */
public abstract class ExpertsApiController {
    public static void loadDetailExperts(final BaseActivity activity, long pollId, long questionId, boolean isHearing, final DetailsExpertListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.GET_EXPERTS_LIST));
        final ProgressDialog pd = new ProgressDialog(activity, R.style.ProgressBar);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.show();
        JSONObject requestJson = new JSONObject();
        try {
            if (pollId != 0) {
                requestJson.put(isHearing ? "hearing_id" : "poll_id", pollId);
            }
            if (questionId != 0) {
                requestJson.put("question_id", questionId);
            }
            JSONObject authJson = new JSONObject();
            authJson.put(Session.SESSION_ID, Session.getSession(activity));
            requestJson.put(Session.AUTH, authJson);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                List<DetailsExpert> detailsExperts = new ArrayList<DetailsExpert>();
                if (pd != null) {
                    pd.dismiss();
                }
                if (jsonObject != null) {
                    JSONArray jsonArray = jsonObject.optJSONArray("experts");
                    if (jsonArray != null) {
                        try {
                            for (int i = 0; i < jsonArray.length(); ++i) {
                                DetailsExpert detailsExpert = new DetailsExpert(jsonArray.getJSONObject(i));
                                if (!detailsExpert.isEmpty()) {
                                    detailsExperts.add(detailsExpert);
                                }
                            }
                        } catch (JSONException ignored) {
                        }
                    }
                }
                if (listener != null) {
                    listener.onLoaded(detailsExperts);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (pd != null) {
                    pd.dismiss();
                }
                Toast.makeText(activity, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onError();
                }
            }
        };
        activity.addRequest(new ru.mos.elk.netframework.request.JsonObjectRequest(url, requestJson, responseListener, errorListener));
    }

    public interface DetailsExpertListener {
        void onLoaded(List<DetailsExpert> detailsExperts);

        void onError();
    }
}


