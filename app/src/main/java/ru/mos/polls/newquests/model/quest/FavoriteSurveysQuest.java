package ru.mos.polls.newquests.model.quest;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.ButterKnife;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.quests.QuestsFragment;

public class FavoriteSurveysQuest extends BackQuest {

    public static final String ID_POLL = "poll";
    public static final String ID_HEARING = "hearing";
    public static final String KIND = "kind";
    private final Kind kind;

    public FavoriteSurveysQuest(long innerId,JSONObject jsonObject){
        super(innerId, jsonObject);
        kind = Kind.parse(jsonObject.optString(KIND));
    }

    public long getSurveyId() {
        return Long.parseLong(getId());
    }

    @Override
    public View inflate(Context context, View convertView) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.quest_favorite_survey, null);
        }

        TextView titleTextView = ButterKnife.findById(convertView, R.id.title);
        ImageView markerSpecialImageView = ButterKnife.findById(convertView, R.id.markerSpecial);
        TextView pointsTextView = ButterKnife.findById(convertView, R.id.points);
        TextView pointsTitleTextView = ButterKnife.findById(convertView, R.id.points_title);
        String title = getTitle();
        titleTextView.setText(title);
        String pointsString = processPoints(getPoints());
        int points = getPoints();
        int visibility = View.GONE;
        if (points > 0) {
            String price = String.format(pointsString, points);
            pointsTextView.setText(price);
            String pointsUnitStirng = PointsManager.getPointUnitString(context, points);
            pointsTitleTextView.setText(pointsUnitStirng);
            visibility = View.VISIBLE;
        }
        pointsTitleTextView.setVisibility(visibility);
        pointsTextView.setVisibility(visibility);


        if (ID_HEARING.equalsIgnoreCase(getType())) {
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hearing, 0, 0, 0);
        }

        return convertView;
    }

    private void onClick(QuestsFragment.Listener listener) {
        listener.onSurvey(getSurveyId());
    }

    @Override
    public String toString() {
        return "FavoriteQuest " + getId() + " " + getTitle();
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isHearingPreview() {
        return kind != null && kind.isHearingPreview();
    }

    public boolean isHearing() {
        return kind != null && kind.isHearing();
    }
}
