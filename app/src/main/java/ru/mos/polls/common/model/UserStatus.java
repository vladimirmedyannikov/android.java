package ru.mos.polls.common.model;

import org.json.JSONObject;

/**
 * Структура данных, описывающая баллы пользователя, часто является ответом от серверсайда
 */
public class UserStatus {
    private int burntPoints;
    private int spentPoints;
    private int allPoints;
    private int currentPoints;
    private int freezedPoints;
    private String state;

    public UserStatus(JSONObject statusJson) {
        if (statusJson != null) {
            burntPoints = statusJson.optInt("burnt_points");
            spentPoints = statusJson.optInt("spent_points");
            allPoints = statusJson.optInt("all_points");
            currentPoints = statusJson.optInt("current_points");
            freezedPoints = statusJson.optInt("freezed_points");
            state = statusJson.optString("state");
        }
    }

    public int getBurntPoints() {
        return burntPoints;
    }

    public int getSpentPoints() {
        return spentPoints;
    }

    public int getAllPoints() {
        return allPoints;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public int getFreezedPoints() {
        return freezedPoints;
    }

    public String getState() {
        return state;
    }
}
