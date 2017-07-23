package ru.mos.polls.rxhttp.rxapi.model.base;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.session.Session;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.06.17 12:31.
 */

public class AuthRequest {
    @SerializedName("auth")
    protected SessionId sessionId = new SessionId();

    public class SessionId {
        @SerializedName("session_id")
        private String sessionId;

        public SessionId() {
            sessionId = Session.get().getSession();
        }

        public String getSessionId() {
            return sessionId;
        }
    }


}
