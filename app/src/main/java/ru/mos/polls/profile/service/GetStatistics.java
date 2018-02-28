package ru.mos.polls.profile.service;


import ru.mos.polls.friend.model.Statistic;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

public class GetStatistics {


    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            private Statistic statistics;

            public Statistic getStatistics() {
                return statistics;
            }
        }
    }
}
