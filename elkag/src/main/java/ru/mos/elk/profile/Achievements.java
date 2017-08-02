package ru.mos.elk.profile;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Класс достижений временный
 */

public class Achievements implements Serializable {
    public static final String LAST_ACHIEVEMENTS = "last_achievements";
    private String id;
    @SerializedName("img_url")
    private String imageUrl;
    private String title;
    private String description;
    private String body;
    @SerializedName("is_next")
    private boolean isNext;
    private boolean isNeedHideTask;

    public Achievements(String id, String imageUrl, String title, String description, String body, boolean isNext, boolean isNeedHideTask) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.body = body;
        this.isNext = isNext;
        this.isNeedHideTask = isNeedHideTask;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getBody() {
        return body;
    }

    public boolean isNext() {
        return isNext;
    }

    public boolean isNeedHideTask() {
        return isNeedHideTask;
    }
}
