package ru.mos.polls.sourcesvoting.service;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.sourcesvoting.model.SourcesVoting;


public class SourcesSet {
    public static class Request extends AuthRequest {
        public Request(List<SourcesVoting> sourcesVotings) {
            this.sourcesVotings = sourcesVotings;
        }

        @SerializedName("list")
        private List<SourcesVoting> sourcesVotings;
    }
}
