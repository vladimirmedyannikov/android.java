package ru.mos.polls.informer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.BuildConfig;
import ru.mos.polls.informer.service.GetAppVersion;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

/**
 * Created by Trunks on 20.02.2018.
 */

public class InformerApiControllerRX {

    public static void loadActualAppVersion(final Callback callback) {
        HandlerApiResponseSubscriber<GetAppVersion.Response.Result> handler = new HandlerApiResponseSubscriber<GetAppVersion.Response.Result>() {
            @Override
            protected void onResult(GetAppVersion.Response.Result result) {
                String versionName = BuildConfig.VERSION_NAME;
                if (result.getVersion() != null) {
                    versionName = result.getVersion();
                }
                callback.onGet(versionName);
            }
        };
        AGApplication
                .api
                .getAppVersion(new GetAppVersion.Request("android"))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }


    public interface Callback {
        void onGet(String actualAppVersion);

        void onError();
    }
}
