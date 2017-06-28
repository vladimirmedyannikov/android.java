package ru.mos.polls.newinnovation.response;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.newinnovation.Rating;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 02.06.17 10:21.
 */

public class ResultInnovationFill {
    private Rating rating;
//    @SerializedName("status")
//    private Balance balance;
    @SerializedName("added_points")
    private int addedPoints;

    public Rating getRating() {
        return rating;
    }

//    public Balance getBalance() {
//        return balance;
//    }

    public int getAddedPoints() {
        return addedPoints;
    }
}
