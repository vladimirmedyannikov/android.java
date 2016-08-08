package ru.mos.polls.profile.controller;


import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonArrayRequest;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.UrlManager;
import ru.mos.polls.profile.model.DistrictArea;
import ru.mos.polls.profile.model.Reference;

/**
 * Контролер для Округов и Районов
 *
 * @since 2.0.0
 */
public class FlatApiController {

    /**
     * получаем округ по номеру района
     *
     * @since 2.0.0
     */

    public static void getDistrictByArea(BaseActivity elkActivity, String areaNumber, final DistrictAreaListener districtAreaListener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.GET_DISTRICT_AND_AREA));
        JSONObject requestJsonObject = new JSONObject();
        Session.addSession(requestJsonObject);
        try {
            requestJsonObject.put("area_id", areaNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        com.android.volley2.Response.Listener<JSONObject> listener = new com.android.volley2.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    DistrictArea districtArea = new DistrictArea(jsonObject);
                    districtAreaListener.onLoaded(districtArea);
                }
            }

        };
        com.android.volley2.Response.ErrorListener errorListener = new com.android.volley2.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                districtAreaListener.onError(volleyError);
            }
        };
        JsonObjectRequest request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
        elkActivity.addRequest(request);
    }

    /**
     * Получаем список Округов и списков
     * String value может быть null
     *
     * @param elkActivity
     * @param referenceListener
     * @since 2.0.0
     */
    public static void getReference(BaseActivity elkActivity, final ReferenceListener referenceListener, String value) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.GET_DISTRICTS));
        JSONObject requestJsonObject = new JSONObject();
        Session.addSession(requestJsonObject);
        if (value != null) {
            url = API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.GET_AREAS));
            try {
                requestJsonObject.put("district_id", value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if (jsonArray != null) {
                    List<Reference> ref = Reference.fromJsonArray(jsonArray);
                    referenceListener.onLoaded(ref);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                referenceListener.onError(volleyError);
            }
        };
        JsonArrayRequest request = new JsonArrayRequest(url, requestJsonObject, listener, errorListener);
        elkActivity.addRequest(request);
    }

    public interface ReferenceListener {
        void onLoaded(List<Reference> referenceList);

        void onError(VolleyError volleyError);
    }

    public interface DistrictAreaListener {
        void onLoaded(DistrictArea districtArea);

        void onError(VolleyError volleyError);
    }
}