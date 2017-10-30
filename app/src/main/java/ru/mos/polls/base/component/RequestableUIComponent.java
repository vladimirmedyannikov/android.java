package ru.mos.polls.base.component;

import android.view.View;

import butterknife.BindView;
import ru.mos.polls.R;


public class RequestableUIComponent extends UIComponent {
    @BindView(R.id.rootConnectionError)
    protected View rootConnectionError;

    @Override
    public void onViewCreated(View layout) {
        super.onViewCreated(layout);
    }

    public void showErrorConnetionView() {
        rootConnectionError.setVisibility(View.VISIBLE);
    }

    public void hideErrorConnetionView() {
        rootConnectionError.setVisibility(View.GONE);
    }
}
