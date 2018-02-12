package ru.mos.polls.quests.model.quest;

import ru.mos.polls.quests.model.QuestFamilyElement;


public class AchievementQuest extends DetailsQuest {
    public static final String TYPE = "achievement";

    public AchievementQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
    }
}
