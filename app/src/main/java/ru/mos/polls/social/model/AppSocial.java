package ru.mos.polls.social.model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import ru.mos.polls.R;
import ru.mos.polls.social.storable.AppStorable;
import ru.mos.social.model.Configurator;
import ru.mos.social.model.Token;
import ru.mos.social.model.social.Social;
import ru.mos.social.model.social.user.FacebookUser;
import ru.mos.social.model.social.user.GpUser;
import ru.mos.social.model.social.user.OkUser;
import ru.mos.social.model.social.user.TwitterUser;
import ru.mos.social.model.social.user.VkUser;
import ru.mos.social.utils.SocialTextUtils;

/**
 * Структура для хранения данных соцсети
 */
public class AppSocial extends Social{
    private static final String IS_LOGON = "isLogon";

    public static final String NAME_FB = "fb";
    public static final String NAME_VK = "vk";
    public static final String NAME_TW = "twitter";
    public static final String NAME_OK = "ok";

    private String icon;
    private boolean isLogon;
    private String socialTitle;

    public static int getId(String socialName) {
        int result = -1;
        if (AppSocial.NAME_FB.equalsIgnoreCase(socialName)) {
            result = AppSocial.ID_FB;
        } else if (AppSocial.NAME_VK.equalsIgnoreCase(socialName)) {
            result = AppSocial.ID_VK;
        } else if (AppSocial.NAME_OK.equalsIgnoreCase(socialName)) {
            result = AppSocial.ID_OK;
        } else if (AppSocial.NAME_TW.equalsIgnoreCase(socialName)) {
            result = AppSocial.ID_TW;
        } else if (AppSocial.NAME_GP.equalsIgnoreCase(socialName)) {
            result = AppSocial.ID_GP;
        }

        return result;
    }

    /**
     * Получаем сохраненную соцсеть
     *
     * @param context
     * @param socialId
     * @return
     */
    public static AppSocial fromPreference(Context context, int socialId) {
//        AppSocial result = Con
//        result.socialId = socialId;
//        result.socialName = SocialManager.getSocialName(socialId);
//        result.icon = SocialManager.getAvatarUrl(context, socialId);
//        String accessToken = SocialManager.getAccessToken(context, socialId);
//        String refreshToken = SocialManager.getRefreshToken(context, socialId);
//        result.tokenData = new TokenData(accessToken, refreshToken);
//        result.expired = SocialManager.getExpired(context, socialId);
//        result.isLogon = SocialManager.isLogon(context, socialId);
        return ((AppStorable) Configurator.getInstance(context).getStorable()).get(socialId);
    }

    public static Observable<List<AppSocial>> getObservableSavedSocials(Context context) {
        List<AppSocial> list = Configurator.getInstance(context).getStorable().getAll();
        for (AppSocial appSocial : list) {
            appSocial.setIsLogin(!appSocial.getToken().isEmpty());
        }
        return Observable.just(list);
    }

    public AppSocial(JSONObject socialJson) {
        super(null);
        if (socialJson != null) {
            id = socialJson.optInt(ID);
            name = SocialTextUtils.get(socialJson, NAME);
            icon = socialJson.optString(ICON);
            token = new Token(socialJson.optJSONObject(TOKEN));
            isLogon = socialJson.optBoolean(IS_LOGON);

            JSONObject user = socialJson.optJSONObject(USER);
            if (user != null) {
                switch (id) {
                    case ID_FB:
                        socialUser = new FacebookUser(user);
                        break;
                    case ID_TW:
                        socialUser = new TwitterUser(user);
                        break;
                    case ID_OK:
                        socialUser = new OkUser(user);
                        break;
                    case ID_VK:
                        socialUser = new VkUser(user);
                        break;
                    case ID_GP:
                        socialUser = new GpUser(user);
                        break;
                }
            }
        }else {
            token = new Token();
        }
    }

    public AppSocial(int id, String name, int icon, Token token) {
        super(id,name,icon,token);
    }

    public AppSocial() {
        super(null);
    }

    public AppSocial(String socialName, JSONObject socialJson) {
        super(getId(socialName), socialName, 0, new Token());
        this.name = socialName;
        id = getId(socialName);
        if (socialJson != null) {
            icon = socialJson.optString("icon");
            isLogon = socialJson.optBoolean("logined");
            token = new Token(socialJson.optString("token1"), socialJson.optString("token2"), socialJson.optLong("expired_time") * 1000);
        }
    }

