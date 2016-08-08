package ru.mos.polls.social.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.google.android.gms.plus.PlusShare;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mos.polls.R;
import ru.mos.polls.social.auth.GpHelper;
import ru.mos.polls.social.controller.AgSocialApiController;
import ru.mos.polls.social.model.Error;
import ru.mos.polls.social.model.SocialPostValue;
import ru.mos.polls.social.model.TokenData;
import ru.ok.android.sdk.Odnoklassniki;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

/**
 * Класс для хранения данных социальных сетей, сохранненые данные в ряде случаев позволяют исключить повторные процедуры авторизации,
 * получения нужных url и прочее. Все данные храняться в SharedPreferences.
 * Содержит наборы классов клиентов, реализующие работы с каждой социальной сетью
 */
public class SocialManager {

    public static final String PREFS = "social_prefs";

    public static final String FB_ACCESS_TOKEN = "fb_access_token";
    public static final String FB_REFRESH_TOKEN = "fb_refresh_token";
    public static final String FB_EXPIRE_TIME = "fb_expire_time";
    public static final String FB_IS_LOGON = "fb_is_logon";

    public static final String VK_ACCESS_TOKEN = "vk_access_token";
    public static final String VK_REFRESH_TOKEN = "vk_refresh_token";
    public static final String VK_EXPIRE_TIME = "vk_expire_time";
    public static final String VK_IS_LOGON = "vk_is_logon";

    public static final String TW_ACCESS_TOKEN = "tw_access_token";
    public static final String TW_REFRESH_TOKEN = "tw_refresh_token";
    public static final String TW_EXPIRE_TIME = "tw_expire_time";
    public static final String TW_IS_LOGON = "tw_is_logon";

    /**
     * Данный тип токена используем для привязки профиля ОК к профилю АГ
     * {@link ru.mos.polls.social.controller.SocialController#bindSocial(ru.mos.polls.social.model.Social, AgSocialApiController.SaveSocialListener)}
     */
    public static final String OK_ACCESS_TOKEN = "ok_access_token";
    public static final String OK_REFRESH_TOKEN = "ok_refresh_token";
    public static final String OK_EXPIRE_TIME = "ok_expire_time";
    public static final String OK_IS_LOGON = "ok_is_logon";
    /**
     * В связи с изменениями в апи одноклассников теперь получаем oauth токен и session_secret_key
     * и используем для постинга {@link ru.mos.polls.social.controller.SocialController#post(SocialPostValue)}
     *
     * @since 1.9.4
     */
    public static final String OK_OAUTH_TOKEN = "ok_oauth_token";
    public static final String OK_SESSION_SECRET_TOKEN = "ok_session_secret_token";

    public static final String GP_ACCESS_TOKEN = "gp_access_token";
    public static final String GP_REFRESH_TOKEN = "gp_refresh_token";
    public static final String GP_EXPIRE_TIME = "gp_expire_time";
    public static final String GP_IS_LOGON = "gp_is_logon";

    public static final String GP_ACCOUNT_NAME = "gp_account_name";

    public static final int SOCIAL_ID_FB = 1;
    public static final int SOCIAL_ID_VK = 2;
    public static final int SOCIAL_ID_TW = 3;
    public static final int SOCIAL_ID_OK = 4;
    public static final int SOCIAL_ID_GP = 5;

    public static final String SOCIAL_NAME_FB = "fb";
    public static final String SOCIAL_NAME_VK = "vk";
    public static final String SOCIAL_NAME_TW = "twitter";
    public static final String SOCIAL_NAME_OK = "ok";
    public static final String SOCIAL_NAME_GP = "gp";

    public static final String AVATAR_URL_FB = "avatar_url_fb";
    public static final String AVATAR_URL_VK = "avatar_url_vk";
    public static final String AVATAR_URL_TW = "avatar_url_tw";
    public static final String AVATAR_URL_OK = "avatar_url_ok";
    public static final String AVATAR_URL_GP = "avatar_url_gp";

    public static final String USER_ID_VK = "user_id_vk";

