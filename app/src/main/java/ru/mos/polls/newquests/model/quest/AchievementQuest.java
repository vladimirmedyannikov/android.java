package ru.mos.polls.newquests.model.quest;

import ru.mos.polls.newquests.model.QuestFamilyList;


public class AchievementQuest extends DetailsQuest {
    public static final String TYPE = "achievement";

    public AchievementQuest(long innerId, QuestFamilyList questFamilyList) {
        super(innerId, questFamilyList);
    }
}
