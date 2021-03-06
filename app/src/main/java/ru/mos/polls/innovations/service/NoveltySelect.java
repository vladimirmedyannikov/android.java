package ru.mos.polls.innovations.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.base.PageAuthRequest;
import ru.mos.polls.innovations.model.Innovation;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 17.05.17 11:22.
 */

public class NoveltySelect {

    public static class Request extends PageAuthRequest {

        public Request(Page page) {
            super(page);
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("novelties")
            private List<Innovation> innovations;

            public List<Innovation> getInnovations() {
                return innovations;
            }
        }
    }

}
