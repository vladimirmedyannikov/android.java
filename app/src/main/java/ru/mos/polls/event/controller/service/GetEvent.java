package ru.mos.polls.event.controller.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.common.model.Position;
import ru.mos.polls.event.model.EventRX;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 16.02.18.
 */

public class GetEvent {
    public static class Request extends AuthRequest {
        @SerializedName("event_id")
        private long eventId;
        @SerializedName("my_lat")
        private double myLat;
        @SerializedName("my_lng")
        private double myLng;


        public Request(long eventId, Position currentPosition) {
            this.eventId = eventId;
            if (currentPosition == null) {
                currentPosition = new Position();
            }
            this.myLat = currentPosition.getLat();
            this.myLng = currentPosition.getLon();
        }
    }

    public static class Response extends GeneralResponse<EventRX> {

    }
}