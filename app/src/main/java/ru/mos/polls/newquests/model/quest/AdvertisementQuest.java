package ru.mos.polls.newquests.model.quest;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.newquests.model.QuestFamilyList;
import ru.mos.polls.quests.QuestsFragment;

public class AdvertisementQuest extends BackQuest {
    public static final String TYPE = "advertisement";
    public static final String TEXT_HTML = "text_html";
    @SerializedName("text_html")
    private String html;

    public AdvertisementQuest(long innerId, QuestFamilyList questFamilyList) {
        super(innerId, questFamilyList);
        html = questFamilyList.getHtml();
    }


    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public void onClick(Context context, QuestsFragment.Listener listener) {
        super.onClick(context, listener);
    }
}
