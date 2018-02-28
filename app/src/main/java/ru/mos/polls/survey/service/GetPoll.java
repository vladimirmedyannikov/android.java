package ru.mos.polls.survey.service;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

public class GetPoll {
    public static class Request extends AuthRequest {
        @SerializedName("hearing_id")
        Long hearingId;
        @SerializedName("poll_id")
        Long pollId;

        public void setHearingId(Long hearingId) {
            this.hearingId = hearingId;
        }

        public void setPollId(Long pollId) {
            this.pollId = pollId;
        }
    }

    public static class Response extends GeneralResponse<JsonObject> {
    }
}
