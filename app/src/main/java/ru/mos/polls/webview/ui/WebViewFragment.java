package ru.mos.polls.webview.ui;

import android.os.Bundle;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentWebviewBinding;
import ru.mos.polls.webview.vm.WebViewFragmentVM;

public class WebViewFragment extends MenuBindingFragment<WebViewFragmentVM, FragmentWebviewBinding> {

    public static final String INFORMATION_TITLE = "information_title_extra";
    public static final String INFORMATION_URL = "information_url_extra";
    public static final String ONLY_LOAD_FIRST_URL = "only_load_first_url";
    public static final String ID = "id";
    public static final String IS_SHARE_ENABLE = "is_share_enable";

    public static WebViewFragment getInstance(String title, String linkUrl, String id, boolean onlyLoadFirstUrl, boolean isShareEnable) {
        WebViewFragment webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INFORMATION_TITLE, title);
        bundle.putString(INFORMATION_URL, linkUrl);
        bundle.putString(ID, id);
        bundle.putBoolean(ONLY_LOAD_FIRST_URL, onlyLoadFirstUrl);
        bundle.putBoolean(IS_SHARE_ENABLE, isShareEnable);
        webViewFragment.setArguments(bundle);
        return webViewFragment;
    }

    @Override
    protected WebViewFragmentVM onCreateViewModel(FragmentWebviewBinding binding) {
        return new WebViewFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getMenuResource() {
        return R.menu.share;
    }

    @Override
    public boolean onBackPressed() {
        if (getViewModel().webViewCanGoBack()) {
            getViewModel().webViewGoBack();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_webview;
    }
}
