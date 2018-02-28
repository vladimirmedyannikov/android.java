package ru.mos.polls.friend.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.friend.model.Friend;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


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
