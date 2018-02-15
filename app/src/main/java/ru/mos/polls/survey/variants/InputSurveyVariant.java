package ru.mos.polls.survey.variants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.R;
import ru.mos.polls.helpers.TextHelper;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.variants.values.VariantValue;

/**
 * Ввод текста
 */
public class InputSurveyVariant extends SurveyVariant {

    private String text;
    private String hint;
    public VariantValue input;
    private TextView textTextView;
    private TextView inputTextView;
    private TextView stubTextView;

    public InputSurveyVariant(String backId, long innerId, @IntRange(from = PERCENT_NOT_SET, to = 100) int percent, int voters, VariantValue vs, String text, String hint) {
        super(backId, innerId, percent, voters);
        if (vs == null) {
            throw new NullPointerException("VariantValue");
        }
        this.text = text;
        this.input = vs;
        this.hint = hint;
    }

    public String getText() {
        return text;
    }

    @Override
    protected View onGetView(Context context, StatusProcessor statusProcessor) {
        View v = View.inflate(context, R.layout.survey_variant_input, null);
        textTextView = (TextView) v.findViewById(R.id.text);
        inputTextView = (TextView) v.findViewById(R.id.input);
        stubTextView = (TextView) v.findViewById(R.id.stub);
        if (!TextUtils.isEmpty(hint)) {
            stubTextView.setText(hint);
        }
        if (input.isEmpty()) {
            inputTextView.setVisibility(View.GONE);
            /**
             * Если поле ввода пустое, то не отображаем поле ввода
             * Скрыть пустые поля ввода в ответах с комментарием</a>
             * Пока "глобально" не правим, возможно придется все венуть назад
             */
        } else {
            inputTextView.setVisibility(View.VISIBLE);
            stubTextView.setVisibility(View.GONE);
            String text = input.asString();
            text = TextHelper.capitalizeFirstLatter(text);
            inputTextView.setText(text);
        }
        textTextView.setText(text);
        statusProcessor.process(textTextView);
        statusProcessor.process(inputTextView);
        statusProcessor.process(stubTextView);
        statusProcessor.processChecked(inputTextView, this);
        return v;
    }

    @Override
    protected void processAnswerJson(JSONObject jsonObject) throws JSONException {
        input.putValueInJson("value", jsonObject);
    }

    @Override
    public void loadAnswerJson(JSONObject answerJsonObject) throws JSONException {
        input.getValueFromJson("value", answerJsonObject);
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
    public void onClick(Activity context, Fragment fragment, boolean checked) {
        if (checked) {
            input.showEditor(context, new VariantValue.Listener() {
                @Override
                public void onEdited() {
                    inputTextView.setText(input.asString());
                    statusProcessor.processChecked(inputTextView, InputSurveyVariant.this);
                    getListener().onCommit();
                }

                @Override
                public void onCancel() {
                    statusProcessor.processChecked(inputTextView, InputSurveyVariant.this);
                    getListener().onCancel();
                }
            });
        } else {
            getListener().onClicked();
        }
    }

    @Override
    public void verify() throws VerificationException {
        if (input.isEmpty()) {
            throw new VerificationException("Не заполнено поле \"" + text + "\"");
        }
    }

    @Override
    public String toString() {
        return "input " + text + " " + input + " " + isChecked();
    }

    public static class Factory extends InputTypeFactory {

        @Override
        protected SurveyVariant onCreate(JSONObject jsonObject, String variantId, long innerVariantId, int percent, int voters, String inputType) {
            final String text = jsonObject.optString("text");
            final String hint = jsonObject.optString("hint");
            final VariantValue variantValue = getVariantValue(jsonObject, inputType, text, hint);
            final SurveyVariant result = new InputSurveyVariant(variantId, innerVariantId, percent, voters, variantValue, text, hint);
            return result;
        }
    }

}
