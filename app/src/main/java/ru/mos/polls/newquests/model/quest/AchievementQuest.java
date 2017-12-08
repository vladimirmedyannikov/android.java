package ru.mos.polls.newquests.model.quest;

import ru.mos.polls.newquests.model.QuestFamilyElement;


public class AchievementQuest extends DetailsQuest {
    public static final String TYPE = "achievement";

    public AchievementQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
    }
}
