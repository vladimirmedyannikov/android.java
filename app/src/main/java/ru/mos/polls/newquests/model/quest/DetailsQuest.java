package ru.mos.polls.newquests.model.quest;

import ru.mos.polls.newquests.model.QuestFamilyElement;

public abstract class DetailsQuest extends BackQuest {

    public DetailsQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
    }
}
