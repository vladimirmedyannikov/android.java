package ru.mos.polls.geotarget;

import android.content.Context;

import com.android.volley2.RequestQueue;
import com.android.volley2.Response;
import com.android.volley2.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.polls.UrlManager;
import ru.mos.polls.geotarget.model.Area;

/**
 * @since 2.3.0
 */

public class GeotargetApiController {

    public static void loadAreas(Context context, final OnAreasListener listener) {
        String method = UrlManager.url(UrlManager.V230,
                UrlManager.Controller.GEOTARGET,
                UrlManager.Methods.AREAS);
        String url = API.getURL(method);

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Area> result = new ArrayList<>();
                if (response != null) {
                    result = Area.from(response.optJSONArray("areas"));
                }
                if (listener != null) {
                    listener.onLoaded(result);
                }
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,
                new JSONObject(),
                responseListener,
                null);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    public static void notifyAboutUserInArea(Context context, int areaId, final OnNotifyUserInAreaListener listener) {
        String method = UrlManager.url(UrlManager.V230,
                UrlManager.Controller.GEOTARGET,
                UrlManager.Methods.USER_IN_AREA);
        String url = API.getURL(method);
        JSONObject body = new JSONObject();
        try {
            body.put("id", areaId);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,
                body,
                responseListener,
                null);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    public interface OnAreasListener {
        void onLoaded(List<Area> loadedAreas);
    }

    public interface OnNotifyUserInAreaListener {
        void onSuccess();
    }
}
