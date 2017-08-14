package ru.mos.polls.badge.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Структура данных, описывающая конкретный тип байджев
 *
 */
public class Badge {
    public static List<Badge> fromJson(JSONArray array) {
        List<Badge> result = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); ++i) {
                result.add(new Badge(array.optJSONObject(i)));
            }
        }
        return result;
    }

    private Type type;
    private int count;
    private long[] ids;

    public Badge(JSONObject json) {
        if (json != null) {
            type = Type.parse(json.optString("type"));
            count = json.optInt("count");
            JSONArray array = json.optJSONArray("ids");
            if (array != null) {
                ids = new long[array.length()];
                for (int i = 0; i < array.length(); ++i) {
                    ids[i] = array.optLong(i);
                }
            }
        }
    }

    public JSONObject asJson() {
        JSONObject result = new JSONObject();
        try {
            result.put("type", type.getValue());
            result.put("count", count);
            result.put("ids", idsAsJson());
        } catch (JSONException ignored) {
        }
        return result;
    }

    public boolean hasIds() {
        return ids != null && ids.length > 0;
    }

    public int getCount() {
        return count;
    }

    public long[] getIds() {
        return ids;
    }

    public boolean forPoll() {
        return type.isPolls();
    }

    public boolean forNovelty() {
        return type.isNovelties();
    }

    public boolean forNew() {
        return type.isNews();
    }

    public Type getType() {
        return type;
    }

    private JSONArray idsAsJson() {
        JSONArray result = new JSONArray();
        for (int i = 0; i < ids.length; ++i) {
            result.put(ids[i]);
        }
        return result;
    }

    public enum Type {
        POLLS("poll"),
        NOVELTIES("novelty"),
        NEWS("news"),
        FRIENDS("friends");

        public static Type parse(String value) {
            Type result = null;
            if (POLLS.value.equalsIgnoreCase(value)) {
                result = POLLS;
            } else if (NOVELTIES.value.equalsIgnoreCase(value)) {
                result = NOVELTIES;
            } else if (NEWS.value.equalsIgnoreCase(value)) {
                result = NEWS;
            } else if (FRIENDS.value.equalsIgnoreCase(value)) {
                result = FRIENDS;
            }
            return result;
        }

        private String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public boolean isPolls() {
            return this == POLLS;
        }

        public boolean isNovelties() {
            return this == NOVELTIES;
        }

        public boolean isNews() {
            return this == NEWS;
        }
    }

}
