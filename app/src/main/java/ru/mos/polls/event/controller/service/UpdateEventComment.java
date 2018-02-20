package ru.mos.polls.event.controller.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 20.02.18.
 */

public class UpdateEventComment {
    public static class Request extends AuthRequest {
        @SerializedName("event_id")
        private long eventId;
        @SerializedName("title")
        private String title;
        @SerializedName("body")
        private String body;
        @SerializedName("rating")
        private int rating;


        public Request(long eventId, String title, String body, int rating) {
            this.eventId = eventId;
            this.title = title;
            this.body = body;
            this.rating = rating;
        }
    }

    public static class Response extends GeneralResponse<Long> {

    }
}