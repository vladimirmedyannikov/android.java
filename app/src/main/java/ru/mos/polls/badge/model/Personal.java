package ru.mos.polls.badge.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Структура данных, описывающая пользователя.
 * Данная информация используется для идентификации
 * пользователя в главном меню приложения
 *
 */
public class Personal {
    private String phone, surname, firstName, middleName, icon;

    public Personal(JSONObject json) {
        if (json != null) {
            phone = json.optString("phone");
            surname = json.optString("surname");
            firstName = json.optString("firstname");
            middleName = json.optString("middlename");
            icon = json.optString("icon");
        }
    }

    public JSONObject asJson() {
        JSONObject result = new JSONObject();
        try {
            result.put("phone", phone);
            result.put("surname", surname);
            result.put("firstName", firstName);
            result.put("middleName", middleName);
            result.put("icon", icon);
        } catch (JSONException ignored) {
        }
        return result;
    }

    public String getPhone() {
        return phone;
    }

    public String getSurname() {
        return surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getIcon() {
        return icon;
    }

}
