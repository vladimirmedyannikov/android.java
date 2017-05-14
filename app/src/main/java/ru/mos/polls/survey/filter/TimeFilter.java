package ru.mos.polls.survey.filter;

import org.json.JSONObject;

import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.questions.SurveyQuestion;

/**
 * Фильтр по времени прохождения опроса.
 */
public abstract class TimeFilter extends Filter {

    /**
     * Время в секундах.
     */
    private final double timeout;

    /**
     * Айдишник вопроса, время ответа на который сравнивается с timeout
     */
    private final long questionId;

    /**
     * Фильтр по времени
     *
     * @param id
     * @param timeout
     * @param questionId
     */
    public TimeFilter(long id, double timeout, long questionId) {
        super(id);
        this.timeout = timeout;
        this.questionId = questionId;
    }

    @Override
    public boolean isSuitable(Survey survey) {
        SurveyQuestion question = survey.getQuestion(questionId);
        long startTime = question.getStartTime();
        long endTime = question.getEndTime();
        if (endTime == 0) {
            endTime = System.currentTimeMillis();
        }
        long actualTime = endTime - startTime;
        long expectedTime = Math.round(timeout * 1000);
        boolean result = compare(actualTime, expectedTime);
        return result;
    }

    public abstract boolean compare(long actualTime, long expectedTime);

    public static abstract class Factory extends Filter.Factory {

        @Override
        public Filter onCreate(long filterId, JSONObject jsonObject) {
            double seconds = jsonObject.optDouble("seconds");
            long questionId = jsonObject.optLong("question_id");
            return onCreate(filterId, seconds, questionId);
        }

        protected abstract Filter onCreate(long filterId, double seconds, long questionId);

    }

}
