package ru.mos.polls.newinnovation;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 31.05.17 11:11.
 */

public class Rating implements Serializable {
    @SerializedName("user_rating")
    private int userRating;
    @SerializedName("full_rating")
    private double fullRating;
    @SerializedName("full_count")
    private int fullCount;
    @SerializedName("counts")
    private int[] counts;

    public int getUserRating() {
        return userRating;
    }

    public double getFullRating() {
        return fullRating;
    }

    public int getFullCount() {
        return fullCount;
    }

    public int[] getCounts() {
        return counts;
    }

    public String getFormattedFullCount(Context context) {
        return String.format("Оценок: %s", fullCount);
    }

    public int getMaxCount() {
        int result = counts[0];
        for (int i = 1; i < counts.length; ++i) {
            if (result < counts[i]) {
                result = counts[i];
            }
        }
        return result;
    }
}
