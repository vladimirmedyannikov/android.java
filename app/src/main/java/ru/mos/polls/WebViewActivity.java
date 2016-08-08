package ru.mos.polls;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.quests.controller.QuestsApiController;


public class WebViewActivity extends ToolbarAbstractActivity {
    public static final String INFORMATION_TITLE = "information_title_extra";
    public static final String INFORMATION_URL = "information_url_extra";
    public static final String ONLY_LOAD_FIRST_URL = "only_load_first_url";
    public static final String ID = "id";
    public static final String IS_SHARE_ENABLE = "is_share_enable";

    public static void startActivityWithOnlyFirstUrl(Context context, String title, String linkUrl, String id) {
        startActivity(context, title, linkUrl, id, true, true);
    }

    public static void startActivity(Context context, String title, String linkUrl, String id) {
        startActivity(context, title, linkUrl, id, false, true);
    }

    public static void startActivity(Context context, String title, String linkUrl, String id, boolean onlyLoadFirstUrl, boolean isShareEnable) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(INFORMATION_TITLE, title);
        intent.putExtra(INFORMATION_URL, linkUrl);
        intent.putExtra(ONLY_LOAD_FIRST_URL, onlyLoadFirstUrl);
        intent.putExtra(ID, id);
        intent.putExtra(IS_SHARE_ENABLE, isShareEnable);
        context.startActivity(intent);
    }

    public static Intent getIntentForNew(Context context, String id, String title, String linkUrl) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(INFORMATION_TITLE, title);
        intent.putExtra(INFORMATION_URL, linkUrl);
        intent.putExtra(ONLY_LOAD_FIRST_URL, true);
        intent.putExtra(IS_SHARE_ENABLE, true);
        intent.putExtra(ID, id);
        return intent;
    }

    private WebView webView;
    private ProgressBar loading;
    private String title;
    private String firstUrl;
    private boolean isOnlyLoadFirstUrl;
    private boolean isShareEnable;
    private MenuItem shareMenuItem;
    private String shareUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_webview);
        webView = (WebView) findViewById(R.id.webView);
        loading = (ProgressBar) findViewById(R.id.loading);
        LinearLayout stubOfflineView = (LinearLayout) findViewById(R.id.stubOfflineView);
        stubOfflineView.setVisibility(View.GONE);
        if (!isOnline(this)) {
            stubOfflineView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            Toast.makeText(WebViewActivity.this, getString(R.string.internet_failed_to_connect), Toast.LENGTH_SHORT).show();
            return;
        }
        title = getIntent().getStringExtra(INFORMATION_TITLE);
        firstUrl = getIntent().getStringExtra(INFORMATION_URL);
        isOnlyLoadFirstUrl = getIntent().getBooleanExtra(ONLY_LOAD_FIRST_URL, false);
        isShareEnable = getIntent().getBooleanExtra(IS_SHARE_ENABLE, true);
        tryHide();
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.invokeZoomPicker();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean urlAllowed = isUrlAllowedForLoad(url);
                boolean isOnline = isOnline(WebViewActivity.this);
                if (urlAllowed && isOnline) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setProgress(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setProgress(false);
            }

            private boolean isUrlAllowedForLoad(String url) {
                return isOnlyLoadFirstUrl && !url.equalsIgnoreCase(firstUrl);
            }
        });
        trySetUrlFromIntent();
        TitleHelper.setTitle(this, title == null || "".equalsIgnoreCase(title) ?
                getString(R.string.default_web_view_title) : title);
        webView.loadUrl(firstUrl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void tryHide() {
        String id = getIntent().getStringExtra(ID);
        if (!TextUtils.isEmpty(id) && !"null".equalsIgnoreCase(id))
            try {
                long newsId = Long.valueOf(id);
                if (isShareEnable) {
                    findShareUrl(newsId);
                }
                QuestsApiController.hideNews(this, newsId, null);
            } catch (Exception ignored) {
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isShareEnable) {
            getMenuInflater().inflate(R.menu.share, menu);
            shareMenuItem = menu.findItem(R.id.action_share);
            shareMenuItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
    }

    private boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void trySetUrlFromIntent() {
        UrlSchemeController.startWebView(this, new UrlSchemeController.LinkListener() {
            @Override
            public void onDetected(String title, String link, String newsId) {
                firstUrl = link;
                isOnlyLoadFirstUrl = true;
                WebViewActivity.this.title = title;
                /**
                 * На всякий случай try-catch, мало ил тип id другой придет
                 */
                try {
                    if (newsId != null && !TextUtils.isEmpty(newsId)) {
                        if (isShareEnable) {
                            findShareUrl(Long.parseLong(newsId));
                        }
                        QuestsApiController.hideNews(WebViewActivity.this, Long.parseLong(newsId), null);
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    private void setProgress(boolean visible) {
        loading.setVisibility(visible ? View.VISIBLE : View.GONE);
        webView.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    private void findShareUrl(long id) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.NEWS, UrlManager.Methods.FIND));
        JSONObject requestJson = new JSONObject();
        try {
            JSONObject authJson = new JSONObject();
            authJson.put(Session.SESSION_ID, Session.getSession(this));
            requestJson.put(Session.AUTH, authJson);
            requestJson.put(ID, id);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    shareUrl = jsonObject.optString("public_site_url");
                    boolean isShareEnable = !TextUtils.isEmpty(shareUrl) && !"null".equalsIgnoreCase(shareUrl);
                    if (shareMenuItem != null) {
                        shareMenuItem.setVisible(isShareEnable);
                    }
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        };
        addRequest(new JsonObjectRequest(url, requestJson, responseListener, errorListener));
    }
}
