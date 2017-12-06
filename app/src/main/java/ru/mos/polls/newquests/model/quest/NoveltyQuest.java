package ru.mos.polls.newquests.model.quest;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.ButterKnife;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;


public class NoveltyQuest extends DetailsQuest {
    public static final String TYPE = "novelty";

    public NoveltyQuest(long innerId, JSONObject jsonObject) {
        super(innerId, jsonObject);
    }

    @Override
    public View inflate(Context context, View convertView) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.quest_novelty, null);
        }

        TextView titleTextView =  ButterKnife.findById(convertView, R.id.title);
        TextView priceTextView = ButterKnife.findById(convertView, R.id.points);
        TextView priceTitleTextView = ButterKnife.findById(convertView, R.id.points_title);
        ImageView markerSpecialImageView = ButterKnife.findById(convertView, R.id.markerSpecial);

        String title = getTitle();
        titleTextView.setText(title);
        String pointsString = processPoints(getPoints());
        int points = getPoints();
        int visibility = View.GONE;
        if (points > 0) {
            String price = String.format(pointsString, points);
            priceTextView.setText(price);
            String pointsUnitStirng = PointsManager.getPointUnitString(context, points);
            priceTitleTextView.setText(pointsUnitStirng);
            visibility = View.VISIBLE;
        }
        priceTitleTextView.setVisibility(visibility);
        priceTextView.setVisibility(visibility);

        return convertView;
    }
}
