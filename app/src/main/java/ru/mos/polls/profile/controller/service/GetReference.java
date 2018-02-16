package ru.mos.polls.profile.controller.service;

import com.google.gson.annotations.SerializedName;


import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;

/**
 * Created by Trunks on 16.02.2018.
 */

public class GetReference {
    public static class Request extends AuthRequest {
        @SerializedName("district_id")
        String districtId;

        public Request() {
        }

        public Request(String districtId) {
            this.districtId = districtId;
        }
    }
}
