package ru.mos.polls.rxhttp.rxapi.model.friends.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 11:46.
 */

public class FriendMy {
    public static class Request extends AuthRequest {
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("list")
            private List<Friend> friends;

            public List<Friend> getFriends() {
                return friends;
            }
        }
    }
}
