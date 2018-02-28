package ru.mos.polls.base.component;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.RecyclerScrollableController;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.Page;


public abstract class PaginationUIComponent<Result> extends UIComponent {

    @BindView(R.id.list)
    protected RecyclerView recyclerView;

    private Page page = new Page();
    private boolean isPaginationEnable = true;

    @Override
    public void onViewCreated(View layout) {
        super.onViewCreated(layout);
        ButterKnife.bind(this, layout);
        recyclerView.addOnScrollListener(getScrollable());
    }

    public void reset() {
        page.reset();
        isPaginationEnable = true;
    }

    protected RecyclerView.OnScrollListener getScrollable() {
        RecyclerScrollableController.OnLastItemVisibleListener onLastItemVisibleListener = () -> {
            if (isPaginationEnable) {
                page.increment();
                onPaging(page);
            }
        };
        return new RecyclerScrollableController(onLastItemVisibleListener);
    }

    public HandlerApiResponseSubscriber<Result> getHandler() {
        isPaginationEnable = false;
        return new HandlerApiResponseSubscriber<Result>() {
            @Override
            protected void onResult(Result result) {
                isPaginationEnable = checkPagingEnable(result);
                onPagingSuccessComplete(result);
            }
        };
    }


    protected abstract void onPaging(Page page);

    protected abstract boolean checkPagingEnable(Result result);

    protected abstract void onPagingSuccessComplete(Result result);

}
