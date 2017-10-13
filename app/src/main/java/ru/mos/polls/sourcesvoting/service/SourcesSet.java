package ru.mos.polls.sourcesvoting.service;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.sourcesvoting.model.SourcesVotingSet;


public class SourcesSet {
    public static class Request extends AuthRequest {
        public Request(List<SourcesVotingSet> sourcesVoting) {
            this.sourcesVoting = sourcesVoting;
        }

        @SerializedName("list")
        private List<SourcesVotingSet> sourcesVoting;
    }
}
