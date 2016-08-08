package ru.mos.polls.survey.questions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import ru.mos.polls.model.Information;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.filter.EmptyFilter;
import ru.mos.polls.survey.filter.Filter;
import ru.mos.polls.survey.variants.SurveyVariant;
import ru.mos.polls.survey.variants.select.IntentExtraProcessor;

/**
 * Один вопрос из опросника.
 */
public abstract class SurveyQuestion implements Serializable {

    public static final long LONG_NOT_SET = -1;

    private final List<SurveyVariant> variantsList;
    private final long id;
    private final Text question;
    private final long filterId;
    private Information information;
    private final String hint;
    private boolean passed = false;
    private Listener listener = Listener.STUB;
    private List<DetailsExpert> detailsExperts;
    private final int votersCount;
    private final int votersVariantsCount;

    public SurveyQuestion(long id, Text question, String hint, List<SurveyVariant> variantsList, long filterId, int votersCount, int votersVariantsCount) {
        this(id, question, hint, variantsList, filterId, votersCount, votersVariantsCount, null);
    }

    /**
     * @param id
     * @param question
     * @param variantsList
     * @param filterId     Использовать LONG_NOT_SET если нет фильтра
     */
    public SurveyQuestion(long id, Text question, String hint, List<SurveyVariant> variantsList, long filterId, int votersCount, int votersVariantsCount, Information information) {
        this.id = id;
        this.question = question;
        this.hint = hint;
        this.variantsList = variantsList;
        this.filterId = filterId;
        this.information = information;
        this.votersCount = votersCount;
        this.votersVariantsCount = votersVariantsCount;
    }

    public void setListener(Listener listener) {
        if (listener == null) {
            this.listener = Listener.STUB;
        } else {
            this.listener = listener;
        }
    }

    protected Listener getListener() {
        return listener;
    }

    public int getVotersCount() {
        return votersCount;
    }

    public Information getInformation() {
        return information;
    }

    public List<DetailsExpert> getDetailsExperts() {
        return detailsExperts;
    }

    public void setDetailsExpert(List<DetailsExpert> detailsExpert) {
        this.detailsExperts = detailsExpert;
    }

    /**
     * Сбрасываем ответы на данный вопрос
     */
    public void reset() {
        for (SurveyVariant surveyVariant : getVariantsList()) {
            for (String backId : getCheckedBackIds()) {
                if (backId.equalsIgnoreCase(surveyVariant.getBackId())) {
                    surveyVariant.setChecked(false);
                }
            }
        }
    }

    public void setInformation(Information information) {
        this.information = information;
    }

    /**
     * Возвращает фильтр для этого вопроса
     *
     * @param survey
     * @return
     */
    public Filter getFilter(Survey survey) {
        final Filter filter;
        if (filterId == SurveyQuestion.LONG_NOT_SET || !survey.getFilters().containsKey(filterId)) {
            filter = new EmptyFilter(SurveyQuestion.LONG_NOT_SET);
        } else {
            filter = survey.getFilters().get(filterId);
        }
        return filter;
    }

    public List<SurveyVariant> getVariantsList() {
        return variantsList;
    }

    public Text getQuestionText() {
        return question;
    }

    public View getView(final Activity context, Fragment fragment, StatusProcessor statusProcessor, ViewFactory viewFactory) {
        if (context == null) {
            throw new NullPointerException("context");
        }
        return onGetView(context, fragment, statusProcessor, viewFactory);
    }

    protected abstract View onGetView(final Activity context, Fragment fragment, StatusProcessor statusProcessor, ViewFactory viewFactory);

    public String getHint() {
        return hint;
    }

    /**
     * Проверяет корректность заполнения ответов на вопрос.
     * Сначала проверяет количество отмеченных ответов.
     * Затем каждый из отмеченных ответов.
     * Если что-то не так - выбрасывает эксепшен.
     *
     * @throws VerificationException
     */
    public void verify() throws VerificationException {
        verifyCheckedCount();
        verifyItems();
    }

    protected abstract void verifyCheckedCount() throws VerificationException;

    protected abstract void verifyItems() throws VerificationException;

    public long getId() {
        return id;
    }

