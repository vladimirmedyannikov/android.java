package ru.mos.polls.newquests.model.quest;

import ru.mos.polls.newquests.model.QuestFamilyList;


public class NoveltyQuest extends DetailsQuest {
    public static final String TYPE = "novelty";

    public NoveltyQuest(long innerId, QuestFamilyList questFamilyList) {
        super(innerId, questFamilyList);
    }
}
