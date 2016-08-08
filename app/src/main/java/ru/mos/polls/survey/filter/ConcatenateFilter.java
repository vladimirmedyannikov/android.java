package ru.mos.polls.survey.filter;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.filter.conditions.Condition;

public class ConcatenateFilter extends ConditionFilter {

    private final long[] filterIds;

    public ConcatenateFilter(long id, long[] filterIds, Condition condition) {
        super(id, condition);
        this.filterIds = filterIds;
    }

    @Override
    public boolean isSuitable(Survey survey) {
        getCondition().reset();
        for (long filterId : filterIds) {
            Filter filter = survey.getFilters().get(filterId);
            boolean suitable = filter.isSuitable(survey);
            getCondition().add(suitable);
        }
        return getCondition().get();
    }

    @Override
    public String toString() {
        return "ConcatenateFilter";
    }

    public static class Factory extends ConditionFilter.Factory {

        @Override
        protected Filter onCreate(long filterId, JSONObject jsonObject, Condition condition) {
            JSONArray filtersIds = jsonObject.optJSONArray("filter_ids");
            long[] ids = new long[filtersIds.length()];
            for (int i = 0; i < filtersIds.length(); i++) {
                ids[i] = filtersIds.optLong(i);
            }
            return new ConcatenateFilter(filterId, ids, condition);
        }
    }

}
