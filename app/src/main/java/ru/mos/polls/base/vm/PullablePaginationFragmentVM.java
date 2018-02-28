package ru.mos.polls.base.vm;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.component.RequestableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.ui.RecyclerScrollableController;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.util.NetworkUtils;


/**
 * Базовый класс для запросов с пагинацией
 */
public abstract class PullablePaginationFragmentVM<F extends JugglerFragment,
        B extends ViewDataBinding,
        A extends BaseRecyclerAdapter> extends UIComponentFragmentViewModel<F, B> implements RequestInterface {

    protected Page page;
    protected RecyclerView recyclerView;
    protected boolean isPaginationEnable;
    protected A adapter;
    protected RecyclerUIComponent recyclerUIComponent;
    protected RequestableUIComponent requestableUIComponent;
    protected PullableUIComponent pullableUIComponent;

    public PullablePaginationFragmentVM(F fragment, B binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(B binding) {
        UIhelper.setRecyclerList(recyclerView, getActivity());
        recyclerView.addOnScrollListener(getScrollableListener());
        recyclerView.setAdapter(adapter);
        page = new Page();
        isPaginationEnable = true;
        recyclerUIComponent = new RecyclerUIComponent(adapter);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        progressable = getProgressable();
        if (checkInternetConnection()) {
            doRequest();
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
        if (requestableUIComponent.isRootConnectionVisible()) {
            requestableUIComponent.hideErrorConnectionView();
        }
    }

    public void setErrorConneсtionView() {
        recyclerUIComponent.hideViews();
        requestableUIComponent.showErrorConnetionView();
        pullableUIComponent.end();
    }

    public RecyclerView.OnScrollListener getScrollableListener() {
        RecyclerScrollableController.OnLastItemVisibleListener onLastItemVisibleListener
                = this::manualPaginationIsDown;
        return new RecyclerScrollableController(onLastItemVisibleListener);
    }

    public void manualPaginationIsDown() {
        if (isPaginationEnable) {
            isPaginationEnable = false;
            page.increment();
            doRequest();
        }
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(pullableUIComponent = new PullableUIComponent(() -> {
                    resetData();
                }))
                .with(requestableUIComponent = new RequestableUIComponent(this))
                .with(new ProgressableUIComponent())
                .with(recyclerUIComponent)
                .build();
    }

    @Override
    public void reload() {
        resetData();
    }

    public void resetData() {
        if (checkInternetConnection()) {
            progressable = getPullableProgressable();
            page.reset();
            adapter.clear();
            doRequest();
        }
    }

    public abstract void doRequest();
}
