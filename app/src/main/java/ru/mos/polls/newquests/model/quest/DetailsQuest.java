package ru.mos.polls.newquests.model.quest;

import ru.mos.polls.newquests.model.QuestFamilyList;

public abstract class DetailsQuest extends BackQuest {

    public DetailsQuest(long innerId, QuestFamilyList questFamilyList) {
        super(innerId, questFamilyList);
    }
}
