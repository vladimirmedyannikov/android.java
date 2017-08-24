package ru.mos.polls.mypoints.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Trunks on 24.08.2017.
 */

public class Status {

    //    "points":{
//        "freezed_points":0,
//                "current_points":96,
//                "spent_points":0,
//                "all_points":96,
//                "state":null
//    }
    @SerializedName("freezed_points")
    private int freezedPoints;
    @SerializedName("current_points")
    private int currentPoints;
    @SerializedName("all_points")
    private long allPoints;
    @SerializedName("spent_points")
    private int spentPoints;
    @SerializedName("state")
    private String state;

    public int getFreezedPoints() {
        return freezedPoints;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public long getAllPoints() {
        return allPoints;
    }

    public int getSpentPoints() {
        return spentPoints;
    }

    public String getState() {
        return state;
    }
}
