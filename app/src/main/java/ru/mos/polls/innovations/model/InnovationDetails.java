package ru.mos.polls.innovations.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 31.05.17 11:03.
 */

public class InnovationDetails extends Innovation {
    @SerializedName("text_full_html")
    private String fullHtml;
    @SerializedName("text_short_html")
    private String shortHtml;
    private Rating rating;
    private long passedDate;

    public String getFullHtml() {
        return fullHtml;
    }

    public String getShortHtml() {
        return shortHtml;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public void setPassedDate(long passedDate) {
        this.passedDate = passedDate;
    }
}
