package ru.mos.polls.social.model;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.social.manager.SocialManager;

/**
 * Структура данных для item в списке доступных социальных сетей
 */
public class SocialPostItem {
    private int resourceId;
    private int resourceDisableId;
    private String title;
    private SocialPostValue socialPostValue;

    /**
     * Парсинг данных для постнга с сс
     *
     * @param socialsJson - json данных для постинга
     * @return массив item-ов для списка диалога
     */
    public static List<SocialPostItem> createItems(Context context,
                                                   JSONObject socialsJson,
                                                   SocialPostValue.Type type,
                                                   Object id) {
        List<SocialPostItem> result = new ArrayList<SocialPostItem>();
        socialsJson = socialsJson.optJSONObject("social");

        SocialPostValue value = getFbSocialPostValue(socialsJson, type, id);
        value.setType(type);
        SocialPostItem item = new SocialPostItem(R.drawable.fb, R.drawable.fb, context.getString(R.string.fb), value);
        result.add(item);

        value = getVkSocialPostValue(socialsJson, type, id);
        value.setType(type);
        item = new SocialPostItem(R.drawable.vk, R.drawable.vk, context.getString(R.string.vk), value);
        result.add(item);

        value = getTwSocialPostValue(socialsJson, type, id);
        value.setType(type);
        item = new SocialPostItem(R.drawable.tw, R.drawable.tw, context.getString(R.string.tw), value);
        result.add(item);

        value = getOkSocialPostValue(socialsJson, type, id);
        value.setType(type);
        item = new SocialPostItem(R.drawable.ok, R.drawable.ok, context.getString(R.string.ok), value);
        result.add(item);

        return result;
    }

    /**
     * Создание списка с item-ов для постинга результатов опроса или чекина
     *
     * @param socialPostValue - данные для постинга
     * @return - список item-ов
     */
    public static List<SocialPostItem> createItems(Context context, SocialPostValue socialPostValue) {
        List<SocialPostItem> result = new ArrayList<SocialPostItem>();

        SocialPostValue fbSocialPostValue = new SocialPostValue(socialPostValue, SocialManager.SOCIAL_NAME_FB);
        SocialPostItem item = new SocialPostItem(R.drawable.fb, R.drawable.fb, context.getString(R.string.fb), fbSocialPostValue);
        result.add(item);

        SocialPostValue vkSocialPostValue = new SocialPostValue(socialPostValue, SocialManager.SOCIAL_NAME_VK);
        item = new SocialPostItem(R.drawable.vk, R.drawable.vk, context.getString(R.string.vk), vkSocialPostValue);
        result.add(item);

        SocialPostValue twSocialPostValue = new SocialPostValue(socialPostValue, SocialManager.SOCIAL_NAME_TW);
        item = new SocialPostItem(R.drawable.tw, R.drawable.tw, context.getString(R.string.tw), twSocialPostValue);
        result.add(item);

        SocialPostValue okSocialPostValue = new SocialPostValue(socialPostValue, SocialManager.SOCIAL_NAME_OK);
        item = new SocialPostItem(R.drawable.ok, R.drawable.ok, context.getString(R.string.ok), okSocialPostValue);
        result.add(item);

        return result;
    }

    public SocialPostItem(int resourceId, int resourceDisableId, String title, SocialPostValue socialPostValue) {
        this.resourceId = resourceId;
        this.resourceDisableId = resourceDisableId;
        this.title = title;
        this.socialPostValue = socialPostValue;
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getResourceDisableId() {
        return resourceDisableId;
    }

    public String getTitle() {
        return title;
    }

    public SocialPostValue getSocialPostValue() {
        return socialPostValue;
    }

    public static SocialPostValue getVkSocialPostValue(JSONObject jsonObject, SocialPostValue.Type type, Object id) {
        return getSocialPostValue(SocialManager.SOCIAL_NAME_VK, jsonObject, type, id);
    }

    public static SocialPostValue getFbSocialPostValue(JSONObject jsonObject, SocialPostValue.Type type, Object id) {
        return getSocialPostValue(SocialManager.SOCIAL_NAME_FB, jsonObject, type, id);
    }

    public static SocialPostValue getTwSocialPostValue(JSONObject jsonObject, SocialPostValue.Type type, Object id) {
        return getSocialPostValue(SocialManager.SOCIAL_NAME_TW, jsonObject, type, id);
    }

    public static SocialPostValue getOkSocialPostValue(JSONObject jsonObject, SocialPostValue.Type type, Object id) {
        return getSocialPostValue(SocialManager.SOCIAL_NAME_OK, jsonObject, type, id);
    }

    public static SocialPostValue getSocialPostValue(String socialName, JSONObject jsonObject, SocialPostValue.Type type, Object id) {
        SocialPostValue result = new SocialPostValue();
        if (jsonObject != null) {
            JSONObject socialJson = jsonObject.optJSONObject(socialName);
            result = new SocialPostValue(socialName, socialJson, type, id);
        }
        return result;
    }
}
