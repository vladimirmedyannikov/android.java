package ru.mos.polls.quests.quest;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.ButterKnife;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.quests.QuestsFragment;

public class ProfileQuest extends DetailsQuest {
    public static final String TYPE_PROFILE = "profile";
    public static final String ID_UPDATE_PERSONAL = "updatePersonal";
    public static final String ID_UPDATE_LOCATION = "updateLocation";
    public static final String ID_UPDATE_SOCIAL = "updateSocial";
    public static final String ID_UPDATE_EMAIL = "updateEmail";
    public static final String ID_UPDATE_EXTRA_INFO = "updateExtraInfo";
    public static final String ID_UPDATE_FAMILY_INFO = "updateFamilyInfo";
    public static final String ID_BIND_TO_PGU = "bindToPGU";

    public ProfileQuest(long innderId,JSONObject jsonObject){
        super(innderId, jsonObject);
    }
    @Override
    public View inflate(Context context, View convertView) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.quest_profile, null);
        }
        TextView titleTextView = ButterKnife.findById(convertView, R.id.title);
        TextView detailsTextView = ButterKnife.findById(convertView, R.id.details);
        TextView pointsTextView = ButterKnife.findById(convertView, R.id.points);
        TextView pointsTitleTextView = ButterKnife.findById(convertView, R.id.points_title);

        titleTextView.setText(getTitle());
        detailsTextView.setText(getDetails());
        pointsTextView.setText(String.format(processPoints(getPoints()), getPoints()));
        pointsTitleTextView.setText(PointsManager.getPointUnitString(context, getPoints()));

        return convertView;
    }

    @Override
    public void onClick(Context context, QuestsFragment.Listener listener) {
        if (ID_UPDATE_PERSONAL.equals(getId())) {
            listener.onUpdatePersonal();
        } else if (ID_UPDATE_LOCATION.equals(getId())) {
            listener.onUpdateLocation();
        } else if (ID_UPDATE_SOCIAL.equals(getId())) {
            listener.onUpdateSocial();
        } else if (ID_UPDATE_EMAIL.equals(getId())) {
            listener.onUpdateEmail();
        } else if (ID_UPDATE_EXTRA_INFO.equals(getId())) {
            listener.onUpdateExtraInfo();
        } else if (ID_UPDATE_FAMILY_INFO.equals(getId())) {
            listener.onUpdateFamilyInfo();
        } else if (ID_BIND_TO_PGU.equals(getId())) {
            listener.onBindToPgu();
        }
    }
}
