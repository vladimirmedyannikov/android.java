package ru.mos.polls.profile.model;

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
