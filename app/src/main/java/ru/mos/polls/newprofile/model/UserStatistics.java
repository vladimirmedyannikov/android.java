package ru.mos.polls.newprofile.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wlTrunks on 15.06.2017.
 */

public class UserStatistics {
    @SerializedName("title")
    String title;
    @SerializedName("value")
    String value;

    public UserStatistics(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }
}
