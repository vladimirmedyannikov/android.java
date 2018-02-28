package ru.mos.polls.innovations.model;

import com.google.gson.annotations.SerializedName;



public class InnovationDetails extends Innovation {
    @SerializedName("text_full_html")
    private String fullHtml;
    @SerializedName("text_short_html")
    private String shortHtml;
    private Rating rating;


    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getTextShortHtml() {
        return shortHtml;
    }

    public String getTextFullHtml() {
        return fullHtml;
    }
}