    /**
     * Токены для отправки на сс АГ
     *
     * @return
     */
    public JSONObject tokenDataAsJson() {
        JSONObject result = new JSONObject();
        JSONObject tokenJson = new JSONObject();
        try {
            tokenJson.put("token1", getToken().getAccess());
            tokenJson.put("token2", getToken().getRefresh());
            result.put(name, tokenJson);
        } catch (JSONException ignored) {
        }
        return result;
    }

    /**
     * Используетс для отвязки соусети от профиля аг
     *
     * @return
     */
    public JSONObject asNull() {
        JSONObject result = new JSONObject();
        try {
            result.put(name, JSONObject.NULL);
        } catch (JSONException ignored) {
        }
        return result;
    }

    public JSONObject asKillJson() {
        JSONObject result = new JSONObject();
        try {
            JSONObject killJson = new JSONObject();
            killJson.put("kill", true);
            result.put(name, killJson);
        } catch (JSONException ignored) {
        }
        return result;
    }


    public void copy(AppSocial otherSocial) {
        if (otherSocial != null) {
            setToken(otherSocial.token);
            id = otherSocial.id;
            name = otherSocial.name;
            icon = otherSocial.icon;
            isLogon = otherSocial.isLogon;
            setSocialUser(otherSocial.getSocialUser());
        }
    }

    public String getStringIcon() {
        return icon;
    }

    public int getIcon() {
        return getSocialIcon(id);
    }

    public boolean isLogon() {
        return isLogon;
    }

    public void setIsLogin(boolean isLogan) {
        this.isLogon = isLogan;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof AppSocial) {
            AppSocial other = (AppSocial) o;
            result = id == other.id
                    && isLogon == other.isLogon;
        }
        return result;
    }

    public static AppSocial findFbSocial(List<AppSocial> socials) {
        return findSocial(socials, AppSocial.ID_FB);
    }

    public static AppSocial findVkSocial(List<AppSocial> socials) {
        return findSocial(socials, AppSocial.ID_VK);
    }

    public static AppSocial findTwSocial(List<AppSocial> socials) {
        return findSocial(socials, AppSocial.ID_TW);
    }

    public static AppSocial findOkSocial(List<AppSocial> socials) {
        return findSocial(socials, AppSocial.ID_OK);
    }

    public static AppSocial findSocial(List<AppSocial> socials, int socialId) {
        AppSocial result = null;
        if (socials != null) {
            for (AppSocial social : socials) {
                if (socialId == social.getId()) {
                    result = social;
                    break;
                }
            }
        }
        return result;
    }

    public static boolean isEquals(List<AppSocial> first, List<AppSocial> second) {
        boolean result = false;
        if (first != null && second != null) {
            if (first.size() == second.size()) {
                result = true;
                for (int i = 0; i < first.size(); ++i) {
                    AppSocial social = findSocial(second, first.get(i).getId());
                    if (social != null) {
                        if (!social.equals(first.get(i))) {
                            result = false;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static int getSocialIcon(int socialId) {
        switch (socialId) {
            case AppSocial.ID_FB:
                return R.drawable.fb;
            case AppSocial.ID_VK:
                return R.drawable.vk;
            case AppSocial.ID_TW:
                return R.drawable.tw;
            case AppSocial.ID_OK:
                return R.drawable.odnklsnk;
            case AppSocial.ID_GP:
                return R.drawable.google;
        }
        return -1;
    }

    public static String getSocialName(int socialId) {
        String result = "Unknown";
        if (AppSocial.ID_FB == socialId) {
            result = AppSocial.NAME_FB;
        } else if (AppSocial.ID_VK == socialId) {
            result = AppSocial.NAME_VK;
        } else if (AppSocial.ID_OK == socialId) {
            result = AppSocial.NAME_OK;
        } else if (AppSocial.ID_TW == socialId) {
            result = AppSocial.NAME_TW;
        } else if (AppSocial.ID_GP == socialId) {
            result = AppSocial.NAME_GP;
        }
        return result;
    }

    public static String getStatisticsKey(int socialId) {
        switch (socialId) {
            case ID_FB:
                return "FB";
            case ID_VK:
                return "VK";
            case ID_TW:
                return "TW";
            case ID_OK:
                return "OK";
            case ID_GP:
                return "GP";
        }
        return null;
    }
}
