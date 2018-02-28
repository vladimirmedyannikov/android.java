package ru.mos.polls.badge.controller.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.badge.model.Badge;
import ru.mos.polls.badge.model.Personal;
import ru.mos.polls.badge.model.State;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class BadgesGet {
    public static class Request extends AuthRequest {
        public Request() {
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("personal")
            private Personal personal;
            @SerializedName("points_count")
            private int pointsCount;
            @SerializedName("badges")
            private List<Badge> badges;

            public State getBadgeState() {
                return new State(personal, pointsCount, badges);
            }
        }
    }
}