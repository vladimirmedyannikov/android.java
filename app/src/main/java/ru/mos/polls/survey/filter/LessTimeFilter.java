package ru.mos.polls.survey.filter;

/**
 * Если в результате ответа на вопрос question_id, время, потраченое на этот вопрос было строго меньше seconds, то фильтр отрабатывает положительно
 */
public class LessTimeFilter extends TimeFilter {

    public LessTimeFilter(long id, double timeout, long questionId) {
        super(id, timeout, questionId);
    }

    @Override
    public boolean compare(long actualTime, long expectedTime) {
        return actualTime < expectedTime;
    }

    public static class Factory extends TimeFilter.Factory {

        @Override
        protected Filter onCreate(long filterId, double seconds, long questionId) {
            return new LessTimeFilter(filterId, seconds, questionId);
        }

    }

}
