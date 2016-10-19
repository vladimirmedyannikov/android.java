package ru.mos.polls.quests.view.questviewholder;


import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.elk.ElkTextUtils;
import ru.mos.polls.R;
import ru.mos.polls.quests.quest.BackQuest;


public class AchievementHolder extends QuestsViewHolder {
    @BindView(R.id.details)
    public TextView detailsTextView;

    public AchievementHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(BackQuest quest) {
        questTitle.setText(quest.getTitle());
        detailsTextView.setText(quest.getDetails());
        if (ElkTextUtils.isEmpty(quest.getDetails())) {
            detailsTextView.setVisibility(View.GONE);
        }
    }
}
