package ru.mos.polls.badge.controller.service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.badge.model.Badge;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 21.02.18.
 */

public class BadgesUpdate {

    public static class Request extends AuthRequest {
        @SerializedName("badges")
        private List<BadgesRX> badges;

        public Request(long[] ids, Badge.Type type) {
            badges = new ArrayList<>();
            badges.add(new BadgesRX().setIds(ids).setType(type.getValue()));
        }

        private static class BadgesRX {
            @SerializedName("type")
            private String type;
            @SerializedName("ids")
            private long[] ids;

            public BadgesRX setType(String type) {
                this.type = type;
                return this;
            }

            public BadgesRX setIds(long[] ids) {
                this.ids = ids;
                return this;
            }
        }
    }

    public static class Response extends GeneralResponse<BadgesUpdate.Response.Result> {
        public static class Result {

        }
    }
}