package ru.mos.polls.innovations.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.innovations.model.InnovationDetails;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 31.05.17 11:51.
 */

public class NoveltyGet {
    public static class Request extends AuthRequest {
        @SerializedName("novelty_id")
        private int noveltyId;

        public Request(int noveltyId) {
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
