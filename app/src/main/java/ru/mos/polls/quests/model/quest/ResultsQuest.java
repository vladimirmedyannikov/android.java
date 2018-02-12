package ru.mos.polls.quests.model.quest;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.quests.model.QuestFamilyElement;

public class ResultsQuest extends BackQuest {
    public static final String TYPE = "results";
    private static final String LINK_URL = "link_url";
    @SerializedName("link_url")
    private String linkUrl;

    public ResultsQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
        linkUrl = questFamilyElement.getLinkUrl();
    }
}