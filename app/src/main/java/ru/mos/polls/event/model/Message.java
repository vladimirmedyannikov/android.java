package ru.mos.polls.event.model;

import com.google.gson.annotations.SerializedName;


public class Message {
    private String title;
    private String body;
    @SerializedName("url_schemes")
    private UrlSchemes urlSchemes;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getUrlSchemes() {
        return urlSchemes.getAndroid();
    }

    private static class UrlSchemes {
        private String ios;
        private String android;
        private String wp;

        public String getAndroid() {
            return android;
        }
    }
}
