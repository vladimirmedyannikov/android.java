package ru.mos.polls.offer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;

public class OfferActivity extends BaseActivity {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_TITLE = "title";

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.accepted)
     CheckBox checked;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        String url = getIntent().getStringExtra(EXTRA_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        webView.loadUrl(url);
        setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.close)
    void accept(){
        boolean accepted = checked.isChecked();
        if (accepted) {
            String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.APPROVE_OFFER));
            JSONObject requestJsonObject = new JSONObject();
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject o) {
                    progressDialog.dismiss();
                    finish();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.dismiss();
                    Toast.makeText(OfferActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
            progressDialog.show();
            addRequest(request);
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
