package ru.mos.polls.informer;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.BuildConfig;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;

/**
 *  Получение актуальной версии приложения {@link #loadActualAppVersion(BaseActivity, Callback)}
 *  @since 2.3.0
 */
public class InformetApiController {

    /**
     * получение актуально версии приложения с серверсайда
     * {@link ru.mos.polls.UrlManager.Controller#UTILS}
     * {@link ru.mos.polls.UrlManager.Methods#APP_VERSION}
     *
     * @param elkActivity  {@link BaseActivity}
     * @param callback {@link Callback}
     */
    public static void loadActualAppVersion(BaseActivity elkActivity, final Callback callback) {
        String method = UrlManager.url(UrlManager.Controller.UTILS,
                UrlManager.Methods.APP_VERSION);
        String url = API.getURL(method);

        JSONObject body = new JSONObject();
        try {
            body.put("platform", "android");
        } catch (JSONException ignored) {
        }

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String result = BuildConfig.VERSION_NAME;
                if (jsonObject != null) {
                    result = jsonObject.optString("version");
                }
                if (callback != null) {
                    callback.onGet(result);
                }
            }
        };

        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (callback != null) {
                    callback.onError();
                }
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,
                body,
                responseListener,
                errorListener);

        elkActivity.addRequest(jsonObjectRequest);
    }

    public interface Callback {
        void onGet(String actualAppVersion);
        void onError();
    }
}
