package ru.mos.polls.newprofile.service;


import ru.mos.polls.newprofile.service.model.Personal;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by wlTrunks on 14.07.2017.
 */

public class ProfileSet extends AuthRequest {

    public static class Request extends AuthRequest {

        Personal personal;

        public Request(Personal personal) {
            this.personal = personal;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
        }
    }
}
