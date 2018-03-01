package ru.mos.polls.profile.ui.views;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.profile.model.flat.Value;
import ru.mos.polls.profile.ui.views.service.AddressesService;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;


public class BuildingWatcher implements TextWatcher {

    private final BaseActivity activity;
    private final AutoCompleteTextView etStreet;
    private final AutoCompleteTextView atv;
    private final View progressBar;
    private final Listener listener;
    boolean houseSelected;

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
        if (newBuilding.length() == 0) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        if (!houseSelected) {
            HandlerApiResponseSubscriber<List<Value>> handler = new HandlerApiResponseSubscriber<List<Value>>(activity, null) {
                @Override
                protected void onResult(List<Value> result) {
                    progressBar.setVisibility(View.INVISIBLE);
                    ArrayAdapter<Value> newAdapter = new ArrayAdapter<Value>(activity, R.layout.elk_item_dropdown, R.id.tvTitle);
                    newAdapter.addAll(result);
                    atv.setAdapter(newAdapter);
                    if (atv.hasFocus())
                        atv.showDropDown();
                    BuildingWatcher.this.listener.onDataLoaded(result.size());
                }

                @Override
                public void onHasError(GeneralResponse<List<Value>> generalResponse) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            };
            activity.getDisposables().add(AGApplication
                    .api
                    .getAddressHouseList(new AddressesService.Request(newBuilding, 1000, etStreet.getTag()))
                    .debounce(1000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(handler));
        }
        this.listener.onAfterTextChanged(s);
    }

    public void setHouseSelected(boolean houseSelected) {
        this.houseSelected = houseSelected;
    }

    public interface Listener {
        void onDataLoaded(int count);

        void onAfterTextChanged(Editable s);
    }
}