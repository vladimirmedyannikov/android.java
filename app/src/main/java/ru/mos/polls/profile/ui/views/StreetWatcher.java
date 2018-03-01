package ru.mos.polls.profile.ui.views;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.profile.model.flat.StreetAdapter;
import ru.mos.polls.profile.model.flat.Value;
import ru.mos.polls.UrlManager;
import ru.mos.polls.api.API;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.helpers.TextHelper;
import ru.mos.polls.profile.ui.views.service.AddressesService;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;


public class StreetWatcher implements TextWatcher {

    private static final Set<String> SECRET_COMBO = createSecketCombo();
    private final View progressBar;

    private static Set<String> createSecketCombo() {
        Set<String> streets = new HashSet<String>(10);
        streets.add("пер");
        streets.add("про");
        streets.add("нов");

        return streets;
    }

    private final BaseActivity activity;
    private final AutoCompleteTextView atv;
    private final Listener listener;

    public StreetWatcher(BaseActivity activity, AutoCompleteTextView atv, View progressBar, Listener listener) {
        this.activity = activity;
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
        String pattern = s.toString();
        if (pattern.length() < 3)
            return;

        progressBar.setVisibility(View.VISIBLE);
//        final Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                progressBar.setVisibility(View.INVISIBLE);
//                ArrayAdapter<Value> adapter = new StreetAdapter(activity, R.layout.elk_item_dropdown);
//                int count = response.length();
//                for (int i = 0, length = count; i < length; i++) {
//                    JSONObject val = response.optJSONObject(i);
//                    String label = val.optString("label");
//                    label = TextHelper.capitalizeFirstLatter(label);
//                    if (val.isNull("terr_name"))
//                        adapter.add(new Value(val.optString("value"), label));
//                    else
//                        adapter.add(new Value(val.optString("value"), label, val.optString("terr_name")));
//                }
//                atv.setAdapter(adapter);
//                if (atv.hasFocus())
//                    atv.showDropDown();
//                request = null;
//                StreetWatcher.this.listener.onDataLoaded(count);
//            }
//        };
//        JSONObject params = new JSONObject();
//        try {
//            params.put("term", pattern);
//            params.put("limit", 250);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressBar.setVisibility(View.INVISIBLE);
//                request = null;
//            }
//        };
//        request = new JsonArrayRequest(API.getURL(UrlManager.url(UrlManager.Controller.AGPROFILE, UrlManager.Methods.GET_ADDRESS_STREET_LIST)), params, listener, errorListener);
//        activity.addRequest(request);
        AddressesService.Request request = new AddressesService.Request(pattern,250);
        HandlerApiResponseSubscriber<List<Value>> handler = new HandlerApiResponseSubscriber<List<Value>>(activity, null) {
            @Override
            protected void onResult(List<Value> result) {
                StreetWatcher.this.listener.onDataLoaded(result.size());
            }
        };
        activity.getDisposables().add(AGApplication
                .api
                .getAddressStreetList(new AddressesService.Request(pattern,250))
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public interface Listener {

        void onDataLoaded(int count);

    }

}