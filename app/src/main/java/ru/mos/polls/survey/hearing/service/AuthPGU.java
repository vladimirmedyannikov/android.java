package ru.mos.polls.survey.hearing.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Trunks on 19.02.2018.
 */

public class AuthPGU {
    public static class Request {
        @SerializedName("auth")
        Auth auth;
        @SerializedName("current_session")
        String currentSession;

        public Request(Auth auth, String currentSession) {
            this.auth = auth;
            this.currentSession = currentSession;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {

        public static class Result {
            @SerializedName("percent_fill_profile")
            int percentFillProfile;
            @SerializedName("message")
            QuestMessage message;

            public int getPercentFillProfile() {
                return percentFillProfile;
            }

            public QuestMessage getMessage() {
                return message;
            }

        }
    }

    public static class Auth {
        @SerializedName("sudir_login")
        String sudirLogin;
        String password;
        @SerializedName("session_id")
        String sessionId;

        public Auth(String sudirLogin, String password, String sessionId) {
            this.sudirLogin = sudirLogin;
            this.password = password;
            this.sessionId = sessionId;
        }
    }
}
