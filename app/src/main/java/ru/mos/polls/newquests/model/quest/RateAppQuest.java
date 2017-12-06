package ru.mos.polls.newquests.model.quest;

import android.content.Context;

import org.json.JSONObject;

import ru.mos.polls.quests.QuestsFragment;

/**
 * Задание для оценки приложения
 * @since 1.9
 */
public class RateAppQuest extends SocialQuest {
    private String appId;

    public RateAppQuest(long innerId, JSONObject jsonObject){
        super(innerId, jsonObject);
        appId = getAppId(jsonObject);
    }

    public static String getAppId(JSONObject questJson) {
        String result = "ru.mos.polls";
        if (questJson != null) {
            questJson = questJson.optJSONObject("app_ids");
            if (questJson != null) {
                result = questJson.optString("android");
            }
        }
        return result;
    }

    @Override
    public void onClick(Context context, QuestsFragment.Listener listener) {
        listener.onRateThisApplication(appId);
    }
}
