package ru.mos.polls.newquests.model.quest;

import android.content.Context;

import ru.mos.polls.newquests.model.QuestFamilyList;
import ru.mos.polls.quests.QuestsFragment;

public abstract class Quest {

    private final long innerId;

    public Quest(long innerId, QuestFamilyList questFamilyList) {
        this.innerId = innerId;
    }

    public long getInnerId() {
        return innerId;
    }

    public abstract void onClick(Context context, QuestsFragment.Listener listener);
}
