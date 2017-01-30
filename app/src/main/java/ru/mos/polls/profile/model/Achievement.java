package ru.mos.polls.profile.model;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;

/**
 * Структура данных для хранения информации о достижении пользователя
 * появилась в версии 1.9
 */
public class Achievement implements Serializable {
    private String id;
    private String imageUrl;
    private String title;
    private String description;
    private String body;
    private boolean isNext;
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

    public void addId(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                jsonObject.put("achievement_id", id);
            } catch (JSONException ignored) {
            }
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

    public String getSubtitle(Context context) {
        String result = description;
        if (isNext) {
            result = context.getString(R.string.achievement_subtitle_is_next);
        }
        return result;
    }

}
