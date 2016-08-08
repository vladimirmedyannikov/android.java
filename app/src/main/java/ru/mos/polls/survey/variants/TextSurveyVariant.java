package ru.mos.polls.survey.variants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import ru.mos.polls.R;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.VerificationException;

/**
 * Просто отображает текст
 */
public class TextSurveyVariant extends SurveyVariant {

    private String text;
    private TextView tv;

    public TextSurveyVariant(String backId, long innerId, @IntRange(from = PERCENT_NOT_SET, to = 100) int percent, int voters, String text) {
        super(backId, innerId, percent, voters);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    protected View onGetView(Context context, StatusProcessor statusProcessor) {
        View v = View.inflate(context, R.layout.survey_variant_text, null);
        statusProcessor.process(v);
        tv = (TextView) v.findViewById(R.id.text);
        statusProcessor.process(tv);
        tv.setText(text);
        statusProcessor.processChecked(tv, this);
        return v;
    }

    @Override
    public void onClick(Activity context, Fragment fragment, boolean checked) {
        statusProcessor.processChecked(tv, this);
        getListener().onClicked();
    }

    @Override
    public void verify() throws VerificationException {
        //Ничего не делает, так как это вариант опроса всегда корректен
    }

    @Override
    protected void processAnswerJson(JSONObject jsonObject) {
        //ничего не делает
    }

    @Override
    public void loadAnswerJson(JSONObject answerJsonObject) {
        //ничего не делает
    }

    @Override
    public boolean onActivityResultOk(Intent data) {
        //не предусмотрено для данного класса
        return false;
    }

    @Override
    public boolean onActivityResultCancel(Intent data) {
        //не предусмотрено для данного класса
        return false;
    }

    @Override
    public String toString() {
        return "text " + text + " " + isChecked();
    }

    public static class Factory extends SurveyVariant.Factory {

        @Override
        protected SurveyVariant onCreate(JSONObject jsonObject, long surveyId, long questionId, String variantId, long innerVariantId, int percent, int voters) {
            final String text = jsonObject.optString("text");
            final SurveyVariant result = new TextSurveyVariant(variantId, innerVariantId, percent, voters, text);
            return result;
        }
    }

}
