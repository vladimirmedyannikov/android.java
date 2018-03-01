package ru.mos.polls.model;

import com.google.gson.annotations.SerializedName;

public class NewsFindModel {

    @SerializedName("link_url")
    private String linkUrl;
    @SerializedName("link_title")
    private String linkTitle;
    @SerializedName("img")
    private String img;
    @SerializedName("public_site_url")
    private String publicSiteUrl;
    @SerializedName("id")
    private long id;

    @SerializedName("need_hide_task")
    private boolean needHideTask = false;
    @SerializedName("poll_id")
    private long pollId;
    @SerializedName("hearing_id")
    private long hearingId;
    @SerializedName("unviewed")
    private boolean unviewed;

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public String getImg() {
        return img;
    }

    public String getPublicSiteUrl() {
        return publicSiteUrl;
    }

    public long getId() {
        return id;
    }

    public boolean isNeedHideTask() {
        return needHideTask;
    }

    public long getPollId() {
        return pollId;
    }

    public long getHearingId() {
        return hearingId;
    }

    public boolean isUnviewed() {
        return unviewed;
    }
}
