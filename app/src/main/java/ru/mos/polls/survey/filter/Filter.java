package ru.mos.polls.survey.filter;

import org.json.JSONObject;

import java.io.Serializable;

import ru.mos.polls.survey.Survey;

public abstract class Filter implements Serializable {

    private final long id;

    public Filter(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public abstract boolean isSuitable(Survey survey);

    public static abstract class Factory {

        public Filter create(JSONObject jsonObject) {
            long filterId = jsonObject.optLong("id");
            return onCreate(filterId, jsonObject);
        }

        protected abstract Filter onCreate(long filterId, JSONObject jsonObject);

    }

}
