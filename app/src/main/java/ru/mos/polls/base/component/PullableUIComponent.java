package ru.mos.polls.base.component;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;

public class PullableUIComponent extends UIComponent implements Progressable {
    @BindView(R.id.swipe)
    protected SwipeRefreshLayout swipeLayout;

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    public PullableUIComponent(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    @Override
    public void onViewCreated(View layout) {
        super.onViewCreated(layout);
        ButterKnife.bind(this, layout);
        swipeLayout.setOnRefreshListener(onRefreshListener);
    }

    @Override
    public void begin() {
        swipeLayout.setRefreshing(true);
    }

    @Override
    public void end() {
        swipeLayout.setRefreshing(false);
    }
}
