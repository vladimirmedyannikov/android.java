package ru.mos.polls.newprofile.service;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.elk.profile.Achievements;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.base.PageAuthRequest;

/**
 * Created by Trunks on 13.07.2017.
 */

public class AchievementsSelect {
    public static class Request extends PageAuthRequest {
        Integer id;

        public Request(Page page, Integer id) {
            super(page);
            this.id = id;
        }

        public Request(Page page) {
            super(page);
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("achievements")
            private List<Achievements> achievements;

            public List<Achievements> getAchievements() {
                return achievements;
            }
        }
    }
}
