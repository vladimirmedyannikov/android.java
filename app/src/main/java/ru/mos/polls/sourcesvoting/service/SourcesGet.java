package ru.mos.polls.sourcesvoting.service;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.sourcesvoting.model.SourcesVoting;


public class SourcesGet {
    public static class Request extends AuthRequest {
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("list")
            private List<SourcesVoting> sourcesVotings;

            public List<SourcesVoting> getSourcesVotings() {
                return sourcesVotings;
            }
        }
    }
}
