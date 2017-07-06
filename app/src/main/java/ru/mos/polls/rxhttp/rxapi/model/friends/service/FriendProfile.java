package ru.mos.polls.rxhttp.rxapi.model.friends.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.friends.Personal;
import ru.mos.polls.rxhttp.rxapi.model.friends.Statistic;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 11:49.
 */

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
