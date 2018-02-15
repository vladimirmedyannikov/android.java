package ru.mos.polls.navigation.drawer;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import ru.mos.polls.R;
import ru.mos.polls.badge.model.BadgesSource;

public class NavigationMenuItem {

    public static final int QUESTS = 10;
    public static final int PROFILE = 20;
    public static final int MY_POINTS = 30;
    public static final int MY_FREE_TIME = 35;
    public static final int ABOUT = 40;
    public static final int NEWS = 50;
    public static final int POLLS = 60;
    public static final int ELECTRONIC_HOUSE = 65;
    public static final int SETTINGS = 70;
    public static final int SUPPORT = 80;
    public static final int NOVELTY = 90;
    public static final int SHOP = 100;
    public static final int FRIENDS = 101;

    /**
     * Врмеменно отключаем функционал мероприятий
     */
    private static NavigationMenuItem ITEM_ABOUT = new NavigationMenuItem(ABOUT, R.string.mainmenu_support);
    private static NavigationMenuItem ITEM_QUESTS = new NavigationMenuItem(QUESTS, R.string.mainmenu_quests);
    private static NavigationMenuItem ITEM_POLLS = new NavigationMenuItem(POLLS, R.string.mainmenu_polls, BadgesSource.TAG_POLLS);
    private static NavigationMenuItem ITEM_NOVELTY = new NavigationMenuItem(NOVELTY, R.string.mainmenu_novelty, BadgesSource.TAG_NOVELTY);
    private static NavigationMenuItem ITEM_NEWS = new NavigationMenuItem(NEWS, R.string.mainmenu_news, BadgesSource.TAG_NEWS);
    private static NavigationMenuItem ITEM_SHOP = new NavigationMenuItem(SHOP, R.string.mainmenu_shop);
    private static NavigationMenuItem ITEM_MY_POINTS = new NavigationMenuItem(MY_POINTS, R.string.mainmenu_my_points, BadgesSource.TAG_POINTS);
    private static NavigationMenuItem ITEM_PROFILE = new NavigationMenuItem(PROFILE, R.string.mainmenu_profile);
    private static NavigationMenuItem ITEM_ELECTRONIC_HOUSE = new NavigationMenuItem(ELECTRONIC_HOUSE, R.string.mainmenu_electronic_house, BadgesSource.TAG_ELECTRONIC_HOUSE);
    private static NavigationMenuItem ITEM_SETTINGS = new NavigationMenuItem(SETTINGS, R.string.mainmenu_settings);
    private static NavigationMenuItem ITEM_FRIENDS = new NavigationMenuItem(FRIENDS, R.string.mainmenu_friends, BadgesSource.TAG_FRIENDS);

    public static NavigationMenuItem[] ITEMS_MENU = new NavigationMenuItem[]{
            ITEM_QUESTS,
            ITEM_POLLS,
            ITEM_NOVELTY,
            ITEM_NEWS,
            ITEM_SHOP,
            ITEM_MY_POINTS,
            ITEM_ELECTRONIC_HOUSE,
            ITEM_PROFILE,
//            ITEM_FRIENDS, //вернуть в версии 2.5.0
            ITEM_SETTINGS,
            ITEM_ABOUT
    };

    public static NavigationMenuItem[] ITEMS_BOTTOM = new NavigationMenuItem[]{
            ITEM_SETTINGS,
            ITEM_ABOUT
    };

    private final int id;
    @StringRes
    private final int textId;
    @Nullable
    private final String bageTag;

    private final boolean cheackable;

    private NavigationMenuItem(int id, @StringRes int textId) {
        this(id, textId, null, true);
    }

    private NavigationMenuItem(int id, @StringRes int textId, @Nullable String bageTag) {
        this(id, textId, bageTag, true);
    }

    private NavigationMenuItem(int id, @StringRes int textId, @Nullable String bageTag, boolean cheackable) {
        this.id = id;
        this.textId = textId;
        this.bageTag = bageTag;
        this.cheackable = cheackable;
    }

    public int getId() {
        return id;
    }

    @StringRes
    public int getTextId() {
        return textId;
    }

    @Nullable
    public String getBageTag() {
        return bageTag;
    }

    public boolean isCheackable() {
        return cheackable;
    }


}
