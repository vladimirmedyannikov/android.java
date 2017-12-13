package ru.mos.polls.newquests.model.quest;

import ru.mos.polls.newquests.model.QuestFamilyElement;


public class NoveltyQuest extends DetailsQuest {
    public static final String TYPE = "novelty";

    public NoveltyQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
    }
}
