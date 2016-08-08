package ru.mos.polls.offer;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONObject;

public class OfferProcessor {

    private Activity activity;
    private String url;
    private String title;

    public OfferProcessor(Activity activity) {
        this.activity = activity;
    }

    public boolean hasOffer(JSONObject jsonObject) {
        final boolean result;
        JSONObject offerJsonObject = jsonObject.optJSONObject("offer");
        if (offerJsonObject != null) {
            url = offerJsonObject.optString("link");
            title = offerJsonObject.optString("title", "");
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public void showOfferActivity() {
        Intent i = new Intent(activity, OfferActivity.class);
        i.putExtra(OfferActivity.EXTRA_URL, url);
        i.putExtra(OfferActivity.EXTRA_TITLE, title);
        activity.startActivity(i);
    }

}
