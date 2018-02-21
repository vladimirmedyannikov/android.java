package ru.mos.polls.social.controller.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.social.model.AppSocial;
import ru.mos.polls.social.model.Message;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 20.02.18.
 */

public class ProfileUpdateSocial {
    public static class Request extends AuthRequest {
        @SerializedName("social")
        private SocialsRX social;

        public Request(AppSocial appSocial, boolean forBind) {
            social = new SocialsRX();
            SocialTokenRX socialTokenRX = forBind ?
                    new SocialTokenRX()
                            .setToken1(appSocial.getToken().getAccess())
                            .setToken2(appSocial.getToken().getRefresh())
                    :
                    new SocialTokenRX().setKill(true);
            switch (appSocial.getId()) {
                case AppSocial.ID_FB:
                    social.setFb(socialTokenRX);
                    break;
                case AppSocial.ID_VK:
                    social.setVk(socialTokenRX);
                    break;
                case AppSocial.ID_TW:
                    social.setTwitter(socialTokenRX);
                    break;
                case AppSocial.ID_OK:
                    social.setOk(socialTokenRX);
                    break;
            }
        }

        static class SocialsRX {
            @SerializedName("fb")
            private SocialTokenRX fb;
            @SerializedName("vk")
            private SocialTokenRX vk;
            @SerializedName("twitter")
            private SocialTokenRX twitter;
            @SerializedName("ok")
            private SocialTokenRX ok;

            public void setFb(SocialTokenRX fb) {
                this.fb = fb;
            }

            public void setVk(SocialTokenRX vk) {
                this.vk = vk;
            }

            public void setTwitter(SocialTokenRX twitter) {
                this.twitter = twitter;
            }

            public void setOk(SocialTokenRX ok) {
                this.ok = ok;
            }
        }

        static class SocialTokenRX {
            @SerializedName("token1")
            private String token1;
            @SerializedName("token2")
            private String token2;
            @SerializedName("expired_time")
            private Long expiredTime;
            @SerializedName("kill")
            private Boolean kill;

            public SocialTokenRX setToken1(String token1) {
                this.token1 = token1;
                return this;
            }

            public SocialTokenRX setToken2(String token2) {
                this.token2 = token2;
                return this;
            }

            public SocialTokenRX setExpiredTime(long expiredTime) {
                this.expiredTime = expiredTime;
                return this;
            }

            public SocialTokenRX setKill(boolean kill) {
                this.kill = kill;
                return this;
            }
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("status")
            private Status status;
            @SerializedName("message")
            private Message message;
            @SerializedName("percent_fill_profile")
            private int percentFillProfile;

            public Status getStatus() {
                return status;
            }

            public Message getMessage() {
                return message;
            }

            public int getPercentFillProfile() {
                return percentFillProfile;
            }
        }
    }
}