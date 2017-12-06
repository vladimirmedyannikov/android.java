package ru.mos.polls.newquests.model.quest;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.ButterKnife;
import ru.mos.elk.ElkTextUtils;
import ru.mos.polls.R;


public class AchievementQuest extends DetailsQuest {
    public static final String TYPE = "achievement";

    public AchievementQuest(long innerId, JSONObject jsonObject) {
        super(innerId, jsonObject);
    }

    @Override
    public View inflate(Context context, View convertView) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.quest_achievement, null);
        }

        TextView titleTextView = ButterKnife.findById(convertView, R.id.title);
        TextView detailsTextView = ButterKnife.findById(convertView, R.id.details);

        titleTextView.setText(getTitle());
        detailsTextView.setText(getDetails());
        if (ElkTextUtils.isEmpty(getDetails())) {
            detailsTextView.setVisibility(View.GONE);
        }

        return convertView;
    }
}
