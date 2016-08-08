package ru.mos.polls.survey.parsers;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.common.model.Message;
import ru.mos.polls.model.Information;
import ru.mos.polls.model.Poll;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.filter.Filter;
import ru.mos.polls.survey.hearing.model.Exposition;
import ru.mos.polls.survey.hearing.model.Meeting;
import ru.mos.polls.survey.questions.SurveyQuestion;

public class SurveyFactory {

    public static Survey fromJson(JSONObject jsonObject) {
        JSONObject detailsJsonObject = jsonObject.optJSONObject("details");
        long surveyId = detailsJsonObject.optLong("id");
        String statusStr = detailsJsonObject.optString("status");
        String title = getString(detailsJsonObject, "title", "");
        String textFullHtml = getString(detailsJsonObject, "text_full_html", "");
        String textShortHtml = getString(detailsJsonObject, "text_short_html", "");
        int points = detailsJsonObject.optInt("points");
        Kind kind = Kind.parse(detailsJsonObject.optString("kind"));
        Survey.Status status = Survey.Status.parse(statusStr);
        long beginDate = detailsJsonObject.optLong("begin_date");
        long endDate = detailsJsonObject.optLong("end_date");
        long passedDate = detailsJsonObject.optLong("passed_date");
        boolean isHearingChecked = detailsJsonObject.optBoolean("is_hearing_checked");
        boolean showPollStats = detailsJsonObject.optBoolean("show_poll_stats");
        int votersCount = detailsJsonObject.optInt("voters_count");

        List<SurveyQuestion> list = getQuestionList(jsonObject, surveyId);

        Survey result = new Survey(surveyId, status, list);
        result.setTitle(title);
        result.setTextFullHtml(textFullHtml);
        result.setTextShortHtml(textShortHtml);
        result.setPoints(points);
        result.setKind(kind);
        result.setBeginDate(beginDate * 1000);
        result.setEndDate(endDate * 1000);
        result.setPassedDate(passedDate * 1000);
        result.setHearingChecked(isHearingChecked);
        result.setShowPollStats(showPollStats);
        result.setVotersCount(votersCount);

        JSONArray valuesJsonArray = jsonObject.optJSONArray("values");
        result.loadAnswersJsonServer(valuesJsonArray);
        for (SurveyQuestion question : result.getQuestionsList()) {
            try {
                question.verify();
                question.setPassed(true);
            } catch (VerificationException e) {
                question.setPassed(false);
            }
        }

        loadFilters(result, jsonObject);
        result.setDetailsExperts(getDetailsExperts(jsonObject));
        result.setInformation(getInformation(jsonObject));
        result.setMessage(getMessage(jsonObject));

        result.setHearingType(detailsJsonObject.optString("hearing_type"));
        result.setExpositions(Exposition.from(jsonObject.optJSONArray("expositions")));
        result.setMeetings(Meeting.from(jsonObject.optJSONArray("meetings")));

        return result;
    }

    private static void loadFilters(Survey result, JSONObject jsonObject) {
        JSONArray filtersJsonArray = jsonObject.optJSONArray("filters");
        if (filtersJsonArray != null) {
            for (int i = 0; i < filtersJsonArray.length(); i++) {
                JSONObject filterJsonObject = filtersJsonArray.optJSONObject(i);
                Filter filter = SurveyFilterFactory.fromJson(filterJsonObject);
                result.filterAdd(filter);
            }
        }
    }

    private static List<SurveyQuestion> getQuestionList(JSONObject jsonObject, long surveyId) {
        JSONArray questionsJsonArray = jsonObject.optJSONArray("questions");
        List<SurveyQuestion> list = new ArrayList<SurveyQuestion>();
        for (int i = 0; i < questionsJsonArray.length(); i++) {
            JSONObject questionJsonObject = questionsJsonArray.optJSONObject(i);
            SurveyQuestion surveyQuestion = SurveyQuestionFactory.fromJson(questionJsonObject, surveyId);
            surveyQuestion.setDetailsExpert(getDetailsExperts(questionJsonObject));
            list.add(surveyQuestion);
        }
        return list;
    }

    private static Survey.Status convertStatus(int pollStatus) {
        if (pollStatus == Poll.PASSED)
            return Survey.Status.PASSED;
        return Survey.Status.ACTIVE;
    }


    private static List<DetailsExpert> getDetailsExperts(JSONObject jsonObject) {
        JSONArray expertsJsonObject = jsonObject.optJSONArray("experts");
        return DetailsExpert.fromJson(expertsJsonObject);
    }

    private static Information getInformation(JSONObject jsonObject) {
        if (!jsonObject.has("information")) return null;
        try {
            jsonObject = jsonObject.getJSONObject("information");
            return new Information(jsonObject.getString("title"), jsonObject.getString("link"));
        } catch (JSONException ignored) {
        }
        return null;
    }

    private static Message getMessage(JSONObject jsonObject) {
        Message result = null;
        if (jsonObject.has("messages")) {
            try {
                JSONObject messageJson = jsonObject.getJSONObject("messages");
                result = new Message(messageJson);
            } catch (JSONException ignored) {
            }
        }
        return result;
    }

    private static String getString(JSONObject jsonObject, String tag, String defaultValue) {
        String result = jsonObject.optString(tag);
        if (TextUtils.isEmpty(result) || "null".equalsIgnoreCase(result)) {
            result = defaultValue;
        }
        return result;
    }

}
