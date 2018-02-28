package ru.mos.polls.quests.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.quests.model.quest.BackQuest;
import ru.mos.polls.quests.model.quest.RateAppQuest;


public class QuestFamilyElement {
    @SerializedName("sub_ids")
    private List<String> idsList;
    @SerializedName("percent_fill_profile")
    private int percent;
    @SerializedName("link_url")
    private String linkUrl;
    @SerializedName("app_ids")
    private RateAppQuest.AppIds appIds;
    @SerializedName("kind")
    private Kind kind;
    @SerializedName("points")
    private int points;
    @SerializedName("title")
    private String title;
    @SerializedName("type")
    private String type;
    @SerializedName("priority")
    private int priority;
    @SerializedName("id")
    private String id;
    @SerializedName("details")
    private String details;
    @SerializedName("hidden")
    private boolean isHidden;
    @SerializedName("url_schemes")
    private BackQuest.UrlSchemes urlScheme;
    @SerializedName("text_html")
    private String html;

    public List<String> getIdsList() {
        return idsList;
    }

    public int getPercent() {
        return percent;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public RateAppQuest.AppIds getAppIds() {
        return appIds;
    }

    public Kind getKind() {
        return kind;
    }

    public int getPoints() {
        return points;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public String getId() {
        return id;
    }

    public String getDetails() {
        return details;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public BackQuest.UrlSchemes getUrlScheme() {
        return urlScheme;
    }

    public String getHtml() {
        return html;
    }
}
