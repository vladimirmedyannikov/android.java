package ru.mos.polls.survey.variants.select;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.survey.variants.select.model.FindObjects;

public class GorodSelectActivity extends SelectActivity<FindObjects> {

    public static final String EXTRA_CATEGORORY = "category";
    public static final String EXTRA_OBJECT_ID = "id";
    public static final String EXTRA_ADDRESS = "address";

    private String category;
    private String address;
    private int objectId;
    private String parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getIntent().getStringExtra(EXTRA_CATEGORORY);
        parent = getIntent().getStringExtra("parent");
    }

    @Override
    protected void getDataFromObject(FindObjects object) {
        objectId = object.getId();
        address = object.getAddress();
    }

    @Override
    protected void processRequest(String search) {
        request.setSoesgCategoryCode(category);
        request.setSearch(search);
        if (parent != null) {
            request.setParent(parent);
        }
    }

    @Override
    protected void addDataToIntent(Intent data) {
        data.putExtra(EXTRA_ADDRESS, address);
        data.putExtra(EXTRA_OBJECT_ID, objectId);
    }

    @Override
    protected View onNewView() {
        return View.inflate(GorodSelectActivity.this, R.layout.listitem_gorod_select, null);
    }

    @Override
    protected void onConvertView(View convertView, FindObjects object) {
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        String title = object.getAddress();
        textView.setText(title);
    }

    @Override
    protected void doRequest(String s) {
        disposables.add(AGApplication
                .api
                .findObjects(request)
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getHandler()));
    }
}

