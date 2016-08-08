package ru.mos.polls.survey.variants.select;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.api.API;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;

public class ServiceSelectActivity extends SelectActivity {

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
    protected void onLoadFromJson(JSONObject jsonObject) {
        id = jsonObject.optLong("id");
        title = jsonObject.optString("title");
        address = jsonObject.optString("descrpiption");
    }

    @Override
    protected void processRequestJson(String search, JSONObject requestJsonObject) throws JSONException {
        requestJsonObject.put("category", category);
        requestJsonObject.put("search", search);
        if (parent != null) {
            requestJsonObject.put("parent", parent);
        }
    }

    @Override
    protected String onGetUrl() {
        return API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.FIND_VARIANTS));
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
    protected void onConvertView(View convertView, JSONObject jsonObject) {
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
        TextView descriptionTextView = (TextView) convertView.findViewById(R.id.description);
        String title = jsonObject.optString("title");
        String description = jsonObject.optString("address");
        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }

}