    /**
     * Получение токена доступа социальной сети
     *
     * @param context
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @return - строковое представление токена доступа
     */
    public static String getAccessToken(Context context, int socialId) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(getAccessTokenKey(socialId), null);
    }

    /**
     * Получение токена для обновления данных
     *
     * @param context
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @return - строковое представление токена доступа
     */
    public static String getRefreshToken(Context context, int socialId) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(getRefreshTokenKey(socialId), null);
    }

    public static boolean isLogon(Context context, int socialId) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(getLogonKey(socialId), false);
    }

    /**
     * Сохранение токена доступа социальной сети
     *
     * @param context
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     */
    public static void setAccessToken(Context context, final int socialId, String accessToken) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (accessToken != null) {
            prefs.edit().putString(getAccessTokenKey(socialId), accessToken).commit();
        }
    }

    /**
     * Сохранение oauth токена для одноклассников
     * Используется для постинга в ОК {@link ru.mos.polls.social.controller.SocialController#post(SocialPostValue)}
     *
     * @param context    текущий контест
     * @param oauthToken токен одноклассников для oauth атворизации
     * @since 1.9.4
     */
    public static void setOauthOkToken(Context context, String oauthToken) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(OK_OAUTH_TOKEN, oauthToken).commit();
    }

    /**
     * Сохранение oauth секретного токена для одноклассников
     * Используется для постинга в ОК {@link ru.mos.polls.social.controller.SocialController#post(SocialPostValue)}
     *
     * @param context                 текущий контест
     * @param oauthSessionSecretToken токен одноклассников для oauth атворизации
     * @since 1.9.4
     */
    public static void setOauthOkSessionSecretToken(Context context, String oauthSessionSecretToken) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(OK_SESSION_SECRET_TOKEN, oauthSessionSecretToken).commit();
    }

    /**
     * Используется для постинга в ОК {@link ru.mos.polls.social.controller.SocialController#post(SocialPostValue)}
     *
     * @param context текущий контекст
     * @return токен
     */
    public static String getOauthOkToken(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(OK_OAUTH_TOKEN, "");
    }

    /**
     * Используется для постинга в ОК {@link ru.mos.polls.social.controller.SocialController#post(SocialPostValue)}
     *
     * @param context текущий контекст
     * @return токен
     * @since 1.9.4
     */
    public static String getOauthOkSessionSecretToken(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(OK_SESSION_SECRET_TOKEN, "");
    }

    /**
     * Удаление токенов oauth для одноклассников
     *
     * @param context текущий контекст
     * @since 1.9.4
     */
    public static void clearOkOauthToken(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(OK_OAUTH_TOKEN)
                .remove(OK_SESSION_SECRET_TOKEN)
                .apply();
    }

    /**
     * Проверка наличия токенов oauth для одноклассников
     *
     * @param context текущий контекст
     * @return true - токены есть
     * @since 1.9.4
     */
    public static boolean isOauthOkTokensExists(Context context) {
        String oauthToken = getOauthOkToken(context);
        String oauthSessionSecretToken = getOauthOkSessionSecretToken(context);
        return !TextUtils.isEmpty(oauthToken) && !TextUtils.isEmpty(oauthSessionSecretToken);
    }

    /**
     * Сохранение токена для обновления социальной сети
     *
     * @param context
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     */
    public static void setRefreshToken(Context context, int socialId, String refreshToken) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(getRefreshTokenKey(socialId), refreshToken).commit();
    }

    public static void setLogon(Context context, int socialId, boolean isLogon) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(getLogonKey(socialId), isLogon).commit();
    }

    /**
     * Сохранение идентификатора пользователя для ВК (использутеся для получения url аватара пользователя)
     *
     * @param context
     * @param userId  - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     */
    public static void setUserIdVk(Context context, String userId) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(USER_ID_VK, userId).commit();
    }

    /**
     * Проверка, действителен ли сохраненный токен доступа
     *
     * @param context
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @return
     */
    public static boolean isExpired(Context context, int socialId) {
        if (socialId == SOCIAL_ID_VK) {
            return false; // проверка socialId == SOCIAL_ID_VK добавлена, так как для вк при авторизации используется разрешение offline (http://vk.com/dev/permissions), то expires_in = 0, а токен бессрочный
        }
        final long currentTime = System.currentTimeMillis();
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        final long expireTime = prefs.getLong(getExpireDateKey(socialId), -1L);
        return expireTime != -1 && expireTime < currentTime;
    }

    public static long getExpired(Context context, int socialId) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        final long expireTime = prefs.getLong(getExpireDateKey(socialId), -1L);
        return expireTime;
    }

    /**
     * Сохранение времени жизни токена
     *
     * @param context
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @param expired  - время жизни токена
     */
    public static void setExpired(Context context, int socialId, long expired) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (expired != -1l) {
            expired += System.currentTimeMillis();
        }
        prefs.edit().putLong(getExpireDateKey(socialId), expired).commit();
    }

    /**
     * Получение сохраненного идентификатора пользователя для ВК
     *
     * @param context
     * @return -строковое представление идентификатора пользоветля
     */
    public static String getUserIdVk(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(USER_ID_VK, "");
    }

    /**
     * Сохранение url аватара для указанной социальной сети
     *
     * @param context
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @param url      - строка url аватара
     */
    public static void setAvatarUrl(Context context, int socialId, String url) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(getAvatarName(socialId), url).commit();
    }

    /**
     * Получение сохраненного url аватара для указанной социальной сети
     *
     * @param context
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @return - сирока url аватара
     */
    public static String getAvatarUrl(Context context, int socialId) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(getAvatarName(socialId), "");
    }

    /**
     * Сохранение имени аккаунта для сети Google+ (имя аккаунта используется для получения токена доступа)
     *
     * @param context
     * @return - строка имени аккаунта
     */
    public static String getGpAccountName(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(GP_ACCOUNT_NAME, null);
    }

    /**
     * Получение сохраненного имени аккаунта для Google+
     *
     * @param context
     * @param accountName - строка имени аккаунта
     */
    public static void setGpAccountName(Context context, String accountName) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(GP_ACCOUNT_NAME, accountName).commit();
    }

    /**
     * Метод получения имени параметра сохранения ссылки на аватарку в зависимости от идентификатора социальной сети
     *
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @return -строка имени параметра сохранения в SharedPreferences
     */
    private static String getAvatarName(int socialId) {
        switch (socialId) {
            case SOCIAL_ID_FB:
                return AVATAR_URL_FB;
            case SOCIAL_ID_VK:
                return AVATAR_URL_VK;
            case SOCIAL_ID_TW:
                return AVATAR_URL_TW;
            case SOCIAL_ID_OK:
                return AVATAR_URL_OK;
            case SOCIAL_ID_GP:
                return AVATAR_URL_GP;
            default:
                throw new IllegalArgumentException("Unknown social id: " + socialId);
        }
    }

    /**
     * Метод удаления сохраненных данных (accessToken, refreshToken, expireIn) для указанной социальной сети
     *
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     */
    public static void clearAuth(Context context, int socialId) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().
                remove(getAccessTokenKey(socialId)).
                remove(getRefreshTokenKey(socialId)).
                remove(getExpireDateKey(socialId)).commit();
        if (socialId == SOCIAL_ID_OK) {
            clearOkOauthToken(context);
        }
    }

    /**
     * Метод получения имени параметра сохранения токена доступа в зависимости от идентификатора социальной сети
     *
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @return -строка имени параметра сохранения в SharedPreferences
     */
    private static String getAccessTokenKey(int socialId) {
        switch (socialId) {
            case SOCIAL_ID_FB:
                return FB_ACCESS_TOKEN;
            case SOCIAL_ID_VK:
                return VK_ACCESS_TOKEN;
            case SOCIAL_ID_TW:
                return TW_ACCESS_TOKEN;
            case SOCIAL_ID_OK:
                return OK_ACCESS_TOKEN;
            case SOCIAL_ID_GP:
                return GP_ACCESS_TOKEN;
            default:
                throw new IllegalArgumentException("Unknown social id: " + socialId);
        }
    }

    private static String getLogonKey(int socialId) {
        switch (socialId) {
            case SOCIAL_ID_FB:
                return FB_IS_LOGON;
            case SOCIAL_ID_VK:
                return VK_IS_LOGON;
            case SOCIAL_ID_TW:
                return TW_IS_LOGON;
            case SOCIAL_ID_OK:
                return OK_IS_LOGON;
            case SOCIAL_ID_GP:
                return GP_IS_LOGON;
            default:
                throw new IllegalArgumentException("Unknown social id: " + socialId);
        }
    }

    public static String getStatisticsKey(int socialId) {
        switch (socialId) {
            case SOCIAL_ID_FB:
                return "FB";
            case SOCIAL_ID_VK:
                return "VK";
            case SOCIAL_ID_TW:
                return "TW";
            case SOCIAL_ID_OK:
                return "OK";
            case SOCIAL_ID_GP:
                return "GP";
        }

        return null;
    }

    /**
     * Метод получения имени параметра сохранения токена в зависимости от идентификатора социальной сети
     *
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @return -строка имени параметра сохранения в SharedPreferences
     */
    private static String getRefreshTokenKey(int socialId) {
        switch (socialId) {
            case SOCIAL_ID_FB:
                return FB_REFRESH_TOKEN;
            case SOCIAL_ID_VK:
                return VK_REFRESH_TOKEN;
            case SOCIAL_ID_TW:
                return TW_REFRESH_TOKEN;
            case SOCIAL_ID_OK:
                return OK_REFRESH_TOKEN;
            case SOCIAL_ID_GP:
                return GP_REFRESH_TOKEN;

            default:
                throw new IllegalArgumentException("Unknown social id: " + socialId);
        }
    }

    /**
     * Метод получения имени параметра сохранения времени жизни токена в зависимости от идентификатора социальной сети
     *
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @return -строка имени параметра сохранения в SharedPreferences
     */
    private static String getExpireDateKey(int socialId) {
        switch (socialId) {
            case SOCIAL_ID_FB:
                return FB_EXPIRE_TIME;
            case SOCIAL_ID_VK:
                return VK_EXPIRE_TIME;
            case SOCIAL_ID_TW:
                return TW_EXPIRE_TIME;
            case SOCIAL_ID_OK:
                return OK_EXPIRE_TIME;
            case SOCIAL_ID_GP:
                return GP_EXPIRE_TIME;
            default:
                throw new IllegalArgumentException("Unknown social id: " + socialId);
        }
    }

    /**
     * Метод получения имени имени социальной сети в зависимости от идентификатора социальной сети
     *
     * @param socialId - идентификатор социальной сети, определеяющий какой токен доступа запрашивается
     * @return -строка имени параметра сохранения в SharedPreferences
     */
    public static String getSocialName(int socialId) {
        switch (socialId) {
            case SOCIAL_ID_FB:
                return SOCIAL_NAME_FB;
            case SOCIAL_ID_VK:
                return SOCIAL_NAME_VK;
            case SOCIAL_ID_TW:
                return SOCIAL_NAME_TW;
            case SOCIAL_ID_OK:
                return SOCIAL_NAME_OK;
            case SOCIAL_ID_GP:
                return SOCIAL_NAME_GP;
            default:
                throw new IllegalArgumentException("Unknown social id: " + socialId);
        }
    }

    public static int getSocialId(String socialName) {
        int result = -1;
        if (SOCIAL_NAME_FB.equalsIgnoreCase(socialName)) {
            result = SOCIAL_ID_FB;
        } else if (SOCIAL_NAME_VK.equalsIgnoreCase(socialName)) {
            result = SOCIAL_ID_VK;
        } else if (SOCIAL_NAME_OK.equalsIgnoreCase(socialName)) {
            result = SOCIAL_ID_OK;
        } else if (SOCIAL_NAME_TW.equalsIgnoreCase(socialName)) {
            result = SOCIAL_ID_TW;
        } else if (SOCIAL_NAME_GP.equalsIgnoreCase(socialName)) {
            result = SOCIAL_ID_GP;
        }

        return result;
    }

    public static void post(Context context, SocialPostValue socialPostValue) throws Exception {
        int socialId = socialPostValue.getSocialId();
        /**
         * получаем токен
         */
        final String accessToken = SocialManager.getAccessToken(context, socialId);
        final String refreshToken = SocialManager.getRefreshToken(context, socialId);
        TokenData tokenData = new TokenData(accessToken, refreshToken);
        /**
         * Инициализируем клиента соц сети и выполняем пост
         */
        PostFactory.createPost(context, socialId).post(tokenData, socialPostValue);
    }

    public static String createAvatarUrl(Context context, ru.mos.polls.social.model.Social social) throws Exception {
        return PostFactory
                .createPost(context, social.getSocialId())
                .getAvatarUrl(social.getTokenData());
    }

    public static void clearAll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
        CookieSyncManager.createInstance(context); //на андроиде 10 если в браузер до логаута не заходили, то CookieManager.getInstance() упадет с IllegalStateExeption
        CookieManager.getInstance().removeAllCookie(); //authdata is stored in cookies for webauthorization
    }

    static class PostFactory {

        static Social createPost(Context context, int socialId) {
            switch (socialId) {
                case SOCIAL_ID_FB:
                    return new FbSocial(context);
                case SOCIAL_ID_TW:
                    return new TwSocial(context);
                case SOCIAL_ID_VK:
                    return new VkSocial(context);
                case SOCIAL_ID_OK:
                    return new OkSocial(context);
                case SOCIAL_ID_GP:
                    return new GpSocial(context);
                default:
                    throw new IllegalArgumentException("Unknown social id: " + socialId);
            }
        }
    }
}

