package ru.mos.polls.profile.ui.views;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


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
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.profile.ui.views.service.AddressesService;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


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
        HandlerApiResponseSubscriber<List<Value>> handler = new HandlerApiResponseSubscriber<List<Value>>(activity, null) {
            @Override
            protected void onResult(List<Value> result) {
                progressBar.setVisibility(View.INVISIBLE);
                ArrayAdapter<Value> adapter = new StreetAdapter(activity, R.layout.elk_item_dropdown);
                atv.setAdapter(adapter);
                adapter.addAll(result);
                if (atv.hasFocus())
                    atv.showDropDown();
                StreetWatcher.this.listener.onDataLoaded(result.size());
            }

            @Override
            public void onHasError(GeneralResponse<List<Value>> generalResponse) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        };
        activity.getDisposables().add(AGApplication
                .api
                .getAddressStreetList(new AddressesService.Request(pattern, 250))
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public interface Listener {
        void onDataLoaded(int count);
    }
}