package ru.mos.polls.ourapps.model;

import com.google.gson.annotations.SerializedName;

public class OurApplication {

    @SerializedName("type")
    private String type;
    @SerializedName("body")
    private String body;
    @SerializedName("link_to_store")
    private String linkToStore;
    @SerializedName("title")
    private String title;
    @SerializedName("link_to_icon")
    private String linkToIcon;

    public String getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    public String getLinkToStore() {
        return linkToStore;
    }

    public String getTitle() {
        return title;
    }

    public String getLinkToIcon() {
        return linkToIcon;
    }
}
