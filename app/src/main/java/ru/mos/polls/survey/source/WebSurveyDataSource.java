package ru.mos.polls.survey.source;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import com.android.volley2.RequestQueue;
import com.android.volley2.Response;
import com.android.volley2.VolleyError;
import com.android.volley2.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.mos.elk.Statistics;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.OkHttpStack;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.api.API;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.electronichouse.vm.HousePollFragmentVM;
import ru.mos.polls.poll.vm.PollActiveFragmentVM;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.hearing.controller.HearingApiControllerRX;
import ru.mos.polls.survey.parsers.SurveyFactory;
@Deprecated
public class WebSurveyDataSource implements SurveyDataSource {

    private final BaseActivity actionBarActivity;

    public WebSurveyDataSource(BaseActivity activity) {
        if (activity == null) {
            throw new NullPointerException("activity");
        }
        actionBarActivity = activity;
    }

    @Override
    public void load(final long surveyId, final boolean isHearing, final LoadListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.GET));
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put(isHearing ? "hearing_id" : "poll_id", surveyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Session.addSession(requestJsonObject);
        Response.ErrorListener errorListener = new StandartErrorListener(actionBarActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (volleyError != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(actionBarActivity);
                    builder.setMessage(volleyError.getMessage());
                    builder.setPositiveButton(R.string.ag_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            actionBarActivity.finish();
                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            actionBarActivity.finish();
                        }
                    });
                    builder.show();
                }
                listener.onError(volleyError != null ?
                        volleyError.getMessage() : actionBarActivity.getString(R.string.unknown_error));
            }
        };
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Survey survey = SurveyFactory.fromJson(jsonObject);
                    listener.onLoaded(survey);
                } catch (Exception e) {
                    listener.onError("Ошибка при загрузке опроса: " + e.getMessage());
                }
            }
        };
        final JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(url, requestJsonObject, responseListener, errorListener);
        actionBarActivity.addRequest(jsonArrayRequest);

    }

    @Override
    public void save(final Survey survey, final SaveListener listener, final boolean isInterrupted) {
        if (survey == null) {
            throw new NullPointerException("survey");
        }
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.FILL));
        JSONObject requestJsonObject = new JSONObject();
        Session.addSession(requestJsonObject);
        boolean noAnswers = true;
        try {
            requestJsonObject.put(survey.getKind().isHearing() ? "hearing_id" : "poll_id", survey.getId());
            if (isInterrupted) {
                requestJsonObject.put("poll_status", "interrupted");
            } else {
                requestJsonObject.put("poll_status", "passed");
            }
            JSONArray valuesJsonArray = survey.getPrepareAnswersJson();
            noAnswers = valuesJsonArray.length() == 0;
            requestJsonObject.put("values", valuesJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                Map<String, String> stParams = new HashMap<String, String>();
                stParams.put("id", String.valueOf(survey.getId()));
                stParams.put("success", isInterrupted ? "Cancel" : "Final");
                Statistics.customEvent("poll passaging", stParams);
                if (isInterrupted) {
                    AGApplication.bus().send(new Events.PollEvents(Events.PollEvents.INTERRUPTED_POLL, survey.getId()));
                } else {
                    Intent intent = new Intent(PollActiveFragmentVM.ACTION_POLL_IS_PASSED);
                    intent.putExtra(PollActiveFragmentVM.ARG_POLL_ID, survey.getId());
                    LocalBroadcastManager.getInstance(actionBarActivity).sendBroadcast(intent);
                    intent = new Intent(HousePollFragmentVM.ACTION_POLL_CHANGED);
                    intent.putExtra(HousePollFragmentVM.ARG_POLL, survey.getId());
                    LocalBroadcastManager.getInstance(actionBarActivity).sendBroadcast(intent);
                }
//                AGApplication.bus().send(new Events.PollEvents(isInterrupted ? Events.PollEvents.INTERRUPTED_POLL : Events.PollEvents.FINISHED_POLL, survey.getId()));
                JSONObject statusJsonObject = json.optJSONObject("status");
                final int currentPoints = statusJsonObject.optInt("current_points");
                final int price = json.optInt("added_points");
                JSONObject socialJson = json.optJSONObject("social");
                final AppPostValue appPostValue = new AppPostValue(socialJson, AppPostValue.Type.POLL);
                appPostValue
                        .setEnable(true)
                        .setNotify(false);
                listener.onSaved(price, currentPoints, appPostValue);
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(actionBarActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                switch (volleyError.getErrorCode()) {
                    case HearingApiControllerRX.ERROR_CODE_NO_MASTER_SSO_ID:
                        listener.onPguAuthError(volleyError.getMessage());
                        break;
                    default:
                        listener.onError(volleyError.getErrorCode(), volleyError.getMessage());
                        break;
                }
            }
        };
        if (noAnswers) {
            listener.onNoDataToSave();
        } else {
            JsonObjectRequest jsonRequest = new JsonObjectRequest(url, requestJsonObject, responseListener, errorListener);
//            actionBarActivity.addRequest(jsonRequest);
            /**
             * т.к. запрос не успевал срабатывать, а умерал раньше, сделано вот так ->
             */
            //TODO разобраться в каком моменте убивается активити. по логам запрос отменятся при приёме "volley canceled-at-delivery" где то раньше срабатывает finish


            RequestQueue requestQueue = Volley.newRequestQueue(actionBarActivity, new OkHttpStack());
            requestQueue.add(jsonRequest);
        }
    }
}
