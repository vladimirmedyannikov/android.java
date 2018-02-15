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

import java.util.HashMap;
import java.util.Map;

import ru.mos.polls.R;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.variants.select.EgipSelectObject;
import ru.mos.polls.survey.variants.select.GorodSelectObject;
import ru.mos.polls.survey.variants.select.SelectObject;
import ru.mos.polls.survey.variants.select.ServiceSelectObject;

/**
 * Реализует выбор одного из нескольких вариантов.
 * Список вариантов - с бека.
 */
public class SelectSurveyVariant extends SurveyVariant {

    private final String text;
    private final SelectObject selectObject;
    private TextView tv;
    private String objectType;


    public SelectSurveyVariant(String backId, long innerId, @IntRange(from = PERCENT_NOT_SET, to = 100) int percent, int voters, String text, SelectObject selectObject, String objectType) {
        super(backId, innerId, percent, voters);
        this.text = text;
        this.selectObject = selectObject;
        this.objectType = objectType;
    }

    @Override
    protected View onGetView(Context context, StatusProcessor statusProcessor) {
        View v = View.inflate(context, R.layout.survey_variant_select, null);
        statusProcessor.process(v);
        tv = (TextView) v.findViewById(R.id.text);
        TextView stubTextView = (TextView) v.findViewById(R.id.stub);
        statusProcessor.process(tv);
        if (selectObject.isSelected()) {
            stubTextView.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            tv.setText(selectObject.asString());
        } else {
            stubTextView.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
            stubTextView.setText(text);
        }
        statusProcessor.processChecked(tv, this);
        return v;
    }

    @Override
    public void onClick(Activity context, Fragment fragment, boolean checked) {
        if (checked) {
            statusProcessor.processChecked(tv, this);
            selectObject.onClick(context, fragment);
        } else {
            getListener().onClicked();
        }
    }

    @Override
    public void verify() throws VerificationException {
        if (!selectObject.isSelected()) {
            throw new VerificationException("Не заполнено поле " + text);
        }
    }

    @Override
    protected void processAnswerJson(JSONObject answerJsonObject) {
        try {
            JSONObject valueJsonObject = new JSONObject();
            selectObject.processAnswerJson(valueJsonObject);
            answerJsonObject.put("value", valueJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAnswerJson(JSONObject answerJsonObject) {
        JSONObject valueJsonObject = answerJsonObject.optJSONObject("value");
        selectObject.loadAnswerJson(valueJsonObject);
    }

    @Override
    public boolean onActivityResultOk(Intent data) {
        selectObject.loadFromIntent(data);
        getListener().onCommit();
        return true;
    }

    @Override
    public boolean onActivityResultCancel(Intent data) {
        getListener().onCancel();
        return true;
    }

    public String getObjectType() {
        return objectType;
    }

    public SelectObject getSelectObject() {
        return selectObject;
    }

    public static class Factory extends SurveyVariant.Factory {

        private static final Map<String, SelectObject.Factory> factories = new HashMap<String, SelectObject.Factory>();

        static {
            factories.put("gorod", new GorodSelectObject.Factory());
            factories.put("service", new ServiceSelectObject.Factory());
            factories.put("egip", new EgipSelectObject.Factory());
        }

        @Override
        protected SurveyVariant onCreate(JSONObject jsonObject, long surveyId, long questionId, String variantId, long innerVariantId, int percent, int voters) {

            final String text = jsonObject.optString("text");

            JSONObject objectJsonObject = jsonObject.optJSONObject("object");
            String objectType = objectJsonObject.optString("type");
            if (!factories.containsKey(objectType)) {
                throw new RuntimeException("unknown object type " + objectType);
            }
            SelectObject.Factory factory = factories.get(objectType);
            SelectObject selectObject = factory.create(surveyId, questionId, variantId, objectJsonObject, text, text);

            //TODO реализовать констрейнты
            /*JSONObject constraintsJsonObject = jsonObject.optJSONObject("constraints");
            boolean noCoordinated = constraintsJsonObject.optBoolean("no_coordinates");
            boolean noAddress = constraintsJsonObject.optBoolean("no_address");*/
            /*SelectObject selectObject = new GorodSelectObject();*/

            return new SelectSurveyVariant(variantId, innerVariantId, percent, voters, text, selectObject, objectType);
        }
    }

}
