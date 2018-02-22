package ru.mos.polls.survey.service;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;


import org.json.JSONObject;

import java.util.List;

import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Trunks on 22.02.2018.
 */

public class FillPoll {
    public static class Request extends AuthRequest {
        @SerializedName("hearing_id")
        Long hearingId;
        @SerializedName("poll_id")
        Long pollId;
        @SerializedName("poll_status")
        String pollStatus;
        JsonArray values;

        public void setValues(JsonArray values) {
            this.values = values;
        }

        public void setHearingId(Long hearingId) {
            this.hearingId = hearingId;
        }

        public void setPollId(Long pollId) {
            this.pollId = pollId;
        }

        public void setPollStatus(String pollStatus) {
            this.pollStatus = pollStatus;
        }
    }

    public static class Response extends GeneralResponse<FillPoll.Response.Result> {
        public static class Result {
            @SerializedName("poll_status")
            String pollStatus;
            @SerializedName("added_points")
            int addedPoints;
            Status status;

            public String getPollStatus() {
                return pollStatus;
            }

            public int getAddedPoints() {
                return addedPoints;
            }

            public Status getStatus() {
                return status;
            }
        }
    }
}
