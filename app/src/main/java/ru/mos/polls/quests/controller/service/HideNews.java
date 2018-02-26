package ru.mos.polls.quests.controller.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 26.02.18.
 */

public class HideNews {
    public static class Request extends AuthRequest {
        @SerializedName("type")
        private String type;
        @SerializedName("ids")
        private List<String> ids;
        @SerializedName("hide")
        private boolean hide;

        public Request(List<String> ids) {
            this.type = "allNews";
            this.hide = true;
            if (ids != null) {
                this.ids = ids;
            }
        }
    }

    public static class Response extends GeneralResponse<HideNews.Response.Result> {
        public static class Result {

        }
    }
}