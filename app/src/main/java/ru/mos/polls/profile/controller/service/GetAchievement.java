package ru.mos.polls.profile.controller.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.profile.model.Achievement;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

public class GetAchievement {
    public static class Request extends AuthRequest {
        @SerializedName("achievement_id")
        String achievementId;

        public Request(String achievementId) {
            this.achievementId = achievementId;
        }
    }

    public static class Response extends GeneralResponse<Achievement> {
    }
}
