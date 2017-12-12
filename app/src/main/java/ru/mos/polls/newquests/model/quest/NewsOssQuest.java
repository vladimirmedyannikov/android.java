package ru.mos.polls.newquests.model.quest;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.newquests.model.QuestFamilyElement;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 12.12.17.
 */

public class NewsOssQuest extends BackQuest {
    public static final String TYPE = "news_oss";
    @SerializedName("link_url")
    private String linkUrl;

    public NewsOssQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
        linkUrl = questFamilyElement.getLinkUrl();
    }
}
