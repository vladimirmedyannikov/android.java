package ru.mos.polls.quests.view.questviewholder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.quests.quest.BackQuest;

public class SocialHolder extends PriceQuestHolder {
    @BindView(R.id.details)
    public TextView detailsTextView;
    @BindView(R.id.icon)
    public ImageView iconImageView;

    public SocialHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(BackQuest quest) {
        super.setDataOnView(quest);
        iconImageView.setImageResource(quest.icon);
        detailsTextView.setText(quest.getDetails());
    }
}
