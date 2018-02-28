package ru.mos.polls.profile.service;


import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;

public class VisibilitySet {
    public static class Request extends AuthRequest {
        private boolean state;

        public Request(boolean state) {
            this.state = state;
        }
    }
}
