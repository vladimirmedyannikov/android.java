package ru.mos.polls.social.controller.service;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.social.model.AppPostItem;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.social.model.AppSocial;


public class LoadPostingData {
    public static class Request extends AuthRequest {
        public Request() {
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("social")
            private SocialsPostRX social;

            public SocialsPostRX getSocial() {
                return social;
            }
        }
        public static class SocialsPostRX {
            @SerializedName("vk")
            private PostDataRX vkData;
            @SerializedName("fb")
            private PostDataRX fbData;
            @SerializedName("twitter")
            private PostDataRX twitterData;
            @SerializedName("ok")
            private PostDataRX okData;

            /**
             * @param appContext applicationContext
             * @param type тип постинга
             * @param id айди
             * @return список айтемов для постинга
             */
            public List<AppPostItem> getPostItems(Context appContext, AppPostValue.Type type, Object id) {
                List<AppPostItem> appPostItems = new ArrayList<>();

                appPostItems.add(new AppPostItem(R.drawable.fb, R.drawable.fb, appContext.getString(R.string.fb),
                        new AppPostValue(AppSocial.NAME_FB, fbData.getAppPostValue(), type, id)));

                appPostItems.add(new AppPostItem(R.drawable.vk, R.drawable.vk, appContext.getString(R.string.vk),
                        new AppPostValue(AppSocial.NAME_VK, vkData.getAppPostValue(), type, id)));

                appPostItems.add(new AppPostItem(R.drawable.tw, R.drawable.tw, appContext.getString(R.string.tw),
                        new AppPostValue(AppSocial.NAME_TW, twitterData.getAppPostValue(), type, id)));

                appPostItems.add(new AppPostItem(R.drawable.ok, R.drawable.ok, appContext.getString(R.string.ok),
                        new AppPostValue(AppSocial.NAME_OK, okData.getAppPostValue(), type, id)));

                return appPostItems;
            }
        }
        private static class PostDataRX {
            @SerializedName("text")
            String text;
            @SerializedName("url")
            String url;
            @SerializedName("enable")
            boolean enable;
            @SerializedName("expired_time")
            long expiredTime;
            public AppPostValue getAppPostValue() {
                return new AppPostValue().setEnable(enable).setLink(url).setText(text);
            }
        }
    }
}