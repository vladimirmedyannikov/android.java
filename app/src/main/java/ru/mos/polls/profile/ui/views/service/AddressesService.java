package ru.mos.polls.profile.ui.views.service;


import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;



public class AddressesService {
    public static class Request extends AuthRequest {
        public Request(String term, int limit, Object ul) {
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
        Object ul;
    }
}
