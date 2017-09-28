package ru.mos.polls.newprofile.service;


import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.friends.Statistic;

/**
 * Created by Trunks on 26.09.2017.
 */

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