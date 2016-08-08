package ru.mos.polls.survey.parsers;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.mos.polls.survey.variants.ActionSurveyVariant;
import ru.mos.polls.survey.variants.ImageSurveyVariant;
import ru.mos.polls.survey.variants.InputSurveyVariant;
import ru.mos.polls.survey.variants.IntervalSurveyVariant;
import ru.mos.polls.survey.variants.SelectSurveyVariant;
import ru.mos.polls.survey.variants.SurveyVariant;
import ru.mos.polls.survey.variants.TextSurveyVariant;

public class SurveyVariantFactory {

    private static final Map<String, SurveyVariant.Factory> factories = new HashMap<String, SurveyVariant.Factory>();

    static {
        factories.put("text", new TextSurveyVariant.Factory());
        factories.put("input", new InputSurveyVariant.Factory());
        factories.put("interval", new IntervalSurveyVariant.Factory());
        factories.put("select", new SelectSurveyVariant.Factory());
        factories.put("image", new ImageSurveyVariant.Factory());
        factories.put("action", new ActionSurveyVariant.Factory());
    }

    public static SurveyVariant fromJson(JSONObject jsonObject, long surveyId, long questionId, long innerVariantId) {
        if (jsonObject == null) {
            throw new NullPointerException("jsonObject");
        }
        final String kind = jsonObject.optString("kind");
        if (!factories.containsKey(kind)) {
            throw new RuntimeException("unknown variant kind " + kind);  //возможно не стоит кидать эксепшен
        }
        SurveyVariant.Factory factory = factories.get(kind);
        final SurveyVariant result = factory.create(jsonObject, surveyId, questionId, innerVariantId);
        return result;
    }

}
