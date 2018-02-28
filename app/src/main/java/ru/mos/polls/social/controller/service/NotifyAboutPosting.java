package ru.mos.polls.social.controller.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.social.model.Message;

public class NotifyAboutPosting {
    public static class Request extends AuthRequest {
        @SerializedName("type")
        private String type;

        public Request(AppPostValue appPostValue) {
            type = appPostValue.getSocialName();
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("status")
            private Status status;
            @SerializedName("message")
            private Message message;

            public Status getStatus() {
                return status;
            }

            public Message getMessage() {
                return message;
            }
        }
    }
}