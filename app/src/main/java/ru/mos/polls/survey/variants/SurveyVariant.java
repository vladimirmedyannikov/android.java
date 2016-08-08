package ru.mos.polls.survey.variants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.variants.values.CharVariantValue;
import ru.mos.polls.survey.variants.values.DateVariantValue;
import ru.mos.polls.survey.variants.values.FloatVariantValue;
import ru.mos.polls.survey.variants.values.IntVariantValue;
import ru.mos.polls.survey.variants.values.VariantValue;

/**
 * Вариант ответа на вопрос
 */
public abstract class SurveyVariant implements Serializable {

    protected static final int PERCENT_NOT_SET = -1;

    /**
     * Пришедший с бека айдишник.
     * Когда-то был long, теперь String.
     * Что будет с ним завтра?
     */
    private final String backId;
    /**
     * Внутренний айдишник.
     * Уникален в пределах одного вопроса.
     * Всегда будет long, вопрос только откуда брать.
     */
    private final long innerId;
    private final int percent;
    private final int voters;
    private Listener listener = Listener.STUB;
    protected StatusProcessor statusProcessor;

    /**
     * Вариант ответа
     *
     * @param backId  айдишник с бека
     * @param innerId внутренний айдишник
     * @param percent процент выбравших данный вариант ответа. Может принимать в т.ч. PERCENT_NOT_SET.
     * @param voters количество проголосовавших
     */
    public SurveyVariant(String backId, long innerId, @IntRange(from = PERCENT_NOT_SET, to = 100) int percent, int voters) {
        this.backId = backId;
        this.innerId = innerId;
        this.percent = percent;
        this.voters = voters;
    }

    /**
     * @return Пришедший с бека айдишник
     */
    public String getBackId() {
        return backId;
    }

    /**
     * @return Внутренний айдишник.
     */
    public long getInnerId() {
        return innerId;
    }

    /**
     * Процент выбравших этот вариант ответа.
     *
     * @return
     */
    public int getPercent() {
        return percent;
    }

    /**
     * @since 1.9.3
     * @return количество пользователей, выбравших данный вараинт ответа
     */
    public int getVoters() {
        return voters;
    }

    public View getView(Context c, StatusProcessor statusProcessor) {
        if (c == null) {
            throw new NullPointerException("context");
        }
        if (statusProcessor == null) {
            throw new NullPointerException("StatusProcessor");
        }
        this.statusProcessor = statusProcessor;
        return onGetView(c, statusProcessor);
    }

    public void setListener(Listener l) {
        if (l == null) {
            listener = Listener.STUB;
        } else {
            listener = l;
        }
    }

    protected Listener getListener() {
        return listener;
    }

    protected abstract View onGetView(Context context, StatusProcessor statusProcessor);

    public abstract void onClick(Activity context, Fragment fragment, boolean checked);

    public abstract void verify() throws VerificationException;

    private boolean checked = false;

    public void setChecked(boolean b) {
        checked = b;
    }

    public boolean isChecked() {
        return checked;
    }

    public JSONObject getAnswerJson() {
        JSONObject result = new JSONObject();
        try {
            result.put("variant_id", backId);
            processAnswerJson(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject getAnswerJson(boolean isChecked) {
        JSONObject result = new JSONObject();
        try {
            if (isChecked) {
                result.put("variant_id", backId);
            }
            processAnswerJson(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected abstract void processAnswerJson(JSONObject jsonObject) throws JSONException;

    public abstract void loadAnswerJson(JSONObject answerJsonObject) throws JSONException;

    public abstract boolean onActivityResultOk(Intent data);

    public abstract boolean onActivityResultCancel(Intent data);

    public interface Listener {

        public static final Listener STUB = new Listener() {

            @Override
            public void onClicked() {
            }

            @Override
            public void onCommit() {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void performParentClick() {
            }

            @Override
            public void refreshSurvey() {
            }
        };

        /**
         * Вызывать когда на варинт обрабатывает нажатие сразу, без показа диалога или другого активити и тп.
         */
        void onClicked();

        /**
         * Полсе показа диалога или активити значение варианта было изменено.
         */
        void onCommit();

        /**
         * После показа диалога или активити значение не было изменено \ в диалоге нажали отмену.
         */
        void onCancel();

        /**
         * Посылает нажатие на родительский эльемент. Елси там список, то нажатие на элемент списка, в котором находится вариант
         */
        void performParentClick();

        /**
         * Обновляет опрос.
         */
        void refreshSurvey();
    }


    public static abstract class Factory {

        public SurveyVariant create(JSONObject jsonObject, long surveyId, long questionId, long innerVariantId) {
            final String variantId = jsonObject.optString("id");
            final int percent = jsonObject.optInt("percent");
            final int voters = jsonObject.optInt("voters_count");
            return onCreate(jsonObject, surveyId, questionId, variantId, innerVariantId, percent, voters);
        }

        protected abstract SurveyVariant onCreate(JSONObject jsonObject, long surveyId, long questionId, String variantId, long innerVariantId, int percent, int voters);

    }

    public static abstract class InputTypeFactory extends SurveyVariant.Factory {

        private static final Map<String, VariantValue.Factory> factories = new HashMap<String, VariantValue.Factory>();

        static {
            factories.put("text", new CharVariantValue.Factory());
            factories.put("char", new CharVariantValue.Factory());
            factories.put("number", new FloatVariantValue.Factory());
            factories.put("float", new FloatVariantValue.Factory());
            factories.put("int", new IntVariantValue.Factory());
            factories.put("date", new DateVariantValue.Factory());
        }

        @Override
        @CallSuper
        protected SurveyVariant onCreate(JSONObject jsonObject, long surveyId, long questionId, String variantId, long innerVariantId, int percent, int voters) {
            final String inputType = jsonObject.optString("input_type");
            return onCreate(jsonObject, variantId, innerVariantId, percent,voters, inputType);
        }

        protected abstract SurveyVariant onCreate(JSONObject jsonObject, String variantId, long innerVariantId, int percent, int voters, String inputType);

        protected static VariantValue getVariantValue(JSONObject jsonObject, String inputType, String text, String hint) {
            final VariantValue value;
            if (!factories.containsKey(inputType)) {
                throw new RuntimeException("unknow input type " + inputType);
            }
            VariantValue.Factory factory = factories.get(inputType);
            String kind = jsonObject.optString("kind");
            value = factory.create(hint, kind, jsonObject);
            value.setTitle(text);
            return value;
        }

    }

}