/**
 * Интерфес для работы с соцсетями
 */
interface Social {
    void post(TokenData tokenData, SocialPostValue socialPostValue) throws Exception;

    String getAvatarUrl(TokenData tokenData) throws Exception;
}

abstract class AbsSocial implements Social {
    protected final Context context;

    AbsSocial(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    protected void errorToLog(String error) {
        Log.e(Error.POSTING_ERROR, error);
    }
}

/**
 * Клиент для работы с Facebook.com
 * Работает на основе facebook sdk
 */
class FbSocial extends AbsSocial {
    public static final String URL_FB_USER = "http://graph.facebook.com/%s/picture";

    FbSocial(Context context) {
        super(context);
    }

    @Override
    public void post(TokenData tokenData, SocialPostValue socialPostValue) throws Exception {
        Session session = Session.getActiveSession();
        if (session == null) {
            session = Session.openActiveSessionFromCache(getContext());
            Session.setActiveSession(session);
        }

        Request request = new Request(Session.getActiveSession(), "me/feed", socialPostValue.getFbParams(), HttpMethod.POST);
        final Response response = request.executeAndWait();
        if (response.getError() != null) {
            errorToLog("FB " + response.getError().toString());
            throw new Exception(String.valueOf(response.getError().getErrorCode()));
        }
    }

