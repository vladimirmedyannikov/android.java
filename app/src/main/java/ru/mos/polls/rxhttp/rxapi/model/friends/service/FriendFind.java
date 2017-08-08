package ru.mos.polls.rxhttp.rxapi.model.friends.service;

import java.util.ArrayList;
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

        public Request(String phone) {
            phones = new ArrayList<>();
            phones.add(phone);
        }

        public Request(List<String> phones) {
            this.phones = phones;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            private List<Friend> add;
            private List<String> fail;

            public List<Friend> getAdd() {
                return add;
            }

            public List<String> getFail() {
                return fail;
            }

            public boolean hasInAdded(String phone) {
                return add != null && find(add, phone) != null;
            }

            public boolean hasInFailed(String phone) {
                return fail.contains(phone);
            }

            public Friend find(List<Friend> list, String phone) {
                Friend result = null;
                for (Friend friend : list) {
                    if (friend.getPhone().equalsIgnoreCase(phone)) {
                        result = friend;
                        break;
                    }
                }
                return result;
            }
        }
    }
}
