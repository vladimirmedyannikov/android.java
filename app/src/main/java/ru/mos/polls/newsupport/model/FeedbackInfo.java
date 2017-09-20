package ru.mos.polls.newsupport.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by matek3022 on 18.09.17.
 */

public class FeedbackInfo  {

    private String app;
    @SerializedName("app_version")
    private String appVersion;
    private String device;
    private String os;
    @SerializedName("session_id")
    private String sessionId;

    public FeedbackInfo(String app, String appVersion, String device, String os, String sessionId) {
        this.app = app;
        this.appVersion = appVersion;
        this.device = device;
        this.os = os;
        this.sessionId = sessionId;
    }
}
