package ru.mos.polls.profile.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Структура данных для хранения информации о достижении пользователя
 * появилась в версии 1.9
 */
public class Achievement implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("img_url")
    private String imageUrl;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("body")
    private String body;
    @SerializedName("is_next")
    private boolean isNext;
    @SerializedName("need_hide_task")
    private boolean isNeedHideTask;

    public static List<Achievement> fromJsonArray(JSONArray jsonArray) {
        List<Achievement> result = null;
        if (jsonArray != null) {
            result = new ArrayList<Achievement>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                Achievement achievement = new Achievement(jsonObject);
                result.add(achievement);
            }
        }
        return result;
    }

    public Achievement(JSONObject json) {
        if (json != null) {
            id = json.optString("id");
            imageUrl = json.optString("img_url");
            title = json.optString("title");
            description = json.optString("description");
            body = json.optString("body");
            isNext = json.optBoolean("is_next");
            isNeedHideTask = json.optBoolean("need_hide_task");
        }
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getBody() {
        return body;
    }

    public boolean isNext() {
        return isNext;
    }

    public boolean isNeedHideTask() {
        return isNeedHideTask;
    }

}
