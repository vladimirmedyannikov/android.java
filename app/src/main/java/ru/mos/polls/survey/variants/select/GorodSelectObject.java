package ru.mos.polls.survey.variants.select;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class GorodSelectObject extends SelectObject {

    private static final int REQUEST_CODE = 355;

    private String address;
    private String objectId;
    private final String category;
    private String parentId = null;
    private String parentIdAnswer;

    public String getObjectId() {
        return objectId;
    }

    public String getParentIdAnswer() {
        return parentIdAnswer;
    }

    public void setParentIdAnswer(String parentIdAnswer) {
        this.parentIdAnswer = parentIdAnswer;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public GorodSelectObject(long surveyId, long questionId, String variantId, String category, String activityTitle, String emptyText, String parentId) {
        super(surveyId, questionId, variantId, activityTitle, emptyText);
        this.category = category;
        this.parentId = parentId;
    }

    @Override
    public void processAnswerJson(JSONObject answerJsonObject) throws JSONException {
        answerJsonObject.put("address", address);
        answerJsonObject.put("object_id", objectId);
    }

    @Override
    public void loadAnswerJson(JSONObject answerJsonObject) {
        address = answerJsonObject.optString("address", "");
        objectId = answerJsonObject.optString("object_id");
        processSelected();
    }

    private void processSelected() {
        setSelected(!TextUtils.isEmpty(address) && !TextUtils.isEmpty(objectId));
    }

    @Override
    public void onClick(Activity context, Fragment fragment) {
        IntentExtraProcessor intentExtraProcessor = new IntentExtraProcessor();
        Intent i = new Intent(context, GorodSelectActivity.class);
        i = intentExtraProcessor.generateStartActivityIntent(i, getActivityTitle(), getEmptyText(), getSurveyId(), getQuestionId(), getVariantId());
        i.putExtra(GorodSelectActivity.EXTRA_CATEGORORY, category);
        i.putExtra("parent", parentIdAnswer);
        fragment.startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    public void loadFromIntent(Intent data) {
        address = data.getStringExtra(GorodSelectActivity.EXTRA_ADDRESS);
        objectId = data.getStringExtra(GorodSelectActivity.EXTRA_OBJECT_ID);
        processSelected();
    }

    @Override
    public String asString() {
        return address;
    }

    public static class Factory extends SelectObject.Factory {

        @Override
        public SelectObject create(long surveyId, long questionId, String variantId, JSONObject jsonObject, String title, String emptyText) {
            String category = jsonObject.optString("category");
            String parentId = jsonObject.optString("parent_id");
            return new GorodSelectObject(surveyId, questionId, variantId, category, title, emptyText, parentId);
        }

    }

}