    @Override
    public String getAvatarUrl(TokenData tokenData) throws Exception {
        Session session = Session.getActiveSession();
        if (session == null) {
            session = Session.openActiveSessionFromCache(getContext());
            Session.setActiveSession(session);
        }

        final Response response = Request.newMeRequest(session, null).executeAndWait();
        String id = response.getGraphObject().getInnerJSONObject().getString("id");
        if (response.getError() != null) {
            errorToLog("FB avatar " + response.getError().getErrorMessage());
            throw new Exception(response.getError().getErrorMessage());
        }
        return String.format(URL_FB_USER, id);
    }
}

/**
 * Клиент для работы с twetter
 * Работа построена на основе twitter4j
 */
class TwSocial extends AbsSocial {

    TwSocial(Context context) {
        super(context);
    }

    @Override
    public void post(TokenData tokenData, SocialPostValue socialPostValue) throws Exception {
        final Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(
                getContext().getString(R.string.tw_consumer_key),
                getContext().getString(R.string.tw_consumer_secret)
        );
        twitter.setOAuthAccessToken(new AccessToken(tokenData.getAccessToken(), tokenData.getRefreshToken()));
        try {
            twitter.updateStatus(socialPostValue.getText().trim());
        } catch (TwitterException e) {
            /**
             * TwitterException не реализует Serializable
             * поэтому явно упаковываем TwitterException в Exception
             */
            errorToLog("TW " + e.toString());
            throw new Exception(String.valueOf(e.getErrorCode()));
        }
    }

