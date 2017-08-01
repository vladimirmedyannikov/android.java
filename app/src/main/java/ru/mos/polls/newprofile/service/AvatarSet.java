package ru.mos.polls.newprofile.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.newprofile.service.model.EmptyResult;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Trunks on 13.07.2017.
 */

public class AvatarSet {
    public static class Request extends AuthRequest {
        @SerializedName("mediaId")
        private String mediaId;

        public Request(String mediaId) {
            this.mediaId = mediaId;
        }
    }
}
