package ru.mos.polls.newquests.model.quest;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.newquests.model.QuestFamilyList;
import ru.mos.polls.quests.QuestsFragment;

/**
 * Задание для оценки приложения
 * @since 1.9
 */
public class RateAppQuest extends SocialQuest {
    @SerializedName("app_ids")
    private AppIds appIds;

    public RateAppQuest(long innerId, QuestFamilyList questFamilyList) {
        super(innerId, questFamilyList);
        appIds = questFamilyList.getAppIds();
    }

    public String getAppId() {
        String result = "ru.mos.polls";
        if (appIds != null) {
            result = appIds.getAppId();
        }
        return result;
    }

    @Override
    public void onClick(Context context, QuestsFragment.Listener listener) {
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
