package ru.mos.polls.quests.controller.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 26.02.18.
 */

public class Hide {
    public static class Request extends AuthRequest {
        @SerializedName("type")
        private String type;
        @SerializedName("id")
        private String id;
        @SerializedName("hide")
        private boolean hide;

        public Request(String type, String id, boolean hide) {
            this.type = type;
            this.id = id;
            this.hide = hide;
        }
    }

    public static class Response extends GeneralResponse<Hide.Response.Result> {
        public static class Result {

        }
    }
}