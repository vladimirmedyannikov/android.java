package ru.mos.polls.mainbanner.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class GetBannerStatistics {

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("users_count")
            public long usersCount;
            @SerializedName("polls_count")
            public long pollsCount;
            @SerializedName("active_territorial_polls_count")
            public long activePollsCount;
            @SerializedName("active_polls_for_all_count")
            public long activeAllPollsCount;
            @SerializedName("passed_polls_count")
            public long passedPollsCount;
            @SerializedName("novelty_count")
            public long noveltyCount;

            public long getUsersCount() {
                return usersCount;
            }

            public long getPollsCount() {
                return pollsCount;
            }

            public long getActivePollsCount() {
                return activePollsCount;
            }

            public long getActiveAllPollsCount() {
                return activeAllPollsCount;
            }

            public long getPassedPollsCount() {
                return passedPollsCount;
            }

            public long getNoveltyCount() {
                return noveltyCount;
            }
        }
    }
}
