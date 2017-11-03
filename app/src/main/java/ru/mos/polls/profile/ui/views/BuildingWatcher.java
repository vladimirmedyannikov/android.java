package ru.mos.polls.profile.ui.views;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.R;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonArrayRequest;
import ru.mos.elk.profile.flat.Value;
import ru.mos.polls.UrlManager;


public class BuildingWatcher implements TextWatcher {

    private final BaseActivity activity;
    private final AutoCompleteTextView etStreet;
    private final AutoCompleteTextView atv;
    private final View progressBar;
    private JsonArrayRequest request;
    private final Listener listener;

    public BuildingWatcher(BaseActivity activity, AutoCompleteTextView etStreet, AutoCompleteTextView atv, View progressBar, Listener listener) {
        this.activity = activity;
        this.etStreet = etStreet;
        this.atv = atv;
        this.progressBar = progressBar;
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String newBuilding = s.toString();
        if (newBuilding.length() == 0)
            return;

        if (request != null)
            request.cancel();

        progressBar.setVisibility(View.VISIBLE);
        final Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayAdapter<Value> newAdapter = new ArrayAdapter<Value>(activity, R.layout.elk_item_dropdown, R.id.tvTitle);
                progressBar.setVisibility(View.INVISIBLE);
                int count = response.length();
                for (int i = 0, length = count; i < length; i++) {
                    JSONObject val = response.optJSONObject(i);
                    newAdapter.add(new Value(val.optString("value"), val.optString("label")));
                }
                atv.setAdapter(newAdapter);
                if (atv.hasFocus())
                    atv.showDropDown();
                request = null;
                BuildingWatcher.this.listener.onDataLoaded(count);
            }
        };
        JSONObject params = new JSONObject();
        try {
            params.put("term", newBuilding);
            params.put("limit", 1000);
            params.put("ul", etStreet.getTag());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                request = null;
            }
        };
        request = new JsonArrayRequest(API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.GET_ADDRESS_HOUSE_LIST)), params, listener, errorListener);
        activity.addRequest(request);
        this.listener.onAfterTextChanged(s);
    }

    public interface Listener {

        void onDataLoaded(int count);

        void onAfterTextChanged(Editable s);

    }

}