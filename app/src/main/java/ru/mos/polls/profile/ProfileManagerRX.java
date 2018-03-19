package ru.mos.polls.profile;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.ilich.juggler.change.Add;
import me.ilich.juggler.change.Remove;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.auth.state.AgAuthState;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.db.UserData;
import ru.mos.polls.db.UserDataProvider;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.profile.service.ProfileGet;
import ru.mos.polls.profile.service.model.EmptyResult;
import ru.mos.polls.push.GCMBroadcastReceiver;
import ru.mos.polls.push.GCMHelper;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.session.Session;
import ru.mos.polls.util.Dialogs;

public class ProfileManagerRX {

    /**
     * Получение данных пользователя
     *
     * @param agUserListener
     */
    public static void getProfile(CompositeDisposable disposable, Context context, final AgUserListener agUserListener) {
        HandlerApiResponseSubscriber<JsonObject> handler = new HandlerApiResponseSubscriber<JsonObject>(context, null) {
            @Override
            protected void onResult(JsonObject result) {
                AgUser agUser = null;
                if (result != null) {
                    try {
                        JSONObject object = new JSONObject(result.toString());
                        agUser = new AgUser(context, object);
                        agUser.save(context);
                        if (agUserListener != null) {
                            agUserListener.onLoaded(agUser);
                        }
                    } catch (JSONException e) {
                    }
                }
            }

            @Override
            public void onHasError(GeneralResponse<JsonObject> generalResponse) {
                super.onHasError(generalResponse);
                if (agUserListener != null) {
                    agUserListener.onError(generalResponse.getErrorMessage(), generalResponse.getErrorCode());
                }
            }
        };
        disposable.add(AGApplication
                .api
                .getProfile(new AuthRequest())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public static void login(CompositeDisposable disposable, Context context, ProfileGet.LoginRequest body, final AgUserListener agUserListener) {
        HandlerApiResponseSubscriber<JsonObject> handler = new HandlerApiResponseSubscriber<JsonObject>(context, null) {
            @Override
            public void onHasError(GeneralResponse<JsonObject> generalResponse) {
                super.onHasError(generalResponse);
                agUserListener.onError(generalResponse.getErrorMessage(), generalResponse.getErrorCode());
            }

            @Override
            public void onNext(GeneralResponse<JsonObject> generalResponse) {
                Session.get().setSession(generalResponse.getSessionId());
                if (generalResponse.hasError()) {
                    onHasError(generalResponse);
                } else {
                    onResult(generalResponse.getResult());
                }
            }

            @Override
            protected void onResult(JsonObject result) {
                AgUser agUser = null;
                if (result != null) {
                    try {
                        JSONObject object = new JSONObject(result.toString());
                        agUser = new AgUser(context, object);
                        agUser.save(context);
                        if (agUserListener != null) {
                            agUserListener.onLoaded(agUser);
                        }
                    } catch (JSONException e) {
                        e.getStackTrace();
                        agUserListener.onError("", 401);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                agUserListener.onError("", 401);
            }
        };
        disposable.add(AGApplication
                .api
                .login(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public static ProfileGet.LoginRequest getRequest(Context context, String phone, String password) {
        ProfileGet.LoginRequest request = new ProfileGet.LoginRequest();
        ProfileGet.LoginRequest.Auth authBean = new ProfileGet.LoginRequest.Auth();
        authBean.setLogin("7" + phone);
        authBean.setPassword(password);
        authBean.setGuid(context.getSharedPreferences(GCMHelper.PREFERENCES, Activity.MODE_PRIVATE).getString(GCMHelper.GUID, null));
        request.setAuth(authBean);
        SharedPreferences gcmPrefs = context.getSharedPreferences(GCMHelper.PREFERENCES, Activity.MODE_PRIVATE);
        if (!gcmPrefs.getBoolean(GCMHelper.PROPERTY_ON_SERVER, false)) {
            ProfileGet.DeviceInfo deviceInfo = new ProfileGet.DeviceInfo(gcmPrefs.getString(GCMHelper.GUID, null)
                    , gcmPrefs.getString(GCMHelper.PROPERTY_REG_ID, null)
                    , GCMHelper.getAppVersionName(context));
            request.setDeviceInfo(deviceInfo);
        }
        return request;
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

    public static void afterLoggedOut(BaseActivity baseActivity) {
        baseActivity.sendBroadcast(new Intent(BaseActivity.INTENT_LOGOUT));
        NotificationManager notificationmanager = (NotificationManager) baseActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            for (int i = 0; i <= GCMBroadcastReceiver.messageNotifyId; ++i) {
                notificationmanager.cancel(i);
            }
        } catch (Exception ignored) {
        }
        ProfileManagerRX.clearStoredData(baseActivity);
        Session.get().clear();
        baseActivity.navigateTo().state(Remove.closeAllActivities(), Add.newActivity(new AgAuthState(), BaseActivity.class));
    }

    public static void logOut(final BaseActivity alkActivity) {
        final ProgressDialog dialog = Dialogs.showProgressDialog(alkActivity, R.string.elk_wait_logout);
        JSONObject params = new JSONObject();
        try {
            params.put("auth", new JSONObject().put("logout", true));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HandlerApiResponseSubscriber<EmptyResult[]> handler = new HandlerApiResponseSubscriber<EmptyResult[]>(alkActivity, null) {
            @Override
            protected void onResult(EmptyResult[] result) {
                dialog.dismiss();
                afterLoggedOut(alkActivity);
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
                afterLoggedOut(alkActivity);
            }
        };
        ProfileGet.LoginRequest request = new ProfileGet.LoginRequest();
        ProfileGet.LoginRequest.Auth auth = new ProfileGet.LoginRequest.Auth();
        auth.setLogout(true);
        request.setAuth(auth);
        alkActivity.getDisposables().add(AGApplication
                .api
                .logOut(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public interface AgUserListener {
        void onLoaded(AgUser agUser);

        void onError(String message, int code);
    }
}
