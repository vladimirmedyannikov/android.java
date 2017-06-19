package ru.mos.polls.newprofile.model;

/**
 * Created by Trunks on 19.06.2017.
 */

public class UserInfo {

    String title;
    String value;

    public UserInfo(String title, String value) {
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
