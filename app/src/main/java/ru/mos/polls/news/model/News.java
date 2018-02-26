package ru.mos.polls.news.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 26.02.18.
 */

public class News {

    @SerializedName("type")
    private String type;
    @SerializedName("title")
    private String title;
    @SerializedName("columns")
    private List<String> columns;
    @SerializedName("link_url")
    private String linkUrl;
    @SerializedName("link_title")
    private String linkTitle;
    @SerializedName("img")
    private String img;
    @SerializedName("style")
    private String style;
    @SerializedName("id")
    private long id;
    /**
     * необязательные параметры
     * ⇓   ⇓   ⇓   ⇓   ⇓   ⇓   ⇓
     */
    @SerializedName("need_hide_task")
    private boolean needHideTask = false;
    @SerializedName("poll_id")
    private long pollId;
    @SerializedName("hearing_id")
    private long hearingId;
    @SerializedName("unviewed")
    private boolean unviewed;

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getColumns() {
        return columns;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public String getImg() {
        return img;
    }

    public String getStyle() {
        return style;
    }

    public long getId() {
        return id;
    }

    public boolean isNeedHideTask() {
        return needHideTask;
    }

    public long getPollId() {
        return pollId;
    }

    public long getHearingId() {
        return hearingId;
    }

    public boolean isUnviewed() {
        return unviewed;
    }
}
