package ru.mos.polls.innovations.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.innovations.model.Rating;
import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class NoveltyFill {

    public static class Request extends AuthRequest {
        @SerializedName("novelty_id")
        private long noveltyId;
        @SerializedName("user_rating")
        private int userRating;

        public Request setNoveltyId(long noveltyId) {
            this.noveltyId = noveltyId;
            return this;
        }

        public Request setUserRating(int userRating) {
            this.userRating = userRating;
            return this;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {

        public static class Result {
            Rating rating;
            QuestMessage message;
            @SerializedName("status")
            Status status;
            @SerializedName("added_points")
            int addedPoints;

            public QuestMessage getMessage() {
                return message;
            }

            public Status getStatus() {
                return status;
            }

            public Rating getRating() {
                return rating;
            }

            public int getAddedPoints() {
                return addedPoints;
            }
        }

    }

}