    @Override
    public String getAvatarUrl(TokenData tokenData) {
        final Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(
                getContext().getString(R.string.tw_consumer_key),
                getContext().getString(R.string.tw_consumer_secret)
        );
        AccessToken accessToken = new AccessToken(tokenData.getAccessToken(), tokenData.getRefreshToken());
        twitter.setOAuthAccessToken(accessToken);
        User user = null;
        try {
            user = twitter.showUser(accessToken.getUserId());
        } catch (TwitterException e) {
            errorToLog("TW avatar" + e.toString());
        }
        return user == null ? "" : user.getMiniProfileImageURL();
    }
}

/**
 * Клиент для работы с ВК
 * Работает напрямую, не используя vk sdk
 */
class VkSocial extends AbsSocial {
    public static final String URL_USER = "https://api.vk.com/method/users.get?";
    public static final String URL_POST = "https://api.vk.com/method/wall.post?";

    VkSocial(Context context) {
        super(context);
    }

    @Override
    public void post(TokenData tokenData, SocialPostValue socialPostValue) throws Exception {
        final HttpClient client = new DefaultHttpClient();
        final HttpGet get = new HttpGet(URL_POST + URLEncodedUtils.format(socialPostValue.getVkParams(tokenData), "UTF-8"));
        final HttpResponse response = client.execute(get);
        JSONObject responseJson = new JSONObject(getContent(response.getEntity()));
        if (responseJson.has("error")) {
            responseJson = responseJson.optJSONObject("error");
            errorToLog("VK " + responseJson.toString());
            throw new Exception(String.valueOf(responseJson.optInt("error_code")));
        }
    }

