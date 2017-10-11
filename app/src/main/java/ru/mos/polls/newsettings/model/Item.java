package ru.mos.polls.newsettings.model;

import ru.mos.polls.R;

/**
 * Created by matek3022 on 25.09.17.
 */

public class Item {
    public static final int SUBSCRIBE = 10;
    public static final int USER_LOCK = 20;
    public static final int CHANGE_PASSWORD = 30;
    public static final int LOGOUT = 40;
    public static final int SOURCES_POLL = 50;

    public static final Item[] SETTINGS = new Item[]{
            new Item(SOURCES_POLL, 0, R.string.sources_poll),
            new Item(SUBSCRIBE, R.drawable.rss, R.string.subscribes),
            new Item(USER_LOCK, R.drawable.elk_block, R.string.user_lock),
            /*new SettingItem(CHANGE_PASSWORD, R.drawable.elk_change_password, R.string.change_password),*/
            new Item(LOGOUT, R.drawable.elk_logout, R.string.logout),
    };

    private int id;
    private int drawableId;
    private int textId;

    public Item(int id, int drawableId, int textId) {
        this.id = id;
        this.drawableId = drawableId;
        this.textId = textId;
    }

    public int getId() {
        return id;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public int getTextId() {
        return textId;
    }
}
