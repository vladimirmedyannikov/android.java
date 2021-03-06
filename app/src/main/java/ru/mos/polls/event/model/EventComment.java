package ru.mos.polls.event.model;

import android.text.TextUtils;

import org.json.JSONObject;

public class EventComment {

    public EventComment(boolean editable) {
        this.editable = editable;
    }

    public static EventComment fromJson(JSONObject jsonObject, boolean editable) {
        EventComment result = new EventComment(editable);
        result.id = jsonObject.optInt("id");

        result.author = jsonObject.optString("author");
        if ("null".equalsIgnoreCase(result.author)) {
            result.author = "";
        }

        result.title = jsonObject.optString("title");
        if ("null".equalsIgnoreCase(result.title)) {
            result.title = "";
        }

        result.body = jsonObject.optString("body");
        if ("null".equalsIgnoreCase(result.body)) {
            result.body = "";
        }

        result.rating = jsonObject.optDouble("rating");
        result.checkin = jsonObject.optInt("checkin");
        result.updateDate = jsonObject.optDouble("update_date");
        return result;
    }

    private long id;
    private String author;
    private String title;
    private String body;
    private double rating;
    private int checkin;
    private double updateDate;

    private final boolean editable;

    public long getId() {
        return id;
    }

    public String getAuthor() {
        return author == null ? "" : author;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public String getBody() {
        return body == null ? "" : body;
    }

    public double getRating() {
        return rating;
    }

    public boolean isCheckIn() {
        return checkin == 1;
    }

    public double getUpdateDate() {
        return updateDate;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isEmpty() {
        return rating == 0 && TextUtils.isEmpty(body) && TextUtils.isEmpty(title);
    }

}
