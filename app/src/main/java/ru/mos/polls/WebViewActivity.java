package ru.mos.polls;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.MailTo;
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
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.model.NewsFindModel;
import ru.mos.polls.quests.controller.QuestsApiControllerRX;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;
import ru.mos.polls.service.NewsFind;
import ru.mos.polls.util.NetworkUtils;


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
    @BindView(R.id.rootConnectionError)
    View rootConnectionError;
    @BindView(R.id.root)
    View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        webView = (WebView) findViewById(R.id.webView);
        loading = (ProgressBar) findViewById(R.id.loading);
        checkInternetConnection();
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
                boolean isFirstUrl = isFirstUrl(url);
                if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = newEmailIntent(WebViewActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    startActivity(i);
                    view.reload();
                    return true;
                }
                /**
                 * если url не первый, то открываем в браузере
                 */
                if ((urlAllowed && isOnline) || !isFirstUrl) {
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

    public static Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    @OnClick(R.id.internet_lost_reload)
    public void refresh() {
        if (checkInternetConnection()) webView.loadUrl(firstUrl);
    }

    public boolean checkInternetConnection() {
        if (NetworkUtils.hasInternetConnection(this)) {
            hideErrorConnectionViews();
            return true;
        } else {
            setErrorConneсtionView();
            return false;
        }
    }

    public void hideErrorConnectionViews() {
        if (rootConnectionError.getVisibility() == View.VISIBLE) {
            rootConnectionError.setVisibility(View.GONE);
        }
        if (root.getVisibility() == View.GONE) {
            root.setVisibility(View.VISIBLE);
        }
    }

    public void setErrorConneсtionView() {
        rootConnectionError.setVisibility(View.VISIBLE);
        root.setVisibility(View.GONE);
    }

    private void tryHide() {
        String id = getIntent().getStringExtra(ID);
        if (!TextUtils.isEmpty(id) && !"null".equalsIgnoreCase(id))
            try {
                long newsId = Long.valueOf(id);
                if (isShareEnable) {
                    findShareUrl(newsId);
                }
                QuestsApiControllerRX.hideNews(disposables,this, newsId, null, Progressable.STUB);
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
                Statistics.shareNews();
                GoogleStatistics.AGNavigation.shareNews();
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

    /**
     * при сравнении удаляем протоколы http и/или https
     * т.к. они могут быть разные для
     * @param url
     * так и для {@link WebViewActivity#firstUrl}
     * @return
     */
    private boolean isFirstUrl(String url) {
        String newFirstUrl = "";
        String newUrl = "";
        if (firstUrl.contains("https")) {
            newFirstUrl = firstUrl.replace("https", "");
        } else if (firstUrl.contains("http")) {
            newFirstUrl = firstUrl.replace("http", "");
        }
        if (url.contains("https")) {
            newUrl = url.replace("https", "");
        } else if (url.contains("http")) {
            newUrl = url.replace("http", "");
        }

        if (newFirstUrl.equals("") || newUrl.equals("")) {
            return url.equals(firstUrl);
        } else {
            return newUrl.equals(newFirstUrl);
        }
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
                        QuestsApiControllerRX.hideNews(disposables, WebViewActivity.this, Long.parseLong(newsId), null, Progressable.STUB);
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
        HandlerApiResponseSubscriber<NewsFindModel> handler = new HandlerApiResponseSubscriber<NewsFindModel>() {
            @Override
            protected void onResult(NewsFindModel result) {
                shareUrl = result.getPublicSiteUrl();
                boolean isShareEnable = !TextUtils.isEmpty(shareUrl) && !"null".equalsIgnoreCase(shareUrl);
                if (shareMenuItem != null) {
                    shareMenuItem.setVisible(isShareEnable);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        };
        disposables.add(AGApplication
                .api
                .findNews(new NewsFind.Request(id))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }
}
