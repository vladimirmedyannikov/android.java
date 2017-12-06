package ru.mos.polls.webview.state;

import android.content.Context;
import android.support.annotation.Nullable;


import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.webview.ui.WebViewFragment;

/**
 * Created by Trunks on 04.07.2017.
 */

public class WebViewState extends ContentBelowToolbarState<WebViewState.WebViewParams> {

    public WebViewState() {
        super(new WebViewParams());
    }

    @Override
    protected JugglerFragment onConvertContent(WebViewParams params, @Nullable JugglerFragment fragment) {
        return new WebViewFragment();
    }

    @Override
    protected JugglerFragment onConvertToolbar(WebViewParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();

    }

    @Nullable
    @Override
    public String getTitle(Context context, WebViewParams params) {
        return "Заявка на голосование";
    }

    static class WebViewParams extends State.Params {

        public WebViewParams() {
        }

    }
}