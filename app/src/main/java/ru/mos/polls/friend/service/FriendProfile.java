package ru.mos.polls.friend.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.friend.model.AchievementsData;
import ru.mos.polls.friend.model.Personal;
import ru.mos.polls.friend.model.Statistic;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class FriendProfile {
    public static class Request extends AuthRequest {
        private int id;

        public Request(int id) {
            super();
            this.id = id;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("is_profile_visible")
            private boolean isProfileVisible;
            private Personal personal;
            private Statistic statistics;
            private AchievementsData achievements;

            public AchievementsData getAchievements() {
                return achievements;
            }

            public boolean isProfileVisible() {
                return isProfileVisible;
            }

            public Personal getPersonal() {
                return personal;
            }

            public Statistic getStatistics() {
                return statistics;
            }
        }
    }
}