    private long startTime = 0;
    private long endTime = 0;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long time) {
        startTime = time;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long time) {
        endTime = time;
    }

    /**
     * Возвращает ответ на конкретный вопрос и сохраняет все введнные значения в варианты ответа
     * Состоит из айдишника вопроса и массива отмеченных вариантов.
     *
     * @return
     */
    public JSONObject getAnswerJson() {
        JSONObject result = new JSONObject();
        try {
            result.put("id", id);
            JSONArray answerJsonArray = new JSONArray();
            for (SurveyVariant variant : variantsList) {
                JSONObject answerJsonObject = variant.getAnswerJson(variant.isChecked());
                answerJsonArray.put(answerJsonObject);
            }
            result.put("answer", answerJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Фомирует json c вариантами ответа для отправки на серверсайд,
     * содержит отмеченный вариант ответа и если нужно, то его значение
     */
    public JSONObject getPrepareAnswerJson() {
        JSONObject result = new JSONObject();
        try {
            result.put("id", id);
            JSONArray answerJsonArray = new JSONArray();
            for (SurveyVariant variant : variantsList) {
                if (variant.isChecked()) { //сохраняем  значение только для отмеченного ответа
                    JSONObject answerJsonObject = variant.getAnswerJson();
                    answerJsonArray.put(answerJsonObject);
                }
            }
            result.put("answer", answerJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Загружает ответы в вопросы.
     * Устаревший, сейчас вместо него
     * исопльзуется loadAllAnswerJson(JSONArray answerJsonArray)
     *
     * @param answerJsonArray
     */
    @Deprecated
    public void loadAnswerJson(JSONArray answerJsonArray) {
        for (SurveyVariant surveyVariant : variantsList) {
            surveyVariant.setChecked(false);
        }
        for (int i = 0; i < answerJsonArray.length(); i++) {
            JSONObject answerJsonObject = answerJsonArray.optJSONObject(i);
            String variantId = answerJsonObject.optString("variant_id");
            for (SurveyVariant surveyVariant : variantsList) {
                if (surveyVariant.getBackId().equals(variantId)) {
                    surveyVariant.setChecked(true);
                    try {
                        surveyVariant.loadAnswerJson(answerJsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    /**
     * Загружает ответы в вопросы.
     * Причем загружает все введенный ранее пользоватлем ответы,
     * даже, если они не были выбраны в качестве ответа на вопрос
     *
     * @param answerJsonArray
     */
    public void loadAllAnswerJson(JSONArray answerJsonArray) {
        for (SurveyVariant surveyVariant : variantsList) {
            surveyVariant.setChecked(false);
        }
        for (int i = 0; i < answerJsonArray.length(); i++) {
            JSONObject answerJsonObject = answerJsonArray.optJSONObject(i);
            String variantId = answerJsonObject.optString("variant_id");
            try {
                SurveyVariant surveyVariant = variantsList.get(i);
                if (surveyVariant.getBackId().equals(variantId)) {
                    surveyVariant.setChecked(true);
                }
                try {
                    surveyVariant.loadAnswerJson(answerJsonObject);
                } catch (JSONException ignored) {
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

    }

    public abstract long[] getCheckedInnerIds();

    public abstract String[] getCheckedBackIds();

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean b) {
        passed = b;
    }

    public boolean onActivityResultOk(Intent data) {
        IntentExtraProcessor intentExtraProcessor = new IntentExtraProcessor();
        boolean result = intentExtraProcessor.processSurveyResultOk(data, variantsList);
        return result;
    }

    public boolean onActivityResultCancel(Intent data) {
        IntentExtraProcessor intentExtraProcessor = new IntentExtraProcessor();
        boolean result = intentExtraProcessor.processSurveyResultCancel(data, variantsList);
        return result;
    }

    /**
     * Количество всего выбранных вариантов ответов по конкретному вопросу (передаётся если status = passed и
     * @since 1.9.4
     * @return целове число
     */
    public int getVotersVariantsCount() {
        return votersVariantsCount;
    }

    public interface Listener {

        public static Listener STUB = new Listener() {

            @Override
            public void onBeforeClick(SurveyVariant surveyVariant) {
            }

            @Override
            public void onAfterClick(SurveyVariant surveyVariant) {
            }

            @Override
            public void onCommmit(SurveyVariant surveyVariant) {
            }

            @Override
            public void onCancel(SurveyVariant surveyVariant) {
            }

            @Override
            public void refreshSurvey() {
            }

        };

        void onBeforeClick(SurveyVariant surveyVariant);

        void onAfterClick(SurveyVariant surveyVariant);

        void onCommmit(SurveyVariant surveyVariant);

        void onCancel(SurveyVariant surveyVariant);

        /**
         * Обновляет опрос.
         */
        void refreshSurvey();

    }

    public interface ViewFactory {

        View getHeaderView(Context context);

        View getFooterView(Context context);

    }

    public static class Text {

        private final String question;
        private final String questionShort;
        private final String questionFull;

        public Text(String question, String questionShort, String questionFull) {
            this.question = question;
            this.questionShort = questionShort;
            this.questionFull = questionFull;
        }

        public String getQuestion() {
            return question;
        }

        public String getQuestionShort() {
            return questionShort;
        }

        public String getQuestionFull() {
            return questionFull;
        }

    }

}
