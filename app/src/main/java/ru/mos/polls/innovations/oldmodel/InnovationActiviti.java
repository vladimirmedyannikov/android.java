package ru.mos.polls.innovations.oldmodel;


import org.json.JSONObject;

import ru.mos.polls.innovations.model.ShortInnovation;

/**
 * Полное описание городской новинки
 * Расширяет {@link ShortInnovation}
 *
 * @since 1.9
 */
@Deprecated
public class InnovationActiviti extends ShortInnovation {
    private String textShortHtml;
    private String textFullHtml;
    private Rating rating;

    public InnovationActiviti(JSONObject json) {
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
