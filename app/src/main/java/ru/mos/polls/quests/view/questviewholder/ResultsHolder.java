package ru.mos.polls.quests.view.questviewholder;

import android.view.View;

import ru.mos.polls.quests.quest.BackQuest;


public class ResultsHolder extends QuestsViewHolder {
    public ResultsHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(BackQuest quest) {
        questTitle.setText(quest.getTitle());
    }
}
