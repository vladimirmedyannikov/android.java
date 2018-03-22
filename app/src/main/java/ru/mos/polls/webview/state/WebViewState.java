package ru.mos.polls.webview.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.webview.ui.WebViewFragment;

public class WebViewState extends ContentBelowToolbarState<WebViewState.WebViewParams> {

    public static WebViewState getStateForNew(String id, String title, String linkUrl) {
        return new WebViewState(title, linkUrl, id, true, true);
    }

    public WebViewState(String title, String linkUrl, String id, boolean onlyLoadFirstUrl, boolean isShareEnable) {
        super(new WebViewParams(title, linkUrl, id, onlyLoadFirstUrl, isShareEnable));
    }

    public WebViewState(String id, String title, String linkUrl) {
        super(new WebViewParams(title, linkUrl, id, false, true));
    }

    @Override
    protected JugglerFragment onConvertContent(WebViewParams params, @Nullable JugglerFragment fragment) {
        return WebViewFragment.getInstance(params.title, params.linkUrl, params.id, params.onlyLoadFirstUrl, params.isShareEnable);
    }

    @Override
    protected JugglerFragment onConvertToolbar(WebViewParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();

    }

    @Nullable
    @Override
    public String getTitle(Context context, WebViewParams params) {
        return params.title;
    }

    static class WebViewParams extends State.Params {
        String title;
        String linkUrl;
        String id;
        boolean onlyLoadFirstUrl;
        boolean isShareEnable;

        public WebViewParams(String title, String linkUrl, String id, boolean onlyLoadFirstUrl, boolean isShareEnable) {
            this.title = title;
            this.linkUrl = linkUrl;
            this.id = id;
            this.onlyLoadFirstUrl = onlyLoadFirstUrl;
            this.isShareEnable = isShareEnable;
        }
    }
}