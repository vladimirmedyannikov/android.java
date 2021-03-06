package ru.mos.polls.shop;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.appsflyer.AppsFlyerLib;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.BuildConfig;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.helpers.AppsFlyerConstants;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.util.NetworkUtils;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Магазин поощрений
 *
 * @since 1.9.2
 */
public class WebShopFragment extends Fragment implements MainActivity.Callback {
    private static final String host = "release".equalsIgnoreCase(BuildConfig.BUILD_TYPE) ? "shop.ag.mos.ru" : "dev.shop.ag.mos.ru";
    private static final String urlPattern = "http://%s/catalog";
    private static final String url = String.format(urlPattern, host);
    private static final String cookiesPattern = "EMPSESSION=%s";

    public static WebShopFragment newInstance() {
        return new WebShopFragment();
    }

    private static String getCookies(String sesionId) {
        return String.format(cookiesPattern, sesionId);
    }

    private BaseActivity activity;
    private Unbinder unbinder;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.rootConnectionError)
    View rootConnectionError;
    @BindView(R.id.root)
    View root;

    CookieManager cookieManager;
    private static final String[] WRITE_EXTERNAL_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int WRITE_EXTERNAL_REQUEST_CODE = 788;

    @OnClick(R.id.internet_lost_reload)
    public void refresh() {
        if (checkInternetConnection()) webView.loadUrl(url);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.WebShop_AppTheme_NoActionBar);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_shop, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public boolean checkInternetConnection() {
        if (NetworkUtils.hasInternetConnection(getActivity())) {
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        TitleHelper.setTitle(activity, R.string.goto_webshop);
        webView.setVisibility(View.VISIBLE);
        new WebViewTask().execute();
        AppsFlyerLib.sendTrackingWithEvent(activity, AppsFlyerConstants.SHOP_OPENED, "");
        hideActionBarLand();
        Statistics.shopBuy();
        GoogleStatistics.AGNavigation.shopBuy();
        checkInternetConnection();
        setWebViewDownloadListener();
    }

    public void setWebViewDownloadListener() {
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            if (EasyPermissions.hasPermissions(getActivity(), WRITE_EXTERNAL_STORAGE)) {
                downloadFile(url, userAgent, contentDisposition, mimetype);
            } else {
                EasyPermissions.requestPermissions(getActivity(), "Для сохранения файла необходимо разрешение", WRITE_EXTERNAL_REQUEST_CODE, WRITE_EXTERNAL_STORAGE);
            }
        });
    }

    @AfterPermissionGranted(WRITE_EXTERNAL_REQUEST_CODE)
    public void downloadFile(String url, String userAgent, String contentDisposition, String mimetype) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimetype);
        cookieManager = CookieManager.getInstance();
        PersistentConfig persistentConfig = new PersistentConfig(activity.getApplicationContext());
        persistentConfig.setCookie(getCookies(Session.getSession(activity.getApplicationContext())));
        cookieManager.setCookie(host, persistentConfig.getCookieString());
        request.addRequestHeader("Cookie", getCookies(Session.getSession(activity.getApplicationContext())));
        request.setDescription("Downloading file...");
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        DownloadManager dManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        dManager.enqueue(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setProgress(boolean visible) {
        loading.setVisibility(visible ? View.VISIBLE : View.GONE);
        webView.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        activity.onBackPressed();
                    }
                    return false;
            }

        }
        return true;
    }

    private class WebViewTask extends AsyncTask<Void, Void, Boolean> {
        String sessionCookie;
        CookieManager cookieManager;

        @Override
        protected void onPreExecute() {
            setProgress(true);
            CookieSyncManager.createInstance(activity);
            cookieManager = CookieManager.getInstance();
            PersistentConfig persistentConfig = new PersistentConfig(activity.getApplicationContext());
            persistentConfig.setCookie(getCookies(Session.getSession(activity.getApplicationContext())));
            sessionCookie = persistentConfig.getCookieString();
            if (sessionCookie != null) {
                cookieManager.removeSessionCookie();
            }
            super.onPreExecute();
        }

        protected Boolean doInBackground(Void... param) {
            SystemClock.sleep(1000);
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (sessionCookie != null) {
                cookieManager.setCookie(host, sessionCookie);
                CookieSyncManager.getInstance().sync();
            }
            if (webView != null) {
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setSupportZoom(true);
                webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                webView.invokeZoomPicker();
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url.startsWith("mailto:")) {
                            MailTo mt = MailTo.parse(url);
                            Intent i = newEmailIntent(activity, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                            startActivity(i);
                            view.reload();
                            return true;
                        }
                        return super.shouldOverrideUrlLoading(view, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        showLoadingProgress(true);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        showLoadingProgress(false);
                    }
                });
                webView.loadUrl(url);
            }
        }
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

    private boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Отлавливаем поворот экрана
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideActionBarLand();
        webView.reload();
    }

    /**
     * Показываем или скрываем action bar
     * в зависимости от положения экрана
     */
    private void hideActionBarLand() {
        int orient = getResources().getConfiguration().orientation;
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            switch (orient) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    actionBar.hide();
                    break;
                case Configuration.ORIENTATION_PORTRAIT:
                    actionBar.show();
                    break;
                default:
            }
        }
    }

    private void showLoadingProgress(boolean isVisible) {
        if (isAdded()) {
            int orient = getResources().getConfiguration().orientation;
            switch (orient) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    ActionBar actionBar = activity.getSupportActionBar();
                    if (actionBar != null) {
                        if (isVisible) {
                            actionBar.show();
                            setProgress(true);
                        } else {
                            actionBar.hide();
                            setProgress(false);
                        }
                    }
                    break;
                case Configuration.ORIENTATION_PORTRAIT:
                    setProgress(isVisible);
                    break;
            }
        }
    }
}
