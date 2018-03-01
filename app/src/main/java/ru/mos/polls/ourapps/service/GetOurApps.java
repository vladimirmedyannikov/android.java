package ru.mos.polls.ourapps.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.ourapps.model.OurApplication;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class GetOurApps {

    public static class Request{
        @SerializedName("user_agent")
        private String userAgent;

        public Request() {
            userAgent = "android";
        }
    }

    public static class Response extends GeneralResponse<List<OurApplication>> {
    }
}
