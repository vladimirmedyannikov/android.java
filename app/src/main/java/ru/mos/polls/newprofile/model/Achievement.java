package ru.mos.polls.newprofile.model;

import java.io.Serializable;

/**
 * Created by Trunks on 23.06.2017.
 */

public class Achievement implements Serializable {
    private String id;
    private String imageUrl;
    private String title;
    private String description;
    private String body;
    private boolean isNext;
    private boolean isNeedHideTask;

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
