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
import ru.mos.polls.survey.variants.select.model.VariantsObjects;

public class ServiceSelectActivity extends SelectActivity<VariantsObjects> {

    public static final String EXTRA_CATEGORORY = "category";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_DESCRIPTION = "description";

    private String category;
    private long id;
    private String title;
    private String address;
    private String parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getIntent().getStringExtra(EXTRA_CATEGORORY);
        parent = getIntent().getStringExtra("parent");
    }

    @Override
    protected void getDataFromObject(VariantsObjects object) {
        id = object.getId();
        title = object.getTitle();
        address = object.getDescription();
    }

    @Override
    protected void processRequest(String search) {
        request.setCategory(category);
        request.setSearch(search);
        if (parent != null) {
            request.setParent(parent);
        }
    }

    @Override
    protected void addDataToIntent(Intent data) {
        data.putExtra(EXTRA_ID, id);
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, address);
    }

    @Override
    protected View onNewView() {
        return View.inflate(ServiceSelectActivity.this, R.layout.listitem_service_select, null);
    }

    @Override
    protected void onConvertView(View convertView, VariantsObjects object) {
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
        TextView descriptionTextView = (TextView) convertView.findViewById(R.id.description);
        String title = object.getTitle();
        String description = object.getAddress();
        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }

    @Override
    protected void doRequest(String s) {
        disposables.add(AGApplication
                .api
                .findVariants(request)
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getHandler()));

    }

}
