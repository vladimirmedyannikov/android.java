package ru.mos.polls.survey.filter;

import ru.mos.polls.survey.filter.conditions.Condition;

public class EqualsFilter extends AnswersFilter {

    public EqualsFilter(long id, Answer[] answers, Condition condition) {
        super(id, answers, condition);
    }

    @Override
    public String toString() {
        return "EqualsFilter";
    }

    /**
     * проверка происходит содержит ли массив ответов значение фильтра
     */
    @Override
    protected boolean onContains(String variantId, String[] variantsIds) {
        if (variantId == null) {
            return false; //зачем это? убирать страшно - оставлю. 10.07.2014
        }
        boolean contains = false;
        if (variantsIds.length > 0) {
            for (String varId : variantsIds) {
                contains = variantId.equals(varId);
                if (contains) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    public static class Factory extends AnswersFilter.Factory {

        @Override
        protected Filter onCreate(long filterId, Answer[] answers, Condition condition) {
            return new EqualsFilter(filterId, answers, condition);
        }

    }

}
