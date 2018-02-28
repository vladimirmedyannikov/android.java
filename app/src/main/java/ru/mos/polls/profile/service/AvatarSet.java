package ru.mos.polls.profile.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

public class AvatarSet {
    public static class Request extends AuthRequest {
        @SerializedName("mediaId")
        private String mediaId;

        public Request(String mediaId) {
            this.mediaId = mediaId;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("percent_fill_profile")
            private int percentFillProfile;

            public int getPercentFillProfile() {
                return percentFillProfile;
            }

        }
    }
}
