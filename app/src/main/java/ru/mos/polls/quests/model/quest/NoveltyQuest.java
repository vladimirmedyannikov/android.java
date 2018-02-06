package ru.mos.polls.quests.model.quest;

import ru.mos.polls.quests.model.QuestFamilyElement;


public class NoveltyQuest extends DetailsQuest {
    public static final String TYPE = "novelty";

    public NoveltyQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
    }
}
