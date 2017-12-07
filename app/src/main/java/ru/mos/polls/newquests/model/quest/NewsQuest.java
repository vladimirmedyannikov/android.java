package ru.mos.polls.newquests.model.quest;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.newquests.model.QuestFamilyList;


public class NewsQuest extends BackQuest {
    public static final String TYPE = "news";
    private static final String LINK_URL = "link_url";
    @SerializedName("link_url")
    private String linkUrl;

    public NewsQuest(long innerId, QuestFamilyList questFamilyList) {
        super(innerId, questFamilyList);
        linkUrl = questFamilyList.getLinkUrl();
    }
}
