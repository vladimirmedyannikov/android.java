package ru.mos.polls.survey;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

import ru.mos.polls.AGApplication;
import ru.mos.polls.survey.questions.SurveyQuestion;

public class SharedPreferencesSurveyManager {

    private static final String SURVEY = "surveys";
    private static final String PAGE = "page_%s_";
    private static final String ANSWERS = "answers_%s_";
    private static final String PASSED = "passed_%s_%s";
    private static final String TIME_START = "time_start_%s_%s";
    private static final String TIME_END = "time_end_%s_%s";
    private static final String SCROLLABLE_INDEX = "scrollable_index_%s_%s";
    private static final String SCROLLABLE_TOP = "scrollable_top_%s_%s";

    private static String getSurveyName() {
        return String.format(SURVEY);
    }

    private static String getPageName(long surveyId) {
        return String.format(PAGE, surveyId);
    }

    private static String getPassedName(long surveyId, long questionId) {
        return String.format(PASSED, surveyId, questionId);
    }

    private static String getTimeStartName(long surveyId, long questionId) {
        return String.format(TIME_START, surveyId, questionId);
    }

    private static String getTimeEndName(long surveyId, long questionId) {
        return String.format(TIME_END, surveyId, questionId);
    }

    private static String getAnsewersName(long surveyId) {
        return String.format(ANSWERS, surveyId);
    }

    private static String getScrollableIndexName(long surveyId, long questionId) {
        return String.format(SCROLLABLE_INDEX, surveyId, questionId);
    }

    private static String getScrollableTopName(long surveyId, long questionId) {
        return String.format(SCROLLABLE_TOP, surveyId, questionId);
    }

    private Context context;

    public SharedPreferencesSurveyManager(Context c) {
        if (c == null) {
            throw new NullPointerException("context");
        }
        context = c;
    }

    public void fill(Survey survey) {
        try {
            final long surveyId = survey.getId();
            String name = getSurveyName();
            SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            for (SurveyQuestion question : survey.getQuestionsList()) {
                final long questionId = question.getId();
                {
                    String passedName = getPassedName(surveyId, questionId);
                    boolean passed = sp.getBoolean(passedName, false);
                    if (!question.isPassed()) {
                        question.setPassed(passed);
                    }
                }
                {
                    String startTimeName = getTimeStartName(surveyId, questionId);
                    long startTime = sp.getLong(startTimeName, 0);
                    question.setStartTime(startTime);
                }
                {
                    String endTimeName = getTimeEndName(surveyId, questionId);
                    long endTime = sp.getLong(endTimeName, 0);
                    question.setEndTime(endTime);
                }
            }
            String answersName = getAnsewersName(surveyId);
            String answers = sp.getString(answersName, new JSONArray().toString());
            try {
                JSONArray answersJsonArray = new JSONArray(answers);
                survey.loadAnswersJsonLocal(answersJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int page = sp.getInt(getPageName(surveyId), 0);
            if (AGApplication.SET_PAGE_ON_SURVEY_FILL) {
                survey.setCurrentPageIndex(page);
            } else {
                survey.setCurrentPageIndex(0);
            }
        } catch (Exception ignored) {
        }
    }

    public void saveCurrentPage(Survey survey) {
        try {
            if (survey == null) {
                return;
            }

            SurveyQuestion question = survey.getQuestion(survey.getCurrentQuestionId());

            long surveyId = survey.getId();
            long questionId = question.getId();

            SharedPreferences sp = context.getSharedPreferences(getSurveyName(), Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sp.edit();
            int pageIndex = survey.getCurrentPageIndex();
            editor.putInt(getPageName(surveyId), pageIndex);
            {
                String passedName = getPassedName(surveyId, questionId);
                boolean passed = question.isPassed();
                editor.putBoolean(passedName, passed);
            }
            {
                String startTimeName = getTimeStartName(surveyId, questionId);
                long startTime = question.getStartTime();
                editor.putLong(startTimeName, startTime);
            }
            {
                String endTimeName = getTimeEndName(surveyId, questionId);
                long endTime = question.getEndTime();
                editor.putLong(endTimeName, endTime);
            }
            JSONArray answersJsonArray = survey.getAnswersJson();
            String answersName = getAnsewersName(surveyId);
            String answers = answersJsonArray.toString();

            editor.putString(answersName, answers);

            editor.commit();
        } catch (Exception ignored) {
        }
    }

    public void saveScrollableIndex(long surveyId, long questionId, int index) {
        String name = getSurveyName();
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sp.edit().putInt(getScrollableIndexName(surveyId, questionId), index).commit();
    }

    public void saveScrollableTop(long surveyId, long questionId, int top) {
        String name = getSurveyName();
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sp.edit().putInt(getScrollableTopName(surveyId, questionId), top).commit();
    }

    public int getScrollableIndex(long surveyId, long questionId) {
        String name = getSurveyName();
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getInt(getScrollableIndexName(surveyId, questionId), 0);
    }

    public int getScrollableTop(long surveyId, long questionId) {
        String name = getSurveyName();
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getInt(getScrollableTopName(surveyId, questionId), 0);
    }

    public void remove(long surveyId) {
        String name = getSurveyName();
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Map<String, ?> map = sp.getAll();
        SharedPreferences.Editor editor = sp.edit();
        for (String key : map.keySet()) { //отбираем те элемнеты, в которых присутствует айдишник опроса
            if (key.contains("_" + surveyId + "_")) {
                editor.remove(key);
            }
        }
        editor.commit();
    }

    public void removeAll() {
        String name = getSurveyName();
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().commit();
    }
}
