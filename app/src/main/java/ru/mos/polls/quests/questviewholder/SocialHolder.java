package ru.mos.polls.quests.questviewholder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.quests.quest.BackQuest;

public class SocialHolder extends PriceQuestHolder {
    TextView detailsTextView;
    ImageView iconImageView;

    public SocialHolder(View itemView) {
        super(itemView);
        detailsTextView = ButterKnife.findById(itemView, R.id.details);
        iconImageView = ButterKnife.findById(itemView, R.id.icon);
    }

    @Override
    public void setDataOnView(BackQuest quest) {
        super.setDataOnView(quest);
        iconImageView.setImageResource(quest.icon);
    }
}
