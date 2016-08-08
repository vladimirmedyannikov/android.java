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

public class GorodSelectActivity extends SelectActivity {

    public static final String EXTRA_CATEGORORY = "category";
    public static final String EXTRA_OBJECT_ID = "id";
    public static final String EXTRA_ADDRESS = "address";

    private String category;
    private String address;
    private String objectId;
    private String parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getIntent().getStringExtra(EXTRA_CATEGORORY);
        parent = getIntent().getStringExtra("parent");
    }

    @Override
    protected void onLoadFromJson(JSONObject jsonObject) {
        objectId = jsonObject.optString("id");
        address = jsonObject.optString("address");
    }

    @Override
    protected void processRequestJson(String search, JSONObject requestJsonObject) throws JSONException {
        requestJsonObject.put("soesg_category_code", category);
        requestJsonObject.put("search", search);
        if (!parent.isEmpty()) {
            requestJsonObject.put("parent", parent);
        }
    }

    @Override
    protected String onGetUrl() {
        return API.getURL(UrlManager.url(UrlManager.Controller.SOESG, UrlManager.Methods.FIND_OBJECTS));
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
    protected void onConvertView(View convertView, JSONObject jsonObject) {
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        String title = jsonObject.optString("address");
        textView.setText(title);
    }

}
