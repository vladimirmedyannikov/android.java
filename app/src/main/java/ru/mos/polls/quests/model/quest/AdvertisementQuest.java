package ru.mos.polls.quests.model.quest;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.quests.model.QuestFamilyElement;
import ru.mos.polls.quests.vm.QuestsFragmentVM;

public class AdvertisementQuest extends BackQuest {
    public static final String TYPE = "advertisement";
    public static final String TEXT_HTML = "text_html";
    @SerializedName("text_html")
    private String html;

    public AdvertisementQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
        html = questFamilyElement.getHtml();
    }


    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public void onClick(Context context, QuestsFragmentVM.Listener listener) {
        super.onClick(context, listener);
    }
}
