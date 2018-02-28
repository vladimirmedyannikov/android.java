package ru.mos.polls.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.common.model.Message;
import ru.mos.polls.model.PromoAddStatus;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class PromoAddCode {
    public static class Request extends AuthRequest {
        @SerializedName("user_agent")
        private String userAgent;
        @SerializedName("code_name")
        private String code_name;

        public Request(String code_name) {
            this.code_name = code_name;
            this.userAgent = "android";
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("status")
            private PromoAddStatus status;
            @SerializedName("message")
            private Message message;

            public PromoAddStatus getStatus() {
                return status;
            }

            public Message getMessage() {
                return message;
            }
        }
    }
}