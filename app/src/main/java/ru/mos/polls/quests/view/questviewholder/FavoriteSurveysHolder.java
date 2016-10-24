package ru.mos.polls.quests.view.questviewholder;


import android.view.View;

import ru.mos.polls.R;
import ru.mos.polls.quests.quest.BackQuest;

public class FavoriteSurveysHolder extends PriceQuestHolder {
    public static final String ID_HEARING = "hearing";

    public FavoriteSurveysHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(BackQuest quest) {
        super.setDataOnView(quest);
        if (ID_HEARING.equalsIgnoreCase(quest.getType())) {
            questTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hearing, 0, 0, 0);
        }
    }
}
