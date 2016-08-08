package ru.mos.polls.survey.filter;

import org.json.JSONObject;

import ru.mos.polls.survey.filter.conditions.AndCondition;
import ru.mos.polls.survey.filter.conditions.Condition;
import ru.mos.polls.survey.filter.conditions.OrCondition;

public abstract class ConditionFilter extends Filter {

    private final Condition condition;

    public ConditionFilter(long id, Condition condition) {
        super(id);
        if (condition == null) {
            throw new NullPointerException("condition");
        }
        this.condition = condition;
    }

    protected Condition getCondition() {
        return condition;
    }

    public static abstract class Factory extends Filter.Factory {

        @Override
        protected Filter onCreate(long filterId, JSONObject jsonObject) {
            String conditionName = jsonObject.optString("condition");
            final Condition condition;
            if ("or".equals(conditionName)) {
                condition = new OrCondition();
            } else if ("and".equals(conditionName)) {
                condition = new AndCondition();
            } else {
                condition = null;
            }
            return onCreate(filterId, jsonObject, condition);
        }

        protected abstract Filter onCreate(long filterId, JSONObject jsonObject, Condition condition);
    }

}
