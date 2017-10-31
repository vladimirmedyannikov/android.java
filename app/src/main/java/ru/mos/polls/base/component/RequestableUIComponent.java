package ru.mos.polls.base.component;

import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import ru.mos.polls.R;
import ru.mos.polls.base.vm.RequestInterface;


public class RequestableUIComponent extends UIComponent {
    public RequestableUIComponent(RequestInterface requestInterface) {
        this.requestInterface = requestInterface;
    }

    RequestInterface requestInterface;

    @BindView(R.id.rootConnectionError)
    protected View rootConnectionError;

    @Override
    public void onViewCreated(View layout) {
        super.onViewCreated(layout);
    }

    public void showErrorConnetionView() {
        rootConnectionError.setVisibility(View.VISIBLE);
    }

    public void hideErrorConnectionView() {
        rootConnectionError.setVisibility(View.GONE);
    }

    @OnClick(R.id.internet_lost_reload)
    public void onReloadClick() {
        if (requestInterface != null) requestInterface.reload();
    }

    public boolean isRootConnectionVisible() {
        return rootConnectionError.getVisibility() == View.VISIBLE;
    }
}
