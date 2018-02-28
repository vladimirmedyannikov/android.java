package ru.mos.polls.quests.model.quest;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.quests.model.QuestFamilyElement;


public class NewsOssQuest extends BackQuest {
    public static final String TYPE = "news_oss";
    @SerializedName("link_url")
    private String linkUrl;

    public NewsOssQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
        linkUrl = questFamilyElement.getLinkUrl();
    }
}
