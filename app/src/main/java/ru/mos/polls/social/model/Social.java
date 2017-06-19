package ru.mos.polls.social.model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.social.manager.SocialManager;

/**
 * Структура для хранения данных соцсети
 */
public class Social implements Serializable {
    private int socialId;
    private String socialName;
    private String icon;
    private TokenData tokenData;
    private long expired;
    private boolean isLogon;

    /**
     * Получаем сохраненную соцсеть
     *
     * @param context
     * @param socialId
     * @return
     */
    public static Social fromPreference(Context context, int socialId) {
        Social result = new Social();
        result.socialId = socialId;
        result.socialName = SocialManager.getSocialName(socialId);
        result.icon = SocialManager.getAvatarUrl(context, socialId);
        String accessToken = SocialManager.getAccessToken(context, socialId);
        String refreshToken = SocialManager.getRefreshToken(context, socialId);
        result.tokenData = new TokenData(accessToken, refreshToken);
        result.expired = SocialManager.getExpired(context, socialId);
        result.isLogon = SocialManager.isLogon(context, socialId);
        return result;
    }

    /**
     * Получаем список сохраненных соц сетей
     *
     * @param context
     * @return
     */
    public static List<Social> getSavedSocials(Context context) {
        List<Social> socials = new ArrayList<Social>();

        Social social = fromPreference(context, SocialManager.SOCIAL_ID_FB);
        socials.add(social);

        social = fromPreference(context, SocialManager.SOCIAL_ID_VK);
        socials.add(social);

        social = fromPreference(context, SocialManager.SOCIAL_ID_TW);
        socials.add(social);

        social = fromPreference(context, SocialManager.SOCIAL_ID_OK);
        socials.add(social);

        return socials;
    }

    public Social() {
    }

    public Social(String socialName, JSONObject socialJson) {
        this.socialName = socialName;
        socialId = SocialManager.getSocialId(socialName);
        if (socialJson != null) {
            String accessToken = socialJson.optString("token1");
            String refreshToken = socialJson.optString("token2");
            tokenData = new TokenData(accessToken, refreshToken);
            icon = socialJson.optString("icon");
            isLogon = socialJson.optBoolean("logined");
            expired = socialJson.optLong("expired_time");
            /**
             * Сервер возвращает время жизни токена в секундах,
             * конвертируем в миллисекунды
             */
            if (socialId == SocialManager.SOCIAL_ID_OK && expired != -1) {
                expired *= 1000;
            }
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
            tokenJson.put("token1", tokenData.getAccessToken());
            tokenJson.put("token2", tokenData.getRefreshToken()/*!TextUtils.isEmpty(tokenData.getRefreshToken()) ? tokenData.getRefreshToken() : JSONObject.NULL*/);
            result.put(socialName, tokenJson);
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
            result.put(socialName, JSONObject.NULL);
        } catch (JSONException ignored) {
        }
        return result;
    }

    public JSONObject asKillJson() {
        JSONObject result = new JSONObject();
        try {
            JSONObject killJson = new JSONObject();
            killJson.put("kill", true);
            result.put(socialName, killJson);
        } catch (JSONException ignored) {
        }
        return result;
    }

    /**
     * Сохраняем соцсеть локально
     *
     * @param context
     */
    public void save(Context context) {
        SocialManager.setAccessToken(context, socialId, tokenData.getAccessToken());
        SocialManager.setRefreshToken(context, socialId, tokenData.getRefreshToken());
        SocialManager.setExpired(context, socialId, expired);
        SocialManager.setAvatarUrl(context, socialId, icon);
        SocialManager.setLogon(context, socialId, isLogon);
    }

    public void copy(Social otherSocial) {
        if (otherSocial != null) {
            tokenData = otherSocial.tokenData;
            expired = otherSocial.expired;
            socialId = otherSocial.socialId;
            icon = otherSocial.icon;
            isLogon = otherSocial.isLogon;
        }
    }

    /**
     * Очищаем данные соцсети
     *
     * @param context
     */
    public void clearAuth(Context context) {
        SocialManager.clearAuth(context, socialId);
    }

    public void reset(Context context) {
        clearAuth(context);
        tokenData = new TokenData(null, null);
        icon = null;
        isLogon = false;
        expired = 0;
    }

    public int getSocialId() {
        return socialId;
    }

    public String getSocialName() {
        return socialName;
    }

    public String getIcon() {
        return icon;
    }

    public TokenData getTokenData() {
        return tokenData;
    }

    public long getExpired() {
        return expired;
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

    public boolean isEmpty() {
        return (tokenData == null || tokenData.isEmpty())
                && !isLogon()
                && expired == 0;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Social) {
            Social other = (Social) o;
            result = socialId == other.socialId
                    && isLogon == other.isLogon;
        }
        return result;
    }

    public static Social findFbSocial(List<Social> socials) {
        return findSocial(socials, SocialManager.SOCIAL_ID_FB);
    }

    public static Social findVkSocial(List<Social> socials) {
        return findSocial(socials, SocialManager.SOCIAL_ID_VK);
    }

    public static Social findTwSocial(List<Social> socials) {
        return findSocial(socials, SocialManager.SOCIAL_ID_TW);
    }

    public static Social findOkSocial(List<Social> socials) {
        return findSocial(socials, SocialManager.SOCIAL_ID_OK);
    }

    public static Social findSocial(List<Social> socials, int socialId) {
        Social result = null;
        if (socials != null) {
            for (Social social : socials) {
                if (socialId == social.getSocialId()) {
                    result = social;
                    break;
                }
            }
        }
        return result;
    }

    public static boolean isEquals(List<Social> first, List<Social> second) {
        boolean result = false;
        if (first != null && second != null) {
            if (first.size() == second.size()) {
                result = true;
                for (int i = 0; i < first.size(); ++i) {
                    Social social = findSocial(second, first.get(i).getSocialId());
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
            case SocialManager.SOCIAL_ID_FB:
                return R.drawable.fb;
            case SocialManager.SOCIAL_ID_VK:
                return R.drawable.vk;
            case SocialManager.SOCIAL_ID_TW:
                return R.drawable.tw;
            case SocialManager.SOCIAL_ID_OK:
                return R.drawable.odnklsnk;
            case SocialManager.SOCIAL_ID_GP:
                return R.drawable.google;
        }
        return -1;
    }
}
