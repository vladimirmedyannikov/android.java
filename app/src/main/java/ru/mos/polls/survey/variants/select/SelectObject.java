package ru.mos.polls.survey.variants.select;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class SelectObject {

    private final long surveyId;
    private final long questionId;
    private final String variantId;
    private boolean selected = false;
    private final String activityTitle;
    private final String emptyText;

    protected SelectObject(long surveyId, long questionId, String variantId, String activityTitle, String emptyText) {
        this.surveyId = surveyId;
        this.questionId = questionId;
        this.variantId = variantId;
        this.activityTitle = activityTitle;
        this.emptyText = emptyText;
    }

    protected long getSurveyId() {
        return surveyId;
    }

    protected long getQuestionId() {
        return questionId;
    }

    protected String getVariantId() {
        return variantId;
    }

    /**
     * Заполняем джисон
     *
     * @param answerJsonObject
     * @throws JSONException
     */
    public abstract void processAnswerJson(JSONObject answerJsonObject) throws JSONException;

    /**
     * Получаем данные иж джисона
     *
     * @param answerJsonObject
     */
    public abstract void loadAnswerJson(JSONObject answerJsonObject);

    public abstract void onClick(Activity context, Fragment fragment);

    public abstract void loadFromIntent(Intent data);

    protected void setSelected(boolean b) {
        selected = b;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public String getEmptyText() {
        return emptyText;
    }

    /**
     * Для отображения в TextView или чего-то подобного.
     *
     * @return Текст, который увидит пользователь.
     */
    public abstract String asString();

    public static abstract class Factory {

        public abstract SelectObject create(long surveyId, long questionId, String variantId, JSONObject jsonObject, String title, String emptyText);

    }

}
