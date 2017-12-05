package ru.mos.polls.quests.quest;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.ButterKnife;
import ru.mos.polls.R;


public class OssPollQuest extends DetailsQuest {
    public static final String TYPE = "poll_oss";

    public OssPollQuest(long innerId, JSONObject jsonObject) {
        super(innerId, jsonObject);
    }

    @Override
    public View inflate(Context context, View convertView) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.quest_oss_poll, null);
        }
        TextView titleTextView = ButterKnife.findById(convertView, R.id.title);
        titleTextView.setText(getTitle());
        return convertView;
    }
}
