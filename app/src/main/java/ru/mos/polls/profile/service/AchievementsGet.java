package ru.mos.polls.profile.service;


import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Trunks on 13.07.2017.
 */

public class AchievementsGet {
    public static class Request extends AuthRequest {
        private int id;
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
        }
    }
}
