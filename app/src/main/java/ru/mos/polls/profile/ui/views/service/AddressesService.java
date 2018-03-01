package ru.mos.polls.profile.ui.views.service;

import java.util.List;

import ru.mos.polls.profile.model.flat.Value;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Trunks on 01.03.2018.
 */

public class AddressesService {
    public static class Request extends AuthRequest {
        public Request(String term, int limit, String ul) {
            this.term = term;
            this.limit = limit;
            this.ul = ul;
        }

        public Request(String term, int limit) {
            this.term = term;
            this.limit = limit;
        }

        String term;
        int limit;
        String ul;
    }

    public static class Response extends GeneralResponse<AddressesService.Response.Result> {
        public static class Result {
            List<Value> list;

            public List<Value> getList() {
                return list;
            }
        }
    }
}
