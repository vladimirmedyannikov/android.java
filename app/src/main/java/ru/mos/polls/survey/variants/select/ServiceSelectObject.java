package ru.mos.polls.survey.variants.select;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceSelectObject extends SelectObject {

    public static final int REQUEST_CODE = 355;

    private final String category;
    private long id = -1;
    private String title = null;
    private String address = null;
    private String parentId = null;
    private String parentIdAnswer;

    public long getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentIdAnswer() {
        return parentIdAnswer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setParentIdAnswer(String parentIdAnswer) {
        this.parentIdAnswer = parentIdAnswer;
    }

    public ServiceSelectObject(long surveyId, long questionId, String variantId, String category, String activityTitle, String emptyText, String parentId) {
        super(surveyId, questionId, variantId, activityTitle, emptyText);
        this.category = category;
        this.parentId = parentId;
    }

    @Override
    public void processAnswerJson(JSONObject answerJsonObject) throws JSONException {
        answerJsonObject.put("id", id);
        answerJsonObject.put("title", title);
        answerJsonObject.put("description", address);
    }

    @Override
    public void loadAnswerJson(JSONObject answerJsonObject) {
        id = answerJsonObject.optLong("id");
        title = answerJsonObject.optString("title");
        address = answerJsonObject.optString("description");
        if (id > 0)
            setSelected(true);
    }

    @Override
    public void onClick(Activity context, Fragment fragment) {
        IntentExtraProcessor intentExtraProcessor = new IntentExtraProcessor();
        Intent i = new Intent(context, ServiceSelectActivity.class);
        i = intentExtraProcessor.generateStartActivityIntent(i, getActivityTitle(), getEmptyText(), getSurveyId(), getQuestionId(), getVariantId());
        i.putExtra(ServiceSelectActivity.EXTRA_TITLE, title);
        i.putExtra(ServiceSelectActivity.EXTRA_CATEGORORY, category);
        i.putExtra("parent", parentIdAnswer);
        fragment.startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    public void loadFromIntent(Intent data) {
        id = data.getLongExtra(ServiceSelectActivity.EXTRA_ID, -1);
        title = data.getStringExtra(ServiceSelectActivity.EXTRA_TITLE);
        address = data.getStringExtra(ServiceSelectActivity.EXTRA_DESCRIPTION);
        processSelected();
    }

    @Override
    public String asString() {
        return title;
    }

    private void processSelected() {
        setSelected(!TextUtils.isEmpty(title) && id != -1);
    }

    public static class Factory extends SelectObject.Factory {

        @Override
        public SelectObject create(long surveyId, long questionId, String variantId, JSONObject jsonObject, String title, String emptyText) {
            String category = jsonObject.optString("category");
            String parentId = jsonObject.optString("parent_id");
            ServiceSelectObject result = new ServiceSelectObject(surveyId, questionId, variantId, category, title, emptyText, parentId);
            return result;
        }

    }

}
