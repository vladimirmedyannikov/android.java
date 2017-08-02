package ru.mos.elk.profile;


/**
 * Класс достижений временный
 */

public class Achievements {
    public static final String LAST_ACHIEVEMENTS = "last_achievements";
    private String id;
    private String img_url;
    private String title;
    private boolean is_next;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isIs_next() {
        return is_next;
    }

    public void setIs_next(boolean is_next) {
        this.is_next = is_next;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
