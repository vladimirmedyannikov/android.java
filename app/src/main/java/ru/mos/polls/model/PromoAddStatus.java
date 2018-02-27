package ru.mos.polls.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 27.02.18.
 */

public class PromoAddStatus {
    @SerializedName("added_points")
    private int addedPoints;
    @SerializedName("current_points")
    private int currentPoints;

    public int getAddedPoints() {
        return addedPoints;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }
}
