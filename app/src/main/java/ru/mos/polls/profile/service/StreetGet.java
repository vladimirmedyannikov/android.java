package ru.mos.polls.profile.service;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.profile.model.flat.Value;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;

public class StreetGet {
    public static class Request extends AuthRequest {
        private int limit;
        private String term;

        public Request(int limit, String term) {
            this.limit = limit;
            this.term = term;
        }
    }

    public static class Response {

        /**
         * result : [{"value":"2408","label":"Краснодонская ул."}]
         * execTime : 0.073180198669434
         * request_id : token=ag_test_token&client_req_id=51814876-797a-40cd-87d4-61f4fb433d41
         * session_id : a575a6176eabedc0ed7a5e339d559494
         */

        @SerializedName("result")
        private List<Value> resultX;

        public List<Value> getResultX() {
            return resultX;
        }

        public void setResultX(List<Value> resultX) {
            this.resultX = resultX;
        }

    }
}
