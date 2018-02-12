package ru.mos.polls.quests.model.quest;

import android.content.Context;

import java.io.Serializable;

import ru.mos.polls.quests.model.QuestFamilyElement;
import ru.mos.polls.quests.vm.QuestsFragmentVM;

public abstract class Quest implements Serializable {

    private final long innerId;

    public Quest(long innerId, QuestFamilyElement questFamilyElement) {
        this.innerId = innerId;
    }

    public long getInnerId() {
        return innerId;
    }

    public abstract void onClick(Context context, QuestsFragmentVM.Listener listener);
}
