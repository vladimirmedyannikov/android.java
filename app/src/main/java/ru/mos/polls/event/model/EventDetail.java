package ru.mos.polls.event.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Хранение детализации о мероприятии
 */
public class EventDetail {
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("min_row_count")
    private int minRowCount;

    public EventDetail(JSONObject eventDetailJson) {
        title = eventDetailJson.optString("title");
        body = eventDetailJson.optString("body");
        minRowCount = eventDetailJson.optInt("min_row_count");
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public int getMinRowCount() {
        return minRowCount;
    }

    public JSONObject asJson() {
        JSONObject result = new JSONObject();
        try {
            result.put("title", title);
            result.put("min_row_count", minRowCount);
            result.put("body", body);
        } catch (JSONException e) {
            result = null;
        }
        return result;
    }
}
