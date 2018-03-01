package ru.mos.polls.survey.source;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.electronichouse.vm.HousePollFragmentVM;
import ru.mos.polls.poll.vm.PollActiveFragmentVM;
import ru.mos.polls.rxhttp.rxapi.handle.error.DefaultResponseErrorHandler;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.hearing.controller.HearingApiControllerRX;
import ru.mos.polls.survey.parsers.SurveyFactory;
import ru.mos.polls.survey.service.FillPoll;
import ru.mos.polls.survey.service.GetPoll;
import ru.mos.polls.util.GuiUtils;

public class WebSurveyDataSourceRX implements SurveyDataSource {

    final BaseActivity actionBarActivity;

    public WebSurveyDataSourceRX(BaseActivity actionBarActivity) {
        this.actionBarActivity = actionBarActivity;
    }

    @Override
    public void load(long surveyId, boolean isHearing, LoadListener listener) {
        DefaultResponseErrorHandler errorHandler = new DefaultResponseErrorHandler(actionBarActivity) {
            @Override
            public void onServerError(int code, String message) {
                super.onServerError(code, message);
                if (message != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(actionBarActivity);
                    builder.setMessage(message);
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
                listener.onError(message);
            }

            @Override
            public void onSystemError(Throwable throwable) {
                GuiUtils.displayOkMessage(actionBarActivity, "Произошла непредвиденная ошибка. Попробуйте повторить операцию позже.", (dialogInterface, i) -> {
                    actionBarActivity.finish();
                });
            }
        };
        HandlerApiResponseSubscriber<JsonObject> handler = new HandlerApiResponseSubscriber<JsonObject>(errorHandler) {
            @Override
            protected void onResult(JsonObject result) {
                try {
                    if (result != null) {
                        JSONObject object = new JSONObject(result.toString());
                        Survey survey = SurveyFactory.fromJson(object);
                        listener.onLoaded(survey);
                    }
                } catch (Exception e) {
                    listener.onError("Ошибка при загрузке опроса: " + e.getMessage());
                }
            }
        };
        GetPoll.Request request = new GetPoll.Request();
        if (isHearing) {
            request.setHearingId(surveyId);
        } else {
            request.setPollId(surveyId);
        }
        AGApplication
                .api
                .getPoll(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    @Override
    public void save(Survey survey, SaveListener listener, boolean isInterrupted) {
        JSONArray valuesJsonArray = survey.getPrepareAnswersJson();
        boolean noAnswers = valuesJsonArray.length() == 0;
        if (noAnswers) {
            listener.onNoDataToSave();
        } else {
            FillPoll.Request request = new FillPoll.Request();
            if (survey.getKind().isHearing()) {
                request.setHearingId(survey.getId());
            } else {
                request.setPollId(survey.getId());
            }
            request.setPollStatus(isInterrupted ? "interrupted" : "passed");
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = (JsonArray) jsonParser.parse(valuesJsonArray.toString());
            request.setValues(jsonArray);
            DefaultResponseErrorHandler errorHandler = new DefaultResponseErrorHandler(actionBarActivity) {
                @Override
                public void onServerError(int code, String message) {
                    super.onServerError(code, message);
                    switch (code) {
                        case HearingApiControllerRX.ERROR_CODE_NO_MASTER_SSO_ID:
                            listener.onPguAuthError(message);
                            break;
                        default:
                            listener.onError(code, message);
                            break;
                    }
                }
            };
            HandlerApiResponseSubscriber<FillPoll.Response.Result> handler = new HandlerApiResponseSubscriber<FillPoll.Response.Result>(errorHandler) {
                @Override
                protected void onResult(FillPoll.Response.Result result) {
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
                    final AppPostValue appPostValue = new AppPostValue(null, AppPostValue.Type.POLL);
                    appPostValue
                            .setEnable(true)
                            .setNotify(false);
                    listener.onSaved(result.getAddedPoints(), result.getStatus().getCurrentPoints(), appPostValue);
                }
            };
            AGApplication
                    .api
                    .fillPoll(request)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(handler);
        }
    }
}