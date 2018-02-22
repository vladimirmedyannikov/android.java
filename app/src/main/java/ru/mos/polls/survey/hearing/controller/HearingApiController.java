package ru.mos.polls.survey.hearing.controller;


import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.api.API;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.common.model.QuestMessage;

/**
 * Контроллер, инкапсулирующи логику работы с API слушаний
 * Полноценно пока (23.04.2015) не используется, используется лишь отдельный методы для привязки аккаунта АГ к аккаунту ПГУ
 *
 * @since 1.8
 */
@Deprecated
public abstract class HearingApiController {
    /**
     * Возможные коды ошибок возникающие при вызове метода hearingCheck()
     * <p/>
     * 1) Если при проверке по master_sso ПГУ ответил отрицательно (т.е. не хватает данных), вернет ошибку 100403
     * с текстом "Необходима привязка пользователя портала Гос. услуг."
     * 2) Если slave_sso не имеет права к подобным публикациям, то возвращается ошибка 100500,
     * с различными вариантами текстовых ответов, например "Привязанный пользователь имеет незаполненные поля имени и фамилии"
     */
    public static final int ERROR_CODE_NO_MASTER_SSO_ID = 5705;
    public static final int ERROR_CODE_NOT_ACCESS_FOR_SLAVE_SSO_ID = 100500;
    public static final int ERROR_FIELDS_ARE_EMPTY = 5703;
    public static final int ERROR_SESSION_EXPIRED = 5732;
    public static final int ERROR_PGU_FLAT_NOT_MATCH = 5734;
    public static final int ERROR_AG_FLAT_NOT_MATCH = 15165;
    public static final int ERROR_PGU_NOT_ATTACHED = 15167;
    public static final int ERROR_PGU_FLAT_NOT_VALID = 15163;
    public static final int ERROR_PGU_USER_DATA = 15164;
    public static final int ERROR_PGU_SESSION_EXPIRED = 15166;

    public static void pguBind(final BaseActivity elkActivity, String pguLogin, String pguPassword, final PguAuthListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.PGU, UrlManager.Methods.BINDING));
        pguCheck(elkActivity, url, pguLogin, pguPassword, listener);
    }

    public static void pguAuth(final BaseActivity elkActivity, String pguLogin, String pguPassword, final PguAuthListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.PGU, UrlManager.Methods.AUTH));
        pguCheck(elkActivity, url, pguLogin, pguPassword, listener);
    }

    /**
     * Авторизация пользователя в pgu
     *
     * @param elkActivity
     * @param pguLogin    логин пользвоателя на сайте pgu
     * @param pguPassword пароль пользователя на сайте pgu
     * @param listener    callback обработка результата авторизации
     */
    public static void pguCheck(final BaseActivity elkActivity, String url, String pguLogin, String pguPassword, final PguAuthListener listener) {

        JSONObject requestJson = new JSONObject();
        try {
            JSONObject authPguJson = new JSONObject();
            authPguJson.put("sudir_login", pguLogin);
            authPguJson.put("password", pguPassword);
            requestJson.put("auth", authPguJson);
            requestJson.put("current_session", Session.getSession(elkActivity));
        } catch (JSONException ignored) {
        }
        /**
         * в ходе запроса как обычно приходит сессия,
         * но это сессия пгу, восстанавливаем сессию аг
         */
        final String agSessionId = Session.getSession(elkActivity);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Session.setSession(agSessionId);
                AgUser.setPguConnected(elkActivity);
                if (listener != null) {
                    QuestMessage questMessage = new QuestMessage(jsonObject);
                    int percent = jsonObject.optInt("percent_fill_profile");
                    listener.onSuccess(questMessage, percent);
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                Session.setSession(agSessionId);
                if (listener != null) {
                    listener.onError(volleyError.getMessage());
                }
            }
        };
        elkActivity.addRequest(new JsonObjectRequest(url, requestJson, responseListener, errorListener));
    }

    /**
     * Сообщение о желании пройти / прохождение собрания для слушания
     *
     * @param elkActivity
     * @param hearingId   голосование слушания
     * @param meetingId   идентификтаор собрания
     * @param listener    callback обработки результатов оповещения
     */
    public static void hearingCheck(final BaseActivity elkActivity, long hearingId, long meetingId, final HearingCheckListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.HEARING_CHECK));
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("hearing_id", hearingId);
            requestJson.put("meeting_id", meetingId);
            /**
             * передаем сессию
             */
            JSONObject authJson = new JSONObject();
            authJson.put(Session.SESSION_ID, Session.getSession(elkActivity));
            requestJson.put(Session.AUTH, authJson);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    JSONObject json = jsonObject.optJSONObject("message");
                    String title = json.optString("title");
                    String message = json.optString("text");
                    if (listener != null) {
                        listener.onSuccess(title, message);
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (listener != null) {
                    switch (volleyError.getErrorCode()) {
                        case ERROR_CODE_NO_MASTER_SSO_ID:
                        case ERROR_SESSION_EXPIRED:
                        case ERROR_FIELDS_ARE_EMPTY:
                        case ERROR_PGU_FLAT_NOT_MATCH:
                        case ERROR_AG_FLAT_NOT_MATCH:
                        case ERROR_PGU_NOT_ATTACHED:
                        case ERROR_PGU_FLAT_NOT_VALID:
                        case ERROR_PGU_USER_DATA:
                        case ERROR_PGU_SESSION_EXPIRED:
                            listener.onPguAuthError(volleyError.getErrorCode(), volleyError.getMessage());
                            break;
                        default:
                            listener.onError(volleyError.getMessage());
                            break;
                    }
                }
            }
        };
        elkActivity.addRequest(new JsonObjectRequest(url, requestJson, responseListener, errorListener));
    }

    public interface HearingCheckListener {
        void onSuccess(String title, String message);

        void onPguAuthError(int code, String message);

        void onError(String message);
    }

    public interface PguAuthListener {
        void onSuccess(QuestMessage questMessage, int percent);

        void onError(String error);
    }
}
