package ru.mos.polls.quests.questviewholder;


import android.view.View;

import ru.mos.polls.quests.quest.BackQuest;

public class OtherHolder extends QuestsViewHolder {
    public OtherHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(BackQuest quest) {
        questTitle.setText(quest.getTitle());
        urlScheme = quest.getUrlScheme();
    }
}
