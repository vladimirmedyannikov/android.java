package ru.mos.polls.poll.service;

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
        @SerializedName("sources")
        List<String> sources;

        public Request(Page page, List<Source> list) {
            super(page);
            if (list.size() > 0) {
                this.sources = new ArrayList<>();
                for (Source source : list) {
                    sources.add(source.toString());
                }
            }
        }

        public Request(Page page, List<String> filters, Source... sources) {
            super(page);
            this.filters = filters;
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

    public enum Source {
        OSS("oss");
        public String title;

        Source(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
