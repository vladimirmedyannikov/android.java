package ru.mos.polls.event.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 20.02.18.
 */

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
