package ru.mos.polls.support.service;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.config.AgApiBuilder;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.support.model.Subject;


public class SubjectsLoad {
    public static class Request {
        String token;
        public Request() {
            token = AgApiBuilder.token().get();
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
