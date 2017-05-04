package ru.mos.polls.survey.filter;

/**
 * Противопроложное условию less_time, то есть положительный результат дает ответ в течении времени, строго больше чем seconds
 */
public class MoreTimeFilter extends TimeFilter {

    public MoreTimeFilter(long id, double timeout, long questionId) {
        super(id, timeout, questionId);
    }

    @Override
    public boolean compare(long actualTime, long expectedTime) {
        return actualTime > expectedTime;
    }

    public static class Factory extends TimeFilter.Factory {

        @Override
        protected Filter onCreate(long filterId, double seconds, long questionId) {
            return new MoreTimeFilter(filterId, seconds, questionId);
        }

    }

}
