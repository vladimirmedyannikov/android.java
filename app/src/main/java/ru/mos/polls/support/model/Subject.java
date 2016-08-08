package ru.mos.polls.support.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Cтурктура данных для хранения темы обратной связи
 * <p/>
 */
public class Subject {
    public final static int ID_SHOPS_BONUS = 5;
    public final static String WORD_SHOP_BONUS = "Магазин поощрений";
    private int id;
    private String title;

    public Subject(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static List<Subject> getDefault() {
        return fromJson(null);
    }

    public static List<Subject> fromJson(JSONArray subjectsJson) {
        List<Subject> result = new ArrayList<Subject>();
        result.add(new Subject(0, "Не выбрана"));
        if (subjectsJson != null) {
            for (int i = 0; i < subjectsJson.length(); ++i) {
                JSONObject subjectJson = subjectsJson.optJSONObject(i);
                Subject subject = new Subject(subjectJson);
                result.add(subject);
            }
        }
        return result;
    }

    public Subject(JSONObject subjectJson) {
        if (subjectJson != null) {
            id = subjectJson.optInt("id");
            title = subjectJson.optString("title");
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isEmpty() {
        return id == 0;
    }

    @Override
    public String toString() {
        return title;
    }
}
