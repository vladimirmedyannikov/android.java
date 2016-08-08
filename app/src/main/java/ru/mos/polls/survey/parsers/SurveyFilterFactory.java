package ru.mos.polls.survey.parsers;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.mos.polls.survey.filter.ConcatenateFilter;
import ru.mos.polls.survey.filter.EqualsFilter;
import ru.mos.polls.survey.filter.Filter;
import ru.mos.polls.survey.filter.LessTimeFilter;
import ru.mos.polls.survey.filter.MoreTimeFilter;
import ru.mos.polls.survey.filter.NotEqualsFilter;

public class SurveyFilterFactory {

    private static Map<String, Filter.Factory> factories = new HashMap<String, Filter.Factory>();

    static {
        factories.put("less_time", new LessTimeFilter.Factory());
        factories.put("more_time", new MoreTimeFilter.Factory());
        factories.put("equal", new EqualsFilter.Factory());
        factories.put("not_equal", new NotEqualsFilter.Factory());
        factories.put("concatenate", new ConcatenateFilter.Factory());
    }

    public static Filter fromJson(JSONObject jsonObject) {
        String type = jsonObject.optString("type");
        if (!factories.containsKey(type)) {
            throw new RuntimeException("unknown filter type " + type);
        }
        final Filter result = factories.get(type).create(jsonObject);
        return result;
    }

}
