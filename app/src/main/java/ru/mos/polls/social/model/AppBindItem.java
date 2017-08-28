package ru.mos.polls.social.model;

import ru.mos.polls.R;


public class AppBindItem {
    public static AppBindItem FB = new AppBindItem(AppSocial.ID_FB, R.drawable.fb, R.drawable.fb_off, R.string.facebook);
    public static AppBindItem TW = new AppBindItem(AppSocial.ID_TW, R.drawable.tw, R.drawable.tw_off, R.string.tw);
    public static AppBindItem OK = new AppBindItem(AppSocial.ID_OK, R.drawable.ok, R.drawable.ok_off, R.string.ok);
    public static AppBindItem VK = new AppBindItem(AppSocial.ID_VK, R.drawable.vk, R.drawable.vk_off, R.string.vk);
    public static AppBindItem GP = new AppBindItem(AppSocial.ID_GP, R.drawable.close_google, R.drawable.google_gray, R.string.social_gp);

    public static AppBindItem[] ITEMS = new AppBindItem[]{FB, TW, OK, VK};

    public static int getTitle(int socialId) {
        int result = -1;
        AppBindItem appBindItem = getItem(socialId);
        if (appBindItem != null) {
            result = appBindItem.getResTitle();
        }
        return result;
    }

    public static int getBindResId(int socialId) {
        int result = -1;
        AppBindItem appBindItem = getItem(socialId);
        if (appBindItem != null) {
            result = appBindItem.getBindResId();
        }
        return result;
    }

    public static int getUnBindResId(int socialId) {
        int result = -1;
        AppBindItem appBindItem = getItem(socialId);
        if (appBindItem != null) {
            result = appBindItem.getUnBindResId();
        }
        return result;
    }

    public static AppBindItem getItem(int socialId) {
        AppBindItem result = null;
        for (AppBindItem appBindItem : ITEMS) {
            if (appBindItem.getSocialId() == socialId) {
                result = appBindItem;
                break;
            }
        }
        return result;
    }

    private int socialId;
    private int bindResId;
    private int unBindResId;
    private int resTitle;

    public AppBindItem(int socialId, int bindResId, int unBindResId, int resTitle) {
        this.socialId = socialId;
        this.bindResId = bindResId;
        this.unBindResId = unBindResId;
        this.resTitle = resTitle;
    }


    public int getSocialId() {
        return socialId;
    }

    public int getBindResId() {
        return bindResId;
    }

    public int getUnBindResId() {
        return unBindResId;
    }

    public int getResTitle() {
        return resTitle;
    }
}
