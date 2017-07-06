package ru.mos.polls.rxhttp.rxapi.model.friends.service;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 10:53.
 */

public class FriendFind {
    public static class Request extends AuthRequest {
        private List<String> phones;

        Request(List<String> phones) {
            this.phones = phones;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            private List<Friend> add;
            private List<String> fail;
        }
    }
}
