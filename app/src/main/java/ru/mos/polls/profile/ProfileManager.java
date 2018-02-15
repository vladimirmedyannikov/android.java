package ru.mos.polls.profile;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.AgAuthActivity;
import ru.mos.polls.R;
import ru.mos.polls.util.Dialogs;
import ru.mos.polls.db.UserData;
import ru.mos.polls.db.UserDataProvider;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.netframework.request.StringRequest;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.api.API;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.push.GCMBroadcastReceiver;

/**
 * Менеджер для работы сервисами профиля пользователя аг
 *
 * @since 1.9
 * on 24.02.15.
 */
public abstract class ProfileManager {
    /**
     * Получение данных пользователя
     *
     * @param elkActivity
     * @param agUserListener
     */
    public static void getProfile(final BaseActivity elkActivity, final AgUserListener agUserListener) {
        getProfile(elkActivity, null, agUserListener, false);
    }

    /**
     * Получение данных пользователя
     *
     * @param elkActivity
     * @param requestBody    тело запроса, используется после авторизации
     * @param agUserListener
     */
    public static void getProfile(final BaseActivity elkActivity, JSONObject requestBody, final AgUserListener agUserListener, final boolean isConfirm) {
        String url = API.getURL(API.url(API.Controller.AGPROFILE, API.Methods.GET_PROFILE));
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonUserData) {
                AgUser result = null;
                if (jsonUserData != null) {
                    result = new AgUser(elkActivity, jsonUserData);
                    result.save(elkActivity);
                }
                if (agUserListener != null) {
                    agUserListener.onLoaded(result);
                }
            }

        };

        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, 0) {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isConfirm) super.onErrorResponse(error);
                if (agUserListener != null) {
                    agUserListener.onError(error);
                }
            }
        };

        if (requestBody == null) {
            requestBody = new JSONObject();
            Session.addSession(requestBody);
        }
        elkActivity.addRequest(new JsonObjectRequest(url, requestBody, responseListener, errorListener));
    }

    /**
     * Сохранение данных пользователя аг на сервере
     *
     * @param elkActivity
     * @param requestBody        данные для обновления
     * @param saveAgUserListener
     */
    public static void setProfile(final BaseActivity elkActivity, JSONObject requestBody, final SaveAgUserListener saveAgUserListener) {
        String url = API.getURL(API.url(API.Controller.AGPROFILE, API.Methods.SET_PROFILE));
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject result) {
                if (saveAgUserListener != null) {
                    saveAgUserListener.onSaved(result);
                }
            }

        };

        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, 0) {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (saveAgUserListener != null) {
                    saveAgUserListener.onError(error);
                }
            }
        };


        if (requestBody == null) {
            requestBody = new JSONObject();
        }
        Session.addSession(requestBody);

        elkActivity.addRequest(new JsonObjectRequest(url, requestBody, responseListener, errorListener));
    }

    /**
     * Удаление всех данных пользователя из локального кеша
     *
     * @param context
     */
    public static void clearStoredData(Context context) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(UserDataProvider.getContentUri(UserData.Cars.URI_CONTENT), null, null);
        cr.delete(UserDataProvider.getContentUri(UserData.Flats.URI_CONTENT), null, null);
        cr.delete(UserDataProvider.getContentUri(UserData.Subscriptions.URI_CONTENT), null, null);
        cr.delete(UserDataProvider.getContentUri(UserData.Roads.URI_CONTENT), null, null);
        cr.delete(UserDataProvider.getContentUri(UserData.Times.URI_CONTENT), null, null);
        cr.delete(UserDataProvider.getContentUri(UserData.RoadGroups.URI_CONTENT), null, null);

        SharedPreferences prefs = context.getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE);
        String phone = prefs.getString(AgUser.PHONE, null);
        prefs.edit().clear().putString(AgUser.PHONE, phone).commit();
    }

    /**
     * @param alkActivity
     * @param authActivity
     * @param afterLoginActivity
     */
    public static void logOut(final BaseActivity alkActivity, final Class<?> authActivity, final Class<?> afterLoginActivity) {
        final ProgressDialog dialog = Dialogs.showProgressDialog(alkActivity, R.string.elk_wait_logout);
        JSONObject params = new JSONObject();
        try {
            params.put("auth", new JSONObject().put("logout", true));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.Listener<String> listener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                afterLoggedOut(alkActivity, authActivity, afterLoginActivity);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                afterLoggedOut(alkActivity, authActivity, afterLoginActivity);
            }
        };
        alkActivity.addRequest(new StringRequest(API.getURL("json/v0.2/auth/user/logout"), params, listener, errorListener), dialog);
    }

    public static void afterLoggedOut(Activity elkActivity, Class<?> authActivity, Class<?> afterLoginActivity) {
        Intent intent = new Intent(elkActivity, authActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AgAuthActivity.PASSED_ACTIVITY, afterLoginActivity);
        elkActivity.sendBroadcast(new Intent(BaseActivity.INTENT_LOGOUT));
        elkActivity.startActivity(intent);
        elkActivity.finish();

        NotificationManager notificationmanager = (NotificationManager) elkActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            for (int i = 0; i <= GCMBroadcastReceiver.messageNotifyId; ++i) {
                notificationmanager.cancel(i);
            }
        } catch (Exception ignored) {
        }

        ProfileManager.clearStoredData(elkActivity);

        Session.setSession(null);
    }

    public interface AgUserListener {
        void onLoaded(AgUser agUser);

        void onError(VolleyError error);
    }

    public interface SaveAgUserListener {
        void onSaved(JSONObject resultJson);

        void onError(VolleyError error);
    }
}
