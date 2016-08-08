package ru.mos.polls.social.model;

import ru.mos.polls.R;
import ru.mos.polls.social.manager.SocialManager;


public class SocialBindItem {
    public static SocialBindItem FB = new SocialBindItem(SocialManager.SOCIAL_ID_FB, R.drawable.fb, R.drawable.fb_off, R.string.facebook);
    public static SocialBindItem TW = new SocialBindItem(SocialManager.SOCIAL_ID_TW, R.drawable.tw, R.drawable.tw_off, R.string.tw);
    public static SocialBindItem OK = new SocialBindItem(SocialManager.SOCIAL_ID_OK, R.drawable.ok, R.drawable.ok_off, R.string.ok);
    public static SocialBindItem VK = new SocialBindItem(SocialManager.SOCIAL_ID_VK, R.drawable.vk, R.drawable.vk_off, R.string.vk);
    public static SocialBindItem GP = new SocialBindItem(SocialManager.SOCIAL_ID_GP, R.drawable.close_google, R.drawable.google_gray, R.string.social_gp);

    public static SocialBindItem[] ITEMS = new SocialBindItem[]{FB, TW, OK, VK};

    public static int getTitle(int socialId) {
        int result = -1;
        SocialBindItem socialBindItem = getItem(socialId);
        if (socialBindItem != null) {
            result = socialBindItem.getResTitle();
        }
        return result;
    }

    public static int getBindResId(int socialId) {
        int result = -1;
        SocialBindItem socialBindItem = getItem(socialId);
        if (socialBindItem != null) {
            result = socialBindItem.getBindResId();
        }
        return result;
    }

    public static int getUnBindResId(int socialId) {
        int result = -1;
        SocialBindItem socialBindItem = getItem(socialId);
        if (socialBindItem != null) {
            result = socialBindItem.getUnBindResId();
        }
        return result;
    }

    public static SocialBindItem getItem(int socialId) {
        SocialBindItem result = null;
        for (SocialBindItem socialBindItem : ITEMS) {
            if (socialBindItem.getSocialId() == socialId) {
                result = socialBindItem;
                break;
            }
        }
        return result;
    }

    private int socialId;
    private int bindResId;
    private int unBindResId;
    private int resTitle;

    public SocialBindItem(int socialId, int bindResId, int unBindResId, int resTitle) {
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
