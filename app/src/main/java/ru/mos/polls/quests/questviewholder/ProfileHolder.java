package ru.mos.polls.quests.questviewholder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.quests.quest.BackQuest;

public class ProfileHolder extends PriceQuestHolder {
    TextView detailsTextView;

    public ProfileHolder(View itemView) {
        super(itemView);
        detailsTextView = ButterKnife.findById(itemView, R.id.details);
    }

    @Override
    public void setDataOnView(BackQuest quest) {
        super.setDataOnView(quest);
        detailsTextView.setText(quest.getDetails());
    }
}
