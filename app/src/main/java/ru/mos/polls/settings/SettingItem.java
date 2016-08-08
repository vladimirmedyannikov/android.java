package ru.mos.polls.settings;

import ru.mos.polls.R;

/**
 * Структура данных для хранения item настройки
 *
 */
public class SettingItem {
    public static final int SUBSCRIBE = 10;
    public static final int USER_LOCK = 20;
    public static final int CHANGE_PASSWORD = 30;
    public static final int LOGOUT = 40;

    public static final SettingItem[] SETTINGS = new SettingItem[] {
            new SettingItem(SUBSCRIBE, R.drawable.rss, R.string.subscribes),
            new SettingItem(USER_LOCK, R.drawable.elk_block, R.string.user_lock),
            new SettingItem(CHANGE_PASSWORD, R.drawable.elk_change_password, R.string.change_password),
            new SettingItem(LOGOUT, R.drawable.elk_logout, R.string.logout),
    };

    private int id;
    private int drawableId;
    private int textId;

    public SettingItem(int id, int drawableId, int textId) {
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
