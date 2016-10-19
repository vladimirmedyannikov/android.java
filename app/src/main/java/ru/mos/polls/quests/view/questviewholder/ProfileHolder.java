package ru.mos.polls.quests.view.questviewholder;


import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.quests.quest.BackQuest;

public class ProfileHolder extends PriceQuestHolder {
    @BindView(R.id.details)
    TextView detailsTextView;

    public ProfileHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(BackQuest quest) {
        super.setDataOnView(quest);
        detailsTextView.setText(quest.getDetails());
    }
}
