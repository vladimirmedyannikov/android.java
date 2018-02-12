package ru.mos.polls.quests.model.quest;

import ru.mos.polls.quests.model.QuestFamilyElement;

public abstract class DetailsQuest extends BackQuest {

    public DetailsQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
    }
}
