package ru.mos.polls.newquests.model.quest;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.R;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.newquests.model.QuestFamilyList;
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
    public int icon;
    public static final String TYPE_SOCIAL = "social";
    public static final String ID_POST_IN_SOCIAL = "postInSocial";
    public static final String ID_INVITE_FRIENDS = "inviteFriends";
    public static final String ID_RATE_THIS_APP = "rateThisApplication";

    public BackQuest(long innerId, QuestFamilyList questFamilyList) {
        super(innerId, questFamilyList);
        points = questFamilyList.getPoints();
        title = questFamilyList.getTitle();
        type = questFamilyList.getType();
        priority = questFamilyList.getPriority();
        id = questFamilyList.getId();
        details = questFamilyList.getDetails();
        icon = getIcon();
        isHidden = questFamilyList.isHidden();
        urlScheme = questFamilyList.getUrlScheme();
    }

    public int getIcon() {
        int iconId;
        if (ID_INVITE_FRIENDS.equals(id)) {
            iconId = R.drawable.image_icon_category_friends;
        } else if (ID_POST_IN_SOCIAL.equals(id)) {
            iconId = R.drawable.image_icon_category_social;
        } else if (ID_RATE_THIS_APP.equals(id)) {
            iconId = R.drawable.icon03;
        } else {
            iconId = R.drawable.image_icon_category_profile;
        }
        return iconId;
    }

    public static String processPoints(int points) {
        return String.format(PATTERN, points);
    }

    /**
     * @since 1.8 появилась возможность скрывать quest
     */
    @SerializedName("hidden")
    private boolean isHidden;
    /**
     * @since 1.9 запуск экранов через неявные интенты
     */
    @SerializedName("url_schemes")
    private UrlSchemes urlScheme;

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
        return urlScheme != null ? urlScheme.getShame() : null;
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

    public class UrlSchemes {
        @SerializedName("android")
        private String shame;

        public String getShame() {
            return shame;
        }
    }
}
