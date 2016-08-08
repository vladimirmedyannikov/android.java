package ru.mos.polls.innovation.model;

import org.json.JSONObject;

/**
 * Полное описание городской новинки
 * Расширяет {@link ru.mos.polls.innovation.model.ShortInnovation}
 *
 * @since 1.9
 */
public class Innovation extends ShortInnovation {
    private String textShortHtml;
    private String textFullHtml;
    private Rating rating;

    public Innovation(JSONObject json) {
        super(json);
        textFullHtml = json.optString("text_full_html");
        textShortHtml = json.optString("text_short_html");
        rating = new Rating(json.optJSONObject("rating"));
    }

    public String getTextShortHtml() {
        return textShortHtml;
    }

    public void setTextShortHtml(String textShortHtml) {
        this.textShortHtml = textShortHtml;
    }

    public String getTextFullHtml() {
        return textFullHtml;
    }

    public void setTextFullHtml(String textFullHtml) {
        this.textFullHtml = textFullHtml;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
}
