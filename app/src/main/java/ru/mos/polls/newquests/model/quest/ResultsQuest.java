package ru.mos.polls.newquests.model.quest;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.ButterKnife;
import ru.mos.polls.R;

public class ResultsQuest extends BackQuest {
    public static final String TYPE = "results";
    private static final String LINK_URL = "link_url";
    private String linkUrl;

    public ResultsQuest(long innerId, JSONObject jsonObject) {
        super(innerId, jsonObject);
        linkUrl = jsonObject.optString(LINK_URL);
    }
    @Override
    public View inflate(Context context, View convertView) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.quest_results, null);
        }
        TextView titleTextView = ButterKnife.findById(convertView, R.id.title);
        String title = getTitle();
        titleTextView.setText(title);

        return convertView;
    }
}