package ru.mos.polls.shop.vm;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.appsflyer.AppsFlyerLib;

import pub.devrel.easypermissions.AfterPermissionGranted;
import ru.mos.polls.BuildConfig;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentShopBinding;
import ru.mos.polls.helpers.AppsFlyerConstants;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.rxhttp.session.Session;
import ru.mos.polls.shop.PersistentConfig;
import ru.mos.polls.shop.ui.WebShopFragment;
import ru.mos.polls.util.NetworkUtils;
import ru.mos.polls.util.PermissionsUtils;

import static android.content.Context.DOWNLOAD_SERVICE;


public class WebShopFragmentVM extends UIComponentFragmentViewModel<WebShopFragment, FragmentShopBinding> implements MainActivity.Callback{
    private static final String host = "release".equalsIgnoreCase(BuildConfig.BUILD_TYPE) ? "shop.ag.mos.ru" : "dev.shop.ag.mos.ru";
    private static final String urlPattern = "http://%s/catalog";
    private static final String url = String.format(urlPattern, host);
    private static final String cookiesPattern = "EMPSESSION=%s";

    private static String getCookies(String sesionId) {
        return String.format(cookiesPattern, sesionId);
    }

    private BaseActivity activity;
    private WebView webView;
    private ProgressBar progress;
    private View rootConnectionError;
    private View root;

    CookieManager cookieManager;
    public static final int WRITE_EXTERNAL_REQUEST_CODE = 788;

    public WebShopFragmentVM(WebShopFragment fragment, FragmentShopBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentShopBinding binding) {
        webView = binding.webView;
        progress = binding.progress;
        root = binding.root;
        rootConnectionError = binding.relativeView.findViewById(R.id.rootConnectionError);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().build();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        activity = (BaseActivity) getActivity();
        TitleHelper.setTitle(activity, R.string.goto_webshop);
        webView.setVisibility(View.VISIBLE);
        new WebShopFragmentVM.WebViewTask().execute();
        AppsFlyerLib.sendTrackingWithEvent(activity, AppsFlyerConstants.SHOP_OPENED, "");
//        hideActionBarLand();
        Statistics.shopBuy();
        GoogleStatistics.AGNavigation.shopBuy();
        checkInternetConnection();
        setWebViewDownloadListener();
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

    public void setWebViewDownloadListener() {
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            if (PermissionsUtils.WRITE_EXTERNAL_STORAGE.isGranted(getActivity())) {
                downloadFile(url, userAgent, contentDisposition, mimetype);
            } else {
                PermissionsUtils.WRITE_EXTERNAL_STORAGE.request(getActivity(), WRITE_EXTERNAL_REQUEST_CODE);
            }
        });
    }

    @AfterPermissionGranted(WRITE_EXTERNAL_REQUEST_CODE)
    public void downloadFile(String url, String userAgent, String contentDisposition, String mimetype) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimetype);
        cookieManager = CookieManager.getInstance();
        PersistentConfig persistentConfig = new PersistentConfig(activity.getApplicationContext());
        persistentConfig.setCookie(getCookies(Session.get().getSession()));
        cookieManager.setCookie(host, persistentConfig.getCookieString());
        request.addRequestHeader("Cookie", getCookies(Session.get().getSession()));
        request.setDescription("Downloading file...");
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        DownloadManager dManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        dManager.enqueue(request);
    }

    private void setProgress(boolean visible) {
        progress.setVisibility(visible ? View.VISIBLE : View.GONE);
        webView.setVisibility(visible ? View.GONE : View.VISIBLE);
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

    private class WebViewTask extends AsyncTask<Void, Void, Boolean> {
        String sessionCookie;
        CookieManager cookieManager;

        @Override
        protected void onPreExecute() {
            setProgress(true);
            CookieSyncManager.createInstance(activity);
            cookieManager = CookieManager.getInstance();
            PersistentConfig persistentConfig = new PersistentConfig(activity.getApplicationContext());
            persistentConfig.setCookie(getCookies(Session.get().getSession()));
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
                            getFragment().startActivity(i);
                            view.reload();
                            return true;
                        }
                        return super.shouldOverrideUrlLoading(view, url);
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
}
