package ru.mos.polls.social.controller.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.social.model.AppSocial;
import ru.mos.social.model.Token;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 20.02.18.
 */

public class GetSocialProfile {
    public static class Request extends AuthRequest {
        public Request() {
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("fb")
            private SocialRX fb;
            @SerializedName("vk")
            private SocialRX vk;
            @SerializedName("twitter")
            private SocialRX twitter;
            @SerializedName("ok")
            private SocialRX ok;

            public AppSocial getFb() {
                return fb != null ? fb.getAppSocial(AppSocial.NAME_FB) : new AppSocial(AppSocial.NAME_FB, null);
            }
            public AppSocial getVk() {
                return vk != null ? vk.getAppSocial(AppSocial.NAME_VK) : new AppSocial(AppSocial.NAME_VK, null);
            }
            public AppSocial getTwitter() {
                return twitter != null ? twitter.getAppSocial(AppSocial.NAME_TW) : new AppSocial(AppSocial.NAME_TW, null);
            }
            public AppSocial getOk() {
                return ok != null ? ok.getAppSocial(AppSocial.NAME_OK) : new AppSocial(AppSocial.NAME_OK, null);
            }
        }

        public static class SocialRX {
            @SerializedName("token1")
            private String token1;
            @SerializedName("token2")
            private String token2;
            @SerializedName("icon")
            private String icon;
            @SerializedName("expired_time")
            private long expiredTime;
            @SerializedName("logined")
            private boolean logined;
            @SerializedName("errorCode")
            private int errorCode;
            @SerializedName("errorMessage")
            private String errorMessage;

            public AppSocial getAppSocial(String socialName) {
                int id = AppSocial.getId(socialName);
                AppSocial res = new AppSocial(id, socialName, 0, new Token(token1,
                        token2,
                        id == AppSocial.ID_FB ?
                                /**
                                 * Т.к. сс возвращает оставшееся время, а не абсолютное
                                 */
                                System.currentTimeMillis() + expiredTime * 1000 :
                                expiredTime * 1000));
                res.setIcon(icon);
                res.setIsLogin(logined);
                return res;
            }
        }
    }
}