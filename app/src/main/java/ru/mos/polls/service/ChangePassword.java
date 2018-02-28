package ru.mos.polls.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;

public class ChangePassword {
    public static class Request extends AuthRequest {
        @SerializedName("old_password")
        String oldPWD;
        @SerializedName("new_password")
        String newPWD;

        public Request(String oldPWD, String newPWD) {
            this.oldPWD = oldPWD;
            this.newPWD = newPWD;
        }
    }
}
