package ru.mos.polls.newquests.model.quest;

import android.content.Context;
import android.view.View;

import ru.mos.polls.quests.QuestsFragment;

public abstract class Quest {

    private final long innerId;

    public Quest(long innerId) {
        this.innerId = innerId;
    }

    public abstract View inflate(Context context, View convertView);

    public long getInnerId() {
        return innerId;
    }

    public abstract void onClick(Context context, QuestsFragment.Listener listener);
}
