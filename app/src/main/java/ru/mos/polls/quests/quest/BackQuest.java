package ru.mos.polls.quests.quest;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.quests.QuestsFragment;

public abstract class BackQuest extends Quest {

    private static final String PATTERN = "+%s";
    private static final String DETAILS = "details";
    private static final String ID = "id";
    public static final String TITLE = "title";
    public static final String POINTS = "points";
    public static final String PRIORITY = "priority";
    public static final String HIDDEN = "hidden";
    public static final String TYPE = "type";
    private int points;
    private String title;
    private String type;
    private int priority;
    private String id;
    private String details;

    public BackQuest(long innerId, JSONObject jsonObject) {
        super(innerId);
        title = jsonObject.optString(TITLE);
        points = jsonObject.optInt(POINTS);
        priority = jsonObject.optInt(PRIORITY);
        isHidden = jsonObject.optBoolean(HIDDEN);
        details = jsonObject.optString(DETAILS);
        id = jsonObject.optString(ID);
        urlScheme = urlSchemeFromJson(jsonObject);
        type = jsonObject.optString(TYPE);
    }

    protected static String processPoints(int points) {
        return String.format(PATTERN, points);
    }

    /**
     * @since 1.8 появилась возможность скрывать quest
     */
    private boolean isHidden;
    /**
     * @since 1.9 запуск экранов через неявные интенты
     */
    private String urlScheme;

    public static String urlSchemeFromJson(JSONObject questJson) {
        String result = null;
        if (questJson != null) {
            questJson = questJson.optJSONObject("url_schemes");
            result = questJson.optString("android");
        }
        return result;
    }

    public int getPoints() {
        return points;
    }

    public String getTitle() {
        return title;
    }

    public int getPriority() {
        return priority;
    }

    public int compare(BackQuest otherQuest) {
        int result = 0;
        if (otherQuest != null) {
            if (priority > otherQuest.getPriority()) {
                result = 1;
            } else if (priority < otherQuest.getPriority()) {
                result = -1;
            }
        }
        return result;
    }

    public String getDetails() {
        return details;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getUrlScheme() {
        return urlScheme;
    }

    @Override
    public void onClick(Context context, QuestsFragment.Listener listener) {
        UrlSchemeController.start(context, getUrlScheme());
    }

    public void addJsonForHide(JSONObject request, boolean willBeHide) {
        try {
            request.put("type", type);
            request.put("id", id);
            request.put("hide", willBeHide);
        } catch (JSONException ignored) {

        }
    }
}
