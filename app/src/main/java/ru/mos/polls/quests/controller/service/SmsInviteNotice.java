package ru.mos.polls.quests.controller.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 27.02.18.
 */

public class SmsInviteNotice {
    public static class Request extends AuthRequest {
        @SerializedName("phones")
        private List<String> phones;

        public Request(List<String> phones) {
            this.phones = phones;
        }
    }

    public static class Response extends GeneralResponse<SmsInviteNotice.Response.Result> {
        public static class Result {
            @SerializedName("status")
            private Status status;

            public Status getStatus() {
                return status;
            }
        }
    }
}