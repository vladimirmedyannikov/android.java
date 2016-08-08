package ru.mos.polls.survey.variants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.R;
import ru.mos.polls.quests.ProfileQuestActivity;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.VerificationException;

public class ActionSurveyVariant extends SurveyVariant {
    public static final int REQUEST_FILLING_LOCATION_DATA = 101;

    private final String text;
    private final String actionName;
    private TextView textTextView;

    public ActionSurveyVariant(String backId, long innerId, @IntRange(from = PERCENT_NOT_SET, to = 100) int percent, int voters, String text, String actionName) {
        super(backId, innerId, percent, voters);
        this.text = text;
        this.actionName = actionName;
    }

    @Override
    protected View onGetView(Context context, StatusProcessor statusProcessor) {
        View result = View.inflate(context, R.layout.survey_varinat_action, null);
        textTextView = (TextView) result.findViewById(R.id.text);
        textTextView.setText(text);
        boolean isOld = !statusProcessor.isEnabled();
        result.setEnabled(!isOld);
        textTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, !isOld ? R.drawable.action_add_any_content : 0, 0);
        statusProcessor.processChecked(textTextView, this);
        return result;
    }

    @Override
    public void onClick(Activity context, Fragment fragment, boolean checked) {
        if (actionName.equals("fillFlats")) {
            ProfileQuestActivity.startActivityAddFlat(fragment);
        }
    }

    @Override
    public void verify() throws VerificationException {
        throw new VerificationException(text);
    }

    // не нужно сохранять это нажатие. Иначе оно передается на сервак в качестве варианта ответа
    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    protected void processAnswerJson(JSONObject jsonObject) throws JSONException {
        //вероятно не нужно
    }

    @Override
    public void loadAnswerJson(JSONObject answerJsonObject) throws JSONException {
        //вероятно не нужно
    }

    @Override
    public boolean onActivityResultOk(Intent data) {
        getListener().refreshSurvey();
        return true;
    }

    @Override
    public boolean onActivityResultCancel(Intent data) {
        return false;
    }

    public static class Factory extends SurveyVariant.Factory {

        @Override
        protected SurveyVariant onCreate(JSONObject jsonObject, long surveyId, long questionId, String variantId, long innerVariantId, int percent, int voters) {
            String text = jsonObject.optString("text");
            String actionName = jsonObject.optString("actionName");
            ActionSurveyVariant result = new ActionSurveyVariant(variantId, innerVariantId, percent, voters, text, actionName);
            return result;
        }
    }
}
