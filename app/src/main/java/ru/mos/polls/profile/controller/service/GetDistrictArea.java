package ru.mos.polls.profile.controller.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.profile.model.DistrictArea;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Trunks on 16.02.2018.
 */

public class GetDistrictArea {
    public static class Request extends AuthRequest {
        @SerializedName("area_id")
        String areaNumber;

        public Request(String areaNumber) {
            this.areaNumber = areaNumber;
        }
    }

    public static class Response extends GeneralResponse<GetDistrictArea.Response.Result> {
        public static class Result {
            @SerializedName("district_label")
            public String district;
            @SerializedName("area_label")
            public String area;

            public DistrictArea getDistrictArea() {
                return new DistrictArea(district, area);
            }
        }
    }
}
