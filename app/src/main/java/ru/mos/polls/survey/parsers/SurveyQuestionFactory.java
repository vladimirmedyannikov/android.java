package ru.mos.polls.survey.parsers;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.model.Information;
import ru.mos.polls.survey.questions.CheckboxSurveyQuestion;
import ru.mos.polls.survey.questions.RadioboxSurveyQuestion;
import ru.mos.polls.survey.questions.RankingSurveyQuestion;
import ru.mos.polls.survey.questions.SimpleSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.variants.SurveyVariant;

public class SurveyQuestionFactory {

    private static String getStringDefault(JSONObject jsonObject, String name, String defaultValue) {
        String s = jsonObject.optString(name, defaultValue);
        if (TextUtils.isEmpty(s) || "null".equals(s)) {
            s = defaultValue;
        }
        return s;
    }

    public static SurveyQuestion fromJson(JSONObject jsonObject, long surveyId) {
        if (jsonObject == null) {
            throw new NullPointerException("jsonObject");
        }
        final SurveyQuestion result;
        final long questionId = jsonObject.optLong("id");
        final int votersCount = jsonObject.optInt("voters_count");
        final int votersVariantsCount = jsonObject.optInt("voters_variants_count");

        final String question = getStringDefault(jsonObject, "question", "");
        final String questionFull = getStringDefault(jsonObject, "question_full_html", "");
        final String questionShort = getStringDefault(jsonObject, "question_short_html", "");

        final SurveyQuestion.Text questionText = new SurveyQuestion.Text(question, questionShort, questionFull);
        final String hint = getStringDefault(jsonObject, "hint", "");
        final String type = jsonObject.optString("type");
        final long filterId;
        final Information information = new Information();
        if (jsonObject.has("filter_id")) {
            filterId = jsonObject.optLong("filter_id");
        } else {
            filterId = SurveyQuestion.LONG_NOT_SET;
        }
        if (jsonObject.has("information")) {
            try {
                JSONObject informationJson = jsonObject.getJSONObject("information");
                information.setTitle(informationJson.getString("title"));
                information.setLink(informationJson.getString("link"));
            } catch (JSONException ignored) {
            }
        }
        final JSONArray variantsJsonArray = jsonObject.optJSONArray("variants");
        List<SurveyVariant> variants = new ArrayList<SurveyVariant>();
        for (int innerVariantId = 0; innerVariantId < variantsJsonArray.length(); innerVariantId++) {
            JSONObject variantJsonObject = variantsJsonArray.optJSONObject(innerVariantId);
            SurveyVariant surveyVariant = SurveyVariantFactory.fromJson(variantJsonObject, surveyId, questionId, innerVariantId);
            variants.add(surveyVariant);
        }
        if ("radiobutton".equals(type)) {
            result = new RadioboxSurveyQuestion(questionId, questionText, hint, variants, filterId, votersCount, votersVariantsCount);
        } else if ("checkbox".equals(type)) {
            int minAllowed = jsonObject.optInt("min_allowed", CheckboxSurveyQuestion.INT_NOT_SET);
            int maxAllowed = jsonObject.optInt("max_allowed", CheckboxSurveyQuestion.INT_NOT_SET);
            result = new CheckboxSurveyQuestion(questionId, questionText, hint, variants, minAllowed, maxAllowed, filterId, votersCount, votersVariantsCount);
        } else if ("ranking".equals(type)) {
            result = new RankingSurveyQuestion(questionId, questionText, hint, variants, filterId, votersCount, votersVariantsCount);
        } else if ("simple".equals(type)) {
            result = new SimpleSurveyQuestion(questionId, questionText, hint, variants, filterId, votersCount, votersVariantsCount);
        } else {
            throw new RuntimeException("unknow question type " + type);
        }
        result.setInformation(information);
        return result;
    }

}
