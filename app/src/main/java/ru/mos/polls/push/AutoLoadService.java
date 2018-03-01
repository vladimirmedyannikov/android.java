package ru.mos.polls.push;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.api.API;
import ru.mos.polls.push.service.RegisterPush;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

public class AutoLoadService extends Service {
    public final static String TASK = "Task";
    public static final int GCM_REGISTER = 5;
    public static final int REFRESH_DATA = 6;
    private CompositeDisposable disposable;

//    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        disposable = new CompositeDisposable();
//        requestQueue = Volley.newRequestQueue(this);
    }

    @Override
    public void onDestroy() {
//		requestQueue.stop();
//		requestQueue = null;
        disposable.clear();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getIntExtra(TASK, 0)) {
                case GCM_REGISTER:
                    pushRegister();
                    break;
                case REFRESH_DATA:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void pushRegister() {
        final SharedPreferences prefs = getSharedPreferences(GCMHelper.PREFERENCES, MODE_PRIVATE);
        final String guid = API.getGUID(this);
        Thread gcmThread = new Thread() {
            public void run() {
                try {
                    String registrationId = prefs.getString(GCMHelper.PROPERTY_REG_ID, "");
                    int storedAppVersion = prefs.getInt(GCMHelper.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
                    int curAppVersion = GCMHelper.getAppVersion(getApplicationContext());
                    if (TextUtils.isEmpty(registrationId) || storedAppVersion < curAppVersion) {
                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(AutoLoadService.this);
                        registrationId = gcm.register(GCMHelper.SENDER_ID);
                        prefs.edit().
                                putString(GCMHelper.PROPERTY_REG_ID, registrationId).
                                putInt(GCMHelper.PROPERTY_APP_VERSION, curAppVersion).
                                putBoolean(GCMHelper.PROPERTY_ON_SERVER, false).commit();
                    }

                    if (!prefs.getBoolean(GCMHelper.PROPERTY_ON_SERVER, false)) {
                        HandlerApiResponseSubscriber<RegisterPush.Response.Result> handler = new HandlerApiResponseSubscriber<RegisterPush.Response.Result>() {
                            @Override
                            protected void onResult(RegisterPush.Response.Result result) {
                                prefs.edit().putBoolean(GCMHelper.PROPERTY_ON_SERVER, true).commit();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                //super.onError(throwable);
                            }
                        };
                        disposable.add(AGApplication
                                .api
                                .registerPush(new RegisterPush.Request(guid, registrationId, GCMHelper.getAppVersionName(getApplicationContext())))
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(handler));
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        gcmThread.setName("GCM Thread");
        gcmThread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
