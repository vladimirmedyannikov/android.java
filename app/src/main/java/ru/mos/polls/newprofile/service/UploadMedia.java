package ru.mos.polls.newprofile.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Trunks on 13.07.2017.
 */

public class UploadMedia {
    public static class Request extends AuthRequest {
        @SerializedName("base64")
        private String base64;
        @SerializedName("extension")
        private String extension;


        public Request setBase64(String base64) {
            this.base64 = base64;
            return this;
        }

        public Request setExtension(String extension) {
            this.extension = extension;
            return this;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            private String id;
            private String url;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
