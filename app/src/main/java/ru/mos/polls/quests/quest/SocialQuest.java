package ru.mos.polls.quests.quest;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.ButterKnife;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.ElkTextUtils;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.quests.QuestsFragment;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.SocialPostValue;

public class SocialQuest extends DetailsQuest {

    public static final String TYPE_SOCIAL = "social";
    public static final String ID_POST_IN_SOCIAL = "postInSocial";
    public static final String ID_INVITE_FRIENDS = "inviteFriends";
    public static final String ID_RATE_THIS_APP = "rateThisApplication";
    private static final String ID = "id";
    private final int icon;

    public SocialQuest(long innderId, JSONObject jsonObject) {
        super(innderId, jsonObject);
        this.icon = getIcon(jsonObject);
    }

    private int getIcon(JSONObject jsonObject) {
        final String id = jsonObject.optString(ID);
        int iconId;
        if (ID_INVITE_FRIENDS.equals(id)) {
            iconId = R.drawable.image_icon_category_friends;
        } else if (ID_POST_IN_SOCIAL.equals(id)) {
            iconId = R.drawable.image_icon_category_social;
        } else if (ID_RATE_THIS_APP.equals(id)) {
            iconId = R.drawable.icon03;
        } else {
            iconId = R.drawable.image_icon_category_profile;
        }
        return iconId;
    }

    @Override
    public View inflate(Context context, View convertView) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.quest_social, null);
        }

        ImageView iconImageView = ButterKnife.findById(convertView, R.id.icon);
        TextView titleTextView = ButterKnife.findById(convertView, R.id.title);
        TextView detailsTextView = ButterKnife.findById(convertView, R.id.details);
        TextView pointsTextView = ButterKnife.findById(convertView, R.id.points);
        TextView pointsTitleTextView = ButterKnife.findById(convertView, R.id.points_title);

        iconImageView.setImageResource(icon);
        titleTextView.setText(getTitle());
        detailsTextView.setText(getDetails());
        pointsTextView.setText(processPoints(getPoints()));
        pointsTitleTextView.setText(PointsManager.getPointUnitString(context, getPoints()));
        if (getPoints() == 0) {
            pointsTextView.setVisibility(View.GONE);
            pointsTitleTextView.setVisibility(View.GONE);
        }
        if (ElkTextUtils.isEmpty(getDetails())) {
            detailsTextView.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public void onClick(final Context context, final QuestsFragment.Listener listener) {
        if (ID_POST_IN_SOCIAL.equals(getId())) {
            SocialUIController.showSocialsDialog((BaseActivity) context, new SocialUIController.SocialClickListener() {
                @Override
                public void onClick(Context context, Dialog dialog, SocialPostValue socialPostValue) {
                    listener.onSocialPost(socialPostValue);
                }

                @Override
                public void onCancel() {
                    ((BaseActivity) context).finish();
                }
            });
        } else if (ID_INVITE_FRIENDS.equals(getId())) {
            listener.onInviteFriends(true);
        }
    }
}
