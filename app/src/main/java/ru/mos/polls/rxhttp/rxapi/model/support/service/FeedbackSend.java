package ru.mos.polls.rxhttp.rxapi.model.support.service;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by matek3022 on 15.09.17.
 */

public class FeedbackSend{
    public static class Request {

        @SerializedName("subject_id")
        private int subjectId;
        @SerializedName("api_version")
        private List<String> apiVersion;
        private String email;
        private String message;
        @SerializedName("order_number")
        private String orderNumber;
        @SerializedName("user_info")
        private FeedbackUserInfo userInfo;

        public Request(int subjectId, List<String> apiVersion, String email, String message, String orderNumber, FeedbackUserInfo userInfo) {
            this.subjectId = subjectId;
            this.apiVersion = apiVersion;
            this.email = email;
            this.message = message;
            this.orderNumber = orderNumber;
            this.userInfo = userInfo;
        }
    }

    public static class Response extends GeneralResponse<String> {
    }

    public class FeedbackUserInfo {
        private String app;
        @SerializedName("app_version")
        private String appVersion;
        private String device;
        private String os;
        @SerializedName("session_id")
        private String sessionId;

        public FeedbackUserInfo(String app, String appVersion, String device, String os, String sessionId) {
            this.app = app;
            this.appVersion = appVersion;
            this.device = device;
            this.os = os;
            this.sessionId = sessionId;
        }
    }
}
