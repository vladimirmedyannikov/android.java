package ru.mos.polls.geotarget.service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.geotarget.model.Area;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class UserInArea {
    public static class Request extends AuthRequest {
        @SerializedName("ids")
        private List<Integer> ids;

        public Request(List<Area> areas) {
            ids = new ArrayList<>();
            if (areas != null && areas.size() > 0) {
                for (Area area : areas) {
                    ids.add(area.getId());
                }
            }
        }
    }

    public static class Response extends GeneralResponse<UserInArea.Response.Result> {
        public static class Result {
            @SerializedName("success")
            private boolean success;
            @SerializedName("disable_area_ids")
            private List<Integer> disableAreaIds;

            public boolean isSuccess() {
                return success;
            }

            public List<Integer> getDisableAreaIds() {
                return disableAreaIds;
            }
        }

    }
}