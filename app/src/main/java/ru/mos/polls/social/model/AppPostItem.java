package ru.mos.polls.social.model;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;

/**
 * Структура данных для item в списке доступных социальных сетей
 */
public class AppPostItem {
    private int resourceId;
    private int resourceDisableId;
    private String title;
    private AppPostValue appPostValue;

    /**
     * Парсинг данных для постнга с сс
     *
     * @param socialsJson - json данных для постинга
     * @return массив item-ов для списка диалога
     */
    public static List<AppPostItem> createItems(Context context,
                                                JSONObject socialsJson,
                                                AppPostValue.Type type,
                                                Object id) {
        List<AppPostItem> result = new ArrayList<AppPostItem>();
        socialsJson = socialsJson.optJSONObject("social");

        AppPostValue value = getFbSocialPostValue(socialsJson, type, id);
        value.setType(type);
        AppPostItem item = new AppPostItem(R.drawable.fb, R.drawable.fb, context.getString(R.string.fb), value);
        result.add(item);

        value = getVkSocialPostValue(socialsJson, type, id);
        value.setType(type);
        item = new AppPostItem(R.drawable.vk, R.drawable.vk, context.getString(R.string.vk), value);
        result.add(item);

        value = getTwSocialPostValue(socialsJson, type, id);
        value.setType(type);
        item = new AppPostItem(R.drawable.tw, R.drawable.tw, context.getString(R.string.tw), value);
        result.add(item);

        value = getOkSocialPostValue(socialsJson, type, id);
        value.setType(type);
        item = new AppPostItem(R.drawable.ok, R.drawable.ok, context.getString(R.string.ok), value);
        result.add(item);

        return result;
    }

    /**
     * Создание списка с item-ов для постинга результатов опроса или чекина
     *
     * @param appPostValue - данные для постинга
     * @return - список item-ов
     */
    public static List<AppPostItem> createItems(Context context, AppPostValue appPostValue) {
        List<AppPostItem> result = new ArrayList<AppPostItem>();

        AppPostValue fbAppPostValue = new AppPostValue(appPostValue, AppSocial.NAME_FB);
        AppPostItem item = new AppPostItem(R.drawable.fb, R.drawable.fb, context.getString(R.string.fb), fbAppPostValue);
        result.add(item);

        AppPostValue vkAppPostValue = new AppPostValue(appPostValue, AppSocial.NAME_VK);
        item = new AppPostItem(R.drawable.vk, R.drawable.vk, context.getString(R.string.vk), vkAppPostValue);
        result.add(item);

        AppPostValue twAppPostValue = new AppPostValue(appPostValue, AppSocial.NAME_TW);
        item = new AppPostItem(R.drawable.tw, R.drawable.tw, context.getString(R.string.tw), twAppPostValue);
        result.add(item);

        AppPostValue okAppPostValue = new AppPostValue(appPostValue, AppSocial.NAME_OK);
        item = new AppPostItem(R.drawable.ok, R.drawable.ok, context.getString(R.string.ok), okAppPostValue);
        result.add(item);

        return result;
    }

    public AppPostItem(int resourceId, int resourceDisableId, String title, AppPostValue appPostValue) {
        this.resourceId = resourceId;
        this.resourceDisableId = resourceDisableId;
        this.title = title;
        this.appPostValue = appPostValue;
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

    public AppPostValue getAppPostValue() {
        return appPostValue;
    }

    public static AppPostValue getVkSocialPostValue(JSONObject jsonObject, AppPostValue.Type type, Object id) {
        return getSocialPostValue(AppSocial.NAME_VK, jsonObject, type, id);
    }

    public static AppPostValue getFbSocialPostValue(JSONObject jsonObject, AppPostValue.Type type, Object id) {
        return getSocialPostValue(AppSocial.NAME_FB, jsonObject, type, id);
    }

    public static AppPostValue getTwSocialPostValue(JSONObject jsonObject, AppPostValue.Type type, Object id) {
        return getSocialPostValue(AppSocial.NAME_TW, jsonObject, type, id);
    }

    public static AppPostValue getOkSocialPostValue(JSONObject jsonObject, AppPostValue.Type type, Object id) {
        return getSocialPostValue(AppSocial.NAME_OK, jsonObject, type, id);
    }

    public static AppPostValue getSocialPostValue(String socialName, JSONObject jsonObject, AppPostValue.Type type, Object id) {
        AppPostValue result = new AppPostValue();
        if (jsonObject != null) {
            JSONObject socialJson = jsonObject.optJSONObject(socialName);
            result = new AppPostValue(socialName, socialJson, type, id);
        }
        return result;
    }
}
