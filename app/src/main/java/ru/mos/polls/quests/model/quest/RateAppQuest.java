package ru.mos.polls.quests.model.quest;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.quests.model.QuestFamilyElement;
import ru.mos.polls.quests.vm.QuestsFragmentVM;

/**
 * Задание для оценки приложения
 * @since 1.9
 */
public class RateAppQuest extends SocialQuest {
    @SerializedName("app_ids")
    private AppIds appIds;

    public RateAppQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
        appIds = questFamilyElement.getAppIds();
    }

    public String getAppId() {
        String result = "ru.mos.polls";
        if (appIds != null) {
            result = appIds.getAppId();
        }
        return result;
    }

    @Override
    public void onClick(Context context, QuestsFragmentVM.Listener listener) {
        listener.onRateThisApplication(getAppId());
    }

    public class AppIds {
        @SerializedName("android")
        private String appId;

        public String getAppId() {
            return appId;
        }
    }
}