    @Override
    public String getAvatarUrl(TokenData tokenData) throws Exception {
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_ids", SocialManager.getUserIdVk(context)));
        params.add(new BasicNameValuePair("fields", "photo_50"));
        params.add(new BasicNameValuePair("v", "5.21"));

        final HttpClient client = new DefaultHttpClient();
        final HttpGet get = new HttpGet(URL_USER + URLEncodedUtils.format(params, "UTF-8"));
        final HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            errorToLog("VK avatar" + response.getStatusLine().getReasonPhrase());
            throw new Exception(response.getStatusLine().getReasonPhrase());
        }
        JSONObject jsonObject = new JSONObject(getContent(response.getEntity())).getJSONArray("response").getJSONObject(0);
        return jsonObject.getString("photo_50");
    }

    private String getContent(HttpEntity httpEntity) throws IOException {
        InputStreamReader is = new InputStreamReader(httpEntity.getContent());
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();
        while (read != null) {
            sb.append(read);
            read = br.readLine();
        }
        return sb.toString();
    }
}

/**
 * Клиент для работы с одноклассниками
 * Работает на основе sdk ok
 */
class OkSocial extends AbsSocial {

    private final Map<String, String> params;
    private final Odnoklassniki ok;

    OkSocial(Context context) {
        super(context);
        params = new HashMap<String, String>();
        if (Odnoklassniki.hasInstance()) {
            ok = Odnoklassniki.getInstance(context);
        } else {
            final String id = context.getString(R.string.ok_app_id);
            final String secret = context.getString(R.string.ok_app_secret);
            final String key = context.getString(R.string.ok_app_key);
            ok = Odnoklassniki.createInstanceForTokens(context.getApplicationContext(), id, secret, key);
        }
    }

