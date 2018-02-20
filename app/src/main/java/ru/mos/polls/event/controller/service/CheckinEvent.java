package ru.mos.polls.event.controller.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.common.model.Position;
import ru.mos.polls.event.model.Message;
import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 20.02.18.
 */

public class CheckinEvent {
    public static class Request extends AuthRequest {
        @SerializedName("event_id")
        private long eventId;
        @SerializedName("position")
        private Position position;


        public Request(long eventId, Position position) {
            this.position = position;
            this.eventId = eventId;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("messages")
            private Message message;
            @SerializedName("status")
            private Status status;

            public Message getMessage() {
                return message;
            }

            public Status getStatus() {
                return status;
            }
        }
    }
}