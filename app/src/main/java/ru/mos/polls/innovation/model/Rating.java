package ru.mos.polls.innovation.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

import ru.mos.polls.R;

/**
 * Описание рейтинга новинки {@link ru.mos.polls.innovation.model.Innovation}
 *
 * @since 1.9
 */
public class Rating implements Serializable {
    private int userRating;
    private double fullRating;
    private int fullCount;
    private int[] counts;

    public Rating(JSONObject ratingJson) {
        if (ratingJson != null) {
            userRating = ratingJson.optInt("user_rating");
            fullRating = ratingJson.optDouble("full_rating");
            fullCount = ratingJson.optInt("full_count");
            counts = getCounts(ratingJson.optJSONArray("counts"));
        }
    }

    private int[] getCounts(JSONArray jsonArray) {
        int[] result = null;
        String raiting;
        if (jsonArray != null) {
            result = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); ++i) {
                raiting = jsonArray.optString(i);
                if ("null".equals(raiting)) {
                    raiting = "0";
                }
                result[i] = Integer.parseInt(raiting);
            }
        }
        return result;
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
        return String.format(context.getString(R.string.formatted_full_count), fullCount);
    }
}