    @Override
    public void post(TokenData tokenData, SocialPostValue socialPostValue) throws Exception {
// использовалось при старом сдк, пока оставим
//        ok.refreshToken(getContext());
//        String response = ok.request("mediatopic.post", socialPostValue.getOkAttachments(), "post");
//        /**
//         * реализация для старого апи
//         */
////        String response = ok.request("share.addLink", socialPostValue.getOkParams(), "post");
//        JSONObject responseJson = new JSONObject(response);
//        String errorCode = responseJson.optString("error_code");
//        if (!"".equalsIgnoreCase(errorCode)) {
//            errorToLog("OK " + responseJson.toString());
//            throw new Exception(String.valueOf(errorCode));
//        }
    }

    @Override
    public String getAvatarUrl(TokenData tokenData) throws Exception {
// использовалось при старом сдк, пока оставим
//        params.put("fields", "pic240min");
//
//        ok.refreshToken(getContext());
//        String response = ok.request("users.getCurrentUser", params, "post");
//        JSONObject responseJson = new JSONObject(response);
//        String errorCode = responseJson.optString("error_code");
//        if (!"".equalsIgnoreCase(errorCode)) {
//            errorToLog("OK avatar" + responseJson.optString("error_msg") + responseJson.optString("error_code"));
//            throw new Exception(responseJson.optString("error_msg"));
//        }
//        return responseJson.getString("pic240min");
        return null;
    }
}


/**
 * Клиент для работы с Google+
 * Получения аватарки работает напрямую, без использования GooglePlus sdk
 * Постинг рабоатет через официальное приложение Google+
 */
class GpSocial extends AbsSocial {
    private static final String PROFILE_URL = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=%s";

    GpSocial(Context context) {
        super(context);
    }

    @Override
    public void post(TokenData tokenData, SocialPostValue socialPostValue) throws Exception {
        if (GpHelper.isGooglePlusInstalled(context)) { //Вообще нужно воспользоваться классом GooglePlusUtil, но его вроде исключили из play services.. int errorCode = GooglePlusUtil.checkGooglePlusApp(this); if (errorCode != GooglePlusUtil.SUCCESS) { GooglePlusUtil.getErrorDialog(errorCode, this, 0).show(); }
            googlePlusPost(socialPostValue);
//            getAvatarUrl(new TokenData(SocialManager.getAccessToken(context, SocialManager.SOCIAL_ID_GP), "")); //для теста
        } else {
            GpHelper.setErrorDialog(context);
        }
    }

    @Override
    public String getAvatarUrl(TokenData tokenData) throws Exception {
        //получаем токен
        String accountName = SocialManager.getGpAccountName(context);
        String accessToken = GpHelper.getAccessToken(context, accountName);
        final HttpClient client = new DefaultHttpClient();
        //получаем url аватара
        String url = String.format(PROFILE_URL, accessToken); //игнорируем данные из SocialManager, так как токен там может быть просрочен
        final HttpGet get = new HttpGet(url);
        final HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new Exception(response.getStatusLine().getReasonPhrase());
        }
        String jsonResponse = getContent(response.getEntity());
        JSONObject jsonObjectResponse = new JSONObject(jsonResponse);
        return jsonObjectResponse.getString("picture");
    }

    private String getContent(HttpEntity httpEntity) throws IOException {
        InputStreamReader is = new InputStreamReader(httpEntity.getContent());
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();
        while (read != null) {
            sb.append(read);
            read = br.readLine();
        }
        return sb.toString();
    }

    private void googlePlusPost(SocialPostValue socialPostValue) {
        Intent shareIntent = new PlusShare.Builder(context)
                .setType("text/plain")
                .setText(socialPostValue.getText() + "\n" + socialPostValue.getImage())
                .setContentUrl(Uri.parse(socialPostValue.getLink()))
                .getIntent();
        ((Activity) context).startActivityForResult(shareIntent, 0);
    }
}

