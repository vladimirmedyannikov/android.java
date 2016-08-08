package ru.mos.polls.survey.variants.select;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Здесь будет отображение карты
 * TODO реализовать
 */
public class EgipSelectObject extends SelectObject {

    public EgipSelectObject(long surveyId, long questionId, String variantId, String activityTitle, String emptyText) {
        super(surveyId, questionId, variantId, activityTitle, emptyText);
    }

    @Override
    public void processAnswerJson(JSONObject answerJsonObject) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadAnswerJson(JSONObject answerJsonObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onClick(Activity context, Fragment fragment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadFromIntent(Intent data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException();
    }

    public static class Factory extends SelectObject.Factory {

        @Override
        public SelectObject create(long surveyId, long questionId, String variantId, JSONObject jsonObject, String title, String emptyText) {
            return new EgipSelectObject(surveyId, questionId, variantId, title, emptyText);
        }
    }
}
