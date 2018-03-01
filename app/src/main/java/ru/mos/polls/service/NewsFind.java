package ru.mos.polls.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.model.NewsFindModel;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

public class NewsFind {

    public static class Request extends AuthRequest {
        @SerializedName("id")
        private long id;

        public Request(long id) {
            this.id = id;
        }
    }

    public static class Response extends GeneralResponse<NewsFindModel> {
    }
}
