package ru.mos.polls.profile.service;


import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;

/**
 * Created by Trunks on 13.07.2017.
 */

public class VisibilitySet {
    public static class Request extends AuthRequest {
        private boolean state;

        public Request(boolean state) {
            this.state = state;
        }
    }
}
