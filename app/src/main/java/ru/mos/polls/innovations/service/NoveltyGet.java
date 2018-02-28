package ru.mos.polls.innovations.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.innovations.model.InnovationDetails;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class NoveltyGet {
    public static class Request extends AuthRequest {
        @SerializedName("novelty_id")
        private long noveltyId;

        public Request(long noveltyId) {
            this.noveltyId = noveltyId;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            private InnovationDetails details;

            public InnovationDetails getDetails() {
                return details;
            }
        }
    }

}
