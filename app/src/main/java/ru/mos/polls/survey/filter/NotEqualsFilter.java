package ru.mos.polls.survey.filter;

import ru.mos.polls.survey.filter.conditions.Condition;

public class NotEqualsFilter extends AnswersFilter {

    public NotEqualsFilter(long id, Answer[] answers, Condition condition) {
        super(id, answers, condition);
    }

    @Override
    public String toString() {
        return "NotEqualsFilter";
    }


    @Override
    protected boolean onContains(String variantId, String[] variantsIds) {
        final boolean contains;
        if (variantsIds.length == 1) {
            contains = !variantsIds[0].equals(variantId);
        } else {
            contains = true;
        }
        return contains;
    }

    public static class Factory extends AnswersFilter.Factory {

        @Override
        protected Filter onCreate(long filterId, Answer[] answers, Condition condition) {
            return new NotEqualsFilter(filterId, answers, condition);
        }

    }

}
