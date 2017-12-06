package ru.mos.polls.webview.vm;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import ru.mos.polls.BuildConfig;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.databinding.FragmentWebviewBinding;
import ru.mos.polls.rxhttp.session.Session;
import ru.mos.polls.util.NetworkUtils;
import ru.mos.polls.webview.ui.WebViewFragment;

/**
 * Created by Trunks on 04.12.2017.
 */

public class WebViewFragmentVM extends FragmentViewModel<WebViewFragment, FragmentWebviewBinding> {
    private static final String host = "release".equalsIgnoreCase(BuildConfig.BUILD_TYPE) ? "ag.mos.ru/poll/constructor" : "testing.ag.mos.ru/poll/constructor";
    private static final String urlPattern = "http://%s/catalog";
    private static final String url = String.format(urlPattern, host);
    private static final String cookiesPattern = "EMPSESSION=%s";

    WebView webView;
    ProgressBar loading;
    View rootConnectionError;
    View root;
    CookieManager cookieManager;

    public WebViewFragmentVM(WebViewFragment fragment, FragmentWebviewBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentWebviewBinding binding) {
        webView = binding.webView;
        loading = binding.loading;
        rootConnectionError = binding.layoutInternetConnectionLost.rootConnectionError;
        root = binding.root;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(host, getCookies(Session.get().getSession()));
        setWebViewSetting();
        setWebViewClient();
        webView.invokeZoomPicker();
        if (checkInternetConnection()) webView.loadUrl(url);
    }

    public void setWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {

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

    public void setWebViewSetting() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    public void setProgress(boolean visible) {
        loading.setVisibility(visible ? View.VISIBLE : View.GONE);
        webView.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    public static String getCookies(String sesionId) {
        return String.format(cookiesPattern, sesionId);
    }
}
