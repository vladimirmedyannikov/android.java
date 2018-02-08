package ru.mos.polls.support.service;


import android.os.Build;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BuildConfig;
import ru.mos.polls.UrlManager;
import ru.mos.polls.push.GCMHelper;
import ru.mos.polls.rxhttp.rxapi.config.AgApi;
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

        public Request(int subjectId, String email, String message, String orderNumber, String sessionId) {
            this.subjectId = subjectId;
            apiVersion = new ArrayList<>();
            apiVersion.add(GCMHelper.REGISTER_PATH);
            apiVersion.add(UrlManager.url(AgApi.Api.Controllers.SUPPORT, AgApi.Api.Methods.SEND_FEEDBACK));
            this.email = email;
            this.message = message;
            if (!TextUtils.isEmpty(orderNumber)) {
                this.orderNumber = orderNumber;
            }
            this.userInfo = new FeedbackUserInfo(sessionId);
        }
    }

    public static class Response extends GeneralResponse<String> {
    }

    public static class FeedbackUserInfo {
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

        public FeedbackUserInfo(String sessionId){
            app = "iSuperCitizen";
            appVersion = BuildConfig.VERSION_NAME;
            device = Build.MODEL + " (" + Build.MANUFACTURER + ")";
            os = "Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")";
            this.sessionId = sessionId;
        }
    }
}
