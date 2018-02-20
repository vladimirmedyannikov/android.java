package ru.mos.polls.informer.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class GetAppVersion {

    public static class Request extends AuthRequest {
        String platform;

        public Request(String platform) {
            this.platform = platform;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("version")
            public String version;

            public String getVersion() {
                return version;
            }
        }
    }
}
