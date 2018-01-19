package ru.mos.polls.mainbanner.service;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Trunks on 19.01.2018.
 */

public class GetBannerStatistics {

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            List<Long> list;

            public List<Long> getList() {
                return list;
            }
        }
    }
}
