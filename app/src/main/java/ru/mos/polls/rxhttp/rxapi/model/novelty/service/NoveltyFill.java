package ru.mos.polls.rxhttp.rxapi.model.novelty.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.model.novelty.Rating;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 02.06.17 10:21.
 */

public class NoveltyFill {

    public static class Request extends AuthRequest {
        @SerializedName("novelty_id")
        private int noveltyId;
        @SerializedName("user_rating")
        private int userRating;

        public Request setNoveltyId(int noveltyId) {
            this.noveltyId = noveltyId;
            return this;
        }

        public Request setUserRating(int userRating) {
            this.userRating = userRating;
            return this;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {

        public  static class Result {
            private Rating rating;
//            @SerializedName("status")
//            private Balance balance;
            @SerializedName("added_points")
            private int addedPoints;

            public Rating getRating() {
                return rating;
            }

//            public Balance getBalance() {
//                return balance;
//            }

            public int getAddedPoints() {
                return addedPoints;
            }
        }

    }

}
