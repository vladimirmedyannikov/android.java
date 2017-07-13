package ru.mos.polls.newprofile.service;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.newprofile.model.Achievement;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.base.PageAuthRequest;

/**
 * Created by Trunks on 13.07.2017.
 */

public class AchievementsSelect {
    public static class Request extends PageAuthRequest {
        public Request(Page page) {
            super(page);
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("achievements")
            private List<Achievement> achievements;

            public List<Achievement> getAchievements() {
                return achievements;
            }
        }
    }
}
