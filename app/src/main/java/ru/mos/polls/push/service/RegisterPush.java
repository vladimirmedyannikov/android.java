package ru.mos.polls.push.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

public class RegisterPush {
    public static class Request{

        @SerializedName("device_info")
        private DeviceInfo deviceInfo;
        @SerializedName("empAction")
        private String empAction;

        public Request(String guid, String objectId, String appVersion) {
            empAction = "registerPush";
            deviceInfo = new DeviceInfo(guid, objectId, "Android", appVersion);
        }

        static class DeviceInfo {
            @SerializedName("guid")
            private String guid;
            @SerializedName("object_id")
            private String objectId;
            @SerializedName("user_agent")
            private String userAgent;
            @SerializedName("app_version")
            private String appVersion;

            public DeviceInfo(String guid, String objectId, String userAgent, String appVersion) {
                this.guid = guid;
                this.objectId = objectId;
                this.userAgent = userAgent;
                this.appVersion = appVersion;
            }
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
        }
    }
}
