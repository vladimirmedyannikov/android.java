package ru.mos.polls.geotarget.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.geotarget.model.Area;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 22.02.18.
 */

public class GetAreas {
    public static class Request extends AuthRequest {
        public Request() {
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("areas")
            private List<Area> areas;

            public List<Area> getAreas() {
                return areas;
            }
        }

    }
}