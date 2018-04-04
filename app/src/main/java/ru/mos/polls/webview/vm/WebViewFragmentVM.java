package ru.mos.polls.webview.vm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.databinding.FragmentWebviewBinding;
import ru.mos.polls.model.NewsFindModel;
import ru.mos.polls.quests.controller.QuestsApiControllerRX;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;
import ru.mos.polls.rxhttp.session.Session;
import ru.mos.polls.service.NewsFind;
import ru.mos.polls.shop.PersistentConfig;
import ru.mos.polls.util.NetworkUtils;
import ru.mos.polls.util.UrlHelper;
import ru.mos.polls.webview.ui.WebViewFragment;

public class WebViewFragmentVM extends UIComponentFragmentViewModel<WebViewFragment, FragmentWebviewBinding> {

    private WebView webView;
    private ProgressBar loading;
    private View rootConnectionError;
    private View root;

    private String title;
    private String firstUrl;
    private String id;
    private boolean isOnlyLoadFirstUrl;
    private boolean isShareEnable;
    private boolean setCookie;
    private String shareUrl;
    CookieManager cookieManager;

    public WebViewFragmentVM(WebViewFragment fragment, FragmentWebviewBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentWebviewBinding binding) {
        webView = binding.webView;
        loading = binding.loading;
        rootConnectionError = binding.layoutInternetConnectionLost.rootConnectionError;
        binding.layoutInternetConnectionLost.internetLostReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternetConnection()) webView.loadUrl(firstUrl);
            }
        });
        root = binding.root;
        checkInternetConnection();
        if (getFragment().getArguments() != null) {
            title = getFragment().getArguments().getString(WebViewFragment.INFORMATION_TITLE, "");
            firstUrl = getFragment().getArguments().getString(WebViewFragment.INFORMATION_URL, "");
            isOnlyLoadFirstUrl = getFragment().getArguments().getBoolean(WebViewFragment.ONLY_LOAD_FIRST_URL, false);
            isShareEnable = getFragment().getArguments().getBoolean(WebViewFragment.IS_SHARE_ENABLE, true);
            setCookie = getFragment().getArguments().getBoolean(WebViewFragment.SET_COOKIE, true);
            id = getFragment().getArguments().getString(WebViewFragment.ID);
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setWebViewSetting();
        setWebViewClient();
        if (checkInternetConnection()) webView.loadUrl(firstUrl);
        tryHide();
        trySetUrlFromIntent();
    }

    @Override
    public void onCreateOptionsMenu() {
        super.onCreateOptionsMenu();
        getFragment().hideMenuItem(R.id.action_share);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().build();
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_share:
                Statistics.shareNews();
                GoogleStatistics.AGNavigation.shareNews();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                sendIntent.setType("text/plain");
                getFragment().startActivity(sendIntent);
                break;
            default:
                super.onOptionsItemSelected(menuItemId);
        }
    }

    public boolean webViewCanGoBack() {
        return webView.canGoBack();
    }

    public void webViewGoBack() {
        webView.goBack();
    }

    public void setWebViewClient() {
        webView.setWebChromeClient(new WebChromeClient());
        if (setCookie) setCookie();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
//                if (BuildConfig.BUILD_TYPE.equals("debug")) {
                handler.proceed("ag_test", "5U:bzH"); //тестовый контур запаролен
//                } else {
//                    super.onReceivedHttpAuthRequest(view, handler, host, realm);
//                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean urlAllowed = isUrlAllowedForLoad(url);
                boolean isOnline = isOnline(getFragment().getContext());
                boolean isFirstUrl = isFirstUrl(url);
                if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = newEmailIntent(getFragment().getContext(), mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    getFragment().startActivity(i);
                    view.reload();
                    return true;
                }
                /**
                 * если url не первый, то открываем в браузере
                 */
                if (((urlAllowed && isOnline) || !isFirstUrl) && !setCookie) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getFragment().startActivity(intent);
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
    }


    public void setCookie() {
        CookieSyncManager.createInstance(getActivity());
        cookieManager = CookieManager.getInstance();
        PersistentConfig persistentConfig = new PersistentConfig(getActivity());
        persistentConfig.setCookie(UrlHelper.getCookies(Session.get().getSession()));
        cookieManager.setCookie(UrlHelper.getHost(), persistentConfig.getCookieString());
        CookieSyncManager.getInstance().sync();
    }

    private void trySetUrlFromIntent() {
        UrlSchemeController.startWebView((BaseActivity) getFragment().getActivity(), new UrlSchemeController.LinkListener() {
            @Override
            public void onDetected(String title, String link, String newsId) {
                firstUrl = link;
                isOnlyLoadFirstUrl = true;
                WebViewFragmentVM.this.title = title;
                /**
                 * На всякий случай try-catch, мало ил тип id другой придет
                 */
                try {
                    if (newsId != null && !TextUtils.isEmpty(newsId)) {
                        if (isShareEnable) {
                            findShareUrl(Long.parseLong(newsId));
                        }
                        QuestsApiControllerRX.hideNews(disposables, getFragment().getContext(), Long.parseLong(newsId), null, Progressable.STUB);
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    /**
     * при сравнении удаляем протоколы http и/или https
     * т.к. они могут быть разные для
     *
     * @param url так и для {@link WebViewFragmentVM#firstUrl}
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

    public static Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    private void findShareUrl(long id) {
        HandlerApiResponseSubscriber<NewsFindModel> handler = new HandlerApiResponseSubscriber<NewsFindModel>() {
            @Override
            protected void onResult(NewsFindModel result) {
                shareUrl = result.getPublicSiteUrl();
                boolean isShareEnable = !TextUtils.isEmpty(shareUrl) && !"null".equalsIgnoreCase(shareUrl);
                if (isShareEnable) {
                    getFragment().showMenuItem(R.id.action_share);
                } else {
                    getFragment().hideMenuItem(R.id.action_share);
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

    private void tryHide() {
        if (!TextUtils.isEmpty(id) && !"null".equalsIgnoreCase(id))
            try {
                long newsId = Long.valueOf(id);
                if (isShareEnable) {
                    findShareUrl(newsId);
                }
                QuestsApiControllerRX.hideNews(disposables, getFragment().getContext(), newsId, null, Progressable.STUB);
            } catch (Exception ignored) {
            }
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
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.invokeZoomPicker();
    }

    public void setProgress(boolean visible) {
        loading.setVisibility(visible ? View.VISIBLE : View.GONE);
        webView.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
}
