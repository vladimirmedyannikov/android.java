package ru.mos.polls.social.model;

import android.content.Context;

import ru.mos.polls.social.manager.SocialManager;


public class TokenData {

    private  String accessToken;
    private  String refreshToken;
    /**
     * Время в миллисекундах, до которого токен считается актуальным<br/>
     * Проверка актуальности токена
     */

    public TokenData(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean isEmpty() {
        return isEmpty(accessToken);
    }

    public static boolean isEmpty(Context context, int socialId) {
        String accessToken = SocialManager.getAccessToken(context, socialId);
        return isEmpty(accessToken);
    }

    private static boolean isEmpty(String target) {
        return target == null || "".equalsIgnoreCase(target.trim());
    }
}
