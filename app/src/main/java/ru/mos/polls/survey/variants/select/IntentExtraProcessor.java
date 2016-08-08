package ru.mos.polls.survey.variants.select;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.variants.SurveyVariant;

public class IntentExtraProcessor {

    public static final String EXTRA_IS_SURVEY = "is_survey";
    private static final String EXTRA_SURVEY_ID = "survey_id";
    private static final String EXTRA_QUESTION_ID = "question_id";
    public static final String EXTRA_VARIANT_ID = "variant_id";
    private static final String EXTRA_ACTIVITY_TITLE = "title";
    private static final String EXTRA_EMPTY_TEXT = "empty_text";

    private static final long LONG_NOT_SET = -1;

    private String title;
    private String emptyText;
    private long surveyId;
    private long questionId;
    private String variantId;

    /**
     * Должно вызываться в onCreate активити например.
     *
     * @param intent
     */
    public void initialize(Intent intent) {
        surveyId = intent.getLongExtra(EXTRA_SURVEY_ID, LONG_NOT_SET);
        if (surveyId == LONG_NOT_SET) {
            throw new InvalidParameterException("survey_id");
        }
        questionId = intent.getLongExtra(EXTRA_QUESTION_ID, LONG_NOT_SET);
        if (questionId == LONG_NOT_SET) {
            throw new InvalidParameterException("question_id");
        }
        variantId = intent.getStringExtra(EXTRA_VARIANT_ID);
        if (TextUtils.isEmpty(variantId)) {
            throw new InvalidParameterException("variant_id");
        }
        title = intent.getStringExtra(EXTRA_ACTIVITY_TITLE);
        emptyText = intent.getStringExtra(EXTRA_EMPTY_TEXT);
    }

    public Intent generateResultIntent() {
        Intent data = new Intent();
        data.putExtra(EXTRA_IS_SURVEY, true);
        data.putExtra(EXTRA_SURVEY_ID, surveyId);
        data.putExtra(EXTRA_QUESTION_ID, questionId);
        data.putExtra(EXTRA_VARIANT_ID, variantId);
        data.putExtra(EXTRA_SURVEY_ID, surveyId);
        return data;
    }

    public boolean processResult(long currentSurveyId, Map<Long, SurveyQuestion> questions, int requestCode, int resultCode, Intent data) {
        final boolean result;
        boolean isSurvey = data.getBooleanExtra(EXTRA_IS_SURVEY, false);
        if (isSurvey) {
            long surveyId = data.getLongExtra(EXTRA_SURVEY_ID, LONG_NOT_SET);
            if (currentSurveyId == surveyId) {
                long questionId = data.getLongExtra(EXTRA_QUESTION_ID, LONG_NOT_SET);
                SurveyQuestion question = questions.get(questionId);
                if (question != null) {
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            result = question.onActivityResultOk(data);
                            break;
                        case Activity.RESULT_CANCELED:
                            result = question.onActivityResultCancel(data);
                            break;
                        default:
                            result = false;
                    }
                } else {
                    result = false;
                }
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    public Intent generateStartActivityIntent(Intent i, String activityTitle, String emptyText, long surveyId, long questionId, String variantId) {
        i.putExtra(EXTRA_ACTIVITY_TITLE, activityTitle);
        i.putExtra(EXTRA_SURVEY_ID, surveyId);
        i.putExtra(EXTRA_QUESTION_ID, questionId);
        i.putExtra(EXTRA_VARIANT_ID, variantId);
        i.putExtra(EXTRA_EMPTY_TEXT, emptyText);
        return i;
    }

    public String getTitle() {
        return title;
    }

    public boolean processSurveyResultOk(Intent data, List<SurveyVariant> variantsList) {
        boolean result = false;
        String variantId = data.getStringExtra(EXTRA_VARIANT_ID);
        for (SurveyVariant variant : variantsList) {
            if (variant.getBackId().equals(variantId)) {
                result = variant.onActivityResultOk(data);
                break;
            }
        }
        return result;
    }

    public boolean processSurveyResultCancel(Intent data, List<SurveyVariant> variantsList) {
        boolean result = false;
        String variantId = data.getStringExtra(EXTRA_VARIANT_ID);
        for (SurveyVariant variant : variantsList) {
            if (variant.getBackId().equals(variantId)) {
                result = variant.onActivityResultCancel(data);
                break;
            }
        }
        return result;
    }

    public String getEmptyText() {
        return emptyText;
    }

    public interface ResultProcessor {

        void onProcessResultOk(SurveyQuestion question, Intent data);

        void onProcessResultCancel(SurveyQuestion question, Intent data);

    }

}
