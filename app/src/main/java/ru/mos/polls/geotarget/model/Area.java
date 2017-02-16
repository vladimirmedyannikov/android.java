package ru.mos.polls.geotarget.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.common.model.Position;

/**
 * @since 2.3.0
 */

public class Area implements Serializable {

    public static List<Area> from(JSONArray array) {
        List<Area> result = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i <= array.length(); ++i) {
                result.add(new Area(array.optJSONObject(i)));
            }
        }
        return result;
    }

    public static JSONArray asJSONArray(List<Area> areas) {
        JSONArray result = new JSONArray();
        for (Area area : areas) {
            result.put(area.asJSONObject());
        }
        return result;
    }

    private int id;
    private Position position;
    private int r;

    public Area(JSONObject json) {
        if (json != null) {
            id = json.optInt("id");
            position = new Position(json.optDouble("lat"), json.optDouble("lon"));
            r = json.optInt("r");
        }
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public int getR() {
        return r;
    }

    public JSONObject asJSONObject() {
        JSONObject result = new JSONObject();
        try {
            result.put("id", id);
            result.put("lat", position.getLat());
            result.put("lon", position.getLon());
            result.put("r", r);
        } catch (JSONException ignored) {
        }

        return result;
    }
}
