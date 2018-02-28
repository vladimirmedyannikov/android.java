package ru.mos.polls.mypoints.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.mypoints.model.Points;
import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.base.PageAuthRequest;

public class HistoryGet {
    public static class Request extends PageAuthRequest {
        List<String> action;

        public Request(Page page) {
            super(page);
        }

        public void setAction(List<String> action) {
            this.action = action;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("history")
            private List<Points> points;
            @SerializedName("points")
            private Status status;

            public List<Points> getPoints() {
                return points;
            }

            public Status getStatus() {
                return status;
            }
        }
    }
}
