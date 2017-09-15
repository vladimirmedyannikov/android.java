package ru.mos.polls.newpoll.service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.base.PageAuthRequest;

/**
 * Created by Trunks on 15.09.2017.
 */

public class PollSelect {
    public static class Request extends PageAuthRequest {
        @SerializedName("kind_filters")
        List<String> kindFilters;
        @SerializedName("filters")
        List<String> filters;

        public Request(Page page, List<String> filters) {
            super(page);
            this.filters = filters;
            kindFilters = new ArrayList<>();
            kindFilters.add(Kind.STANDART.getKind());
            kindFilters.add(Kind.SPECIAL.getKind());
            kindFilters.add(Kind.HEARING.getKind());
        }


    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("polls")
            private List<Poll> polls;

            public List<Poll> getPolls() {
                return polls;
            }
        }
    }
}
