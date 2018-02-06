package ru.mos.polls.quests.model.quest;

import ru.mos.polls.quests.model.QuestFamilyElement;


public class EventQuest extends BackQuest {
    public static final String TYPE = "events";

    public EventQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
    }


    public long getSurveyId() {
        return Long.parseLong(getId());
    }

    @Override
    public String toString() {
        return "FavoriteQuest " + getId() + " " + getTitle();
    }
}

