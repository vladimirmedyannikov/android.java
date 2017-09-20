package ru.mos.polls.rxhttp.rxapi.model.support.service;

import java.util.List;

import ru.mos.elk.api.Token;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.support.Subject;

/**
 * Created by matek3022 on 15.09.17.
 */

public class SubjectsLoad {
    public static class Request {
        String token;
        public Request() {
            token = Token.AG.getToken(true);
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            private List<Subject> subjects;

            public List<Subject> getSubjects() {
                return subjects;
            }
        }
    }
}
