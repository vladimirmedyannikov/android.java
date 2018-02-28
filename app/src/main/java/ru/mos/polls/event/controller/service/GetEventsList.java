package ru.mos.polls.event.controller.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.common.model.PageInfo;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.event.model.EventFromList;
import ru.mos.polls.event.model.Filter;
import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class GetEventsList {
    public static class Request extends AuthRequest {
        @SerializedName("filter")
        private String filter;
        @SerializedName("my_lat")
        private double myLat;
        @SerializedName("my_lng")
        private double myLng;
        @SerializedName("count_per_page")
        private int countPerPage;
        @SerializedName("page_number")
        private int pageNumber;


        public Request(Position currentPosition, final Filter filter, final PageInfo pageInfo) {
            if (filter != null) {
                this.filter = filter.toString();
            }
            if (currentPosition == null) {
                currentPosition = new Position();
            }
            this.myLat = currentPosition.getLat();
            this.myLng = currentPosition.getLon();
            if (pageInfo != null) {
                this.countPerPage = pageInfo.getCountPerPage();
                this.pageNumber = pageInfo.getPageNumber();
            }
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("events")
            private List<EventFromList> listEvent;
            @SerializedName("status")
            private Status status;

            public List<EventFromList> getListEvent() {
                return listEvent;
            }

            public Status getStatus() {
                return status;
            }
        }
    }
}