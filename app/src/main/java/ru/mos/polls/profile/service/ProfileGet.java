package ru.mos.polls.profile.service;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Trunks on 26.02.2018.
 */

public class ProfileGet {

    public static class LoginRequest {

        private Auth auth;
        @SerializedName("device_info")
        private DeviceInfo deviceInfo;

        public Auth getAuth() {
            return auth;
        }

        public void setAuth(Auth auth) {
            this.auth = auth;
        }

        public void setDeviceInfo(DeviceInfo deviceInfo) {
            this.deviceInfo = deviceInfo;
        }

        public static class Auth {
            /**
             * login : 79637502152
             * password : 11111
             * guid : 5b451d2f-841d-4abd-a05f-0126c1d28708
             * sessionId : null
             */

            private String login;
            private String password;
            private String guid;
            private String sessionId;
            private Boolean logout;

            public void setLogin(String login) {
                this.login = login;
            }


            public void setPassword(String password) {
                this.password = password;
            }


            public void setGuid(String guid) {
                this.guid = guid;
            }

            public void setSessionId(String sessionId) {
                this.sessionId = sessionId;
            }

            public void setLogout(Boolean logout) {
                this.logout = logout;
            }
        }
    }

    public static class RecoveryRequest {

        /**
         * msisdn : 79637502152
         * deviceInfo : {"os":"Android 7.1.1 (SDK 25)","device":"Android SDK built for x86 (Google)"}
         */

        private String msisdn;
        @SerializedName("client_info")
        private ClientInfoBean clientInfo;
        @SerializedName("device_info")
        private DeviceInfo deviceInfo;


        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public void setClientInfo(ClientInfoBean clientInfo) {
            this.clientInfo = clientInfo;
        }

        public void setDeviceInfo(DeviceInfo deviceInfo) {
            this.deviceInfo = deviceInfo;
        }
    }

    public static class ClientInfoBean {
        public ClientInfoBean(String os, String device) {
            this.os = os;
            this.device = device;
        }

        /**
         * os : Android 7.1.1 (SDK 25)
         * device : Android SDK built for x86 (Google)
         */

        private String os;
        private String device;

        public void setOs(String os) {
            this.os = os;
        }

        public void setDevice(String device) {
            this.device = device;
        }
    }

    public static class DeviceInfo {
        @SerializedName("guid")
        String guid;
        @SerializedName("object_id")
        String objectId;
        @SerializedName("user_agent")
        final String userAgent = "Android";
        @SerializedName("app_version")
        String appVersion;

        public DeviceInfo(String guid, String objectId, String appVersion) {
            this.guid = guid;
            this.objectId = objectId;
            this.appVersion = appVersion;
        }
    }

    public static class Response extends GeneralResponse<JsonObject> {
    }
}
