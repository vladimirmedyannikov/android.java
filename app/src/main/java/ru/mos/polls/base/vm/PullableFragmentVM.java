package ru.mos.polls.base.vm;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;


import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.ui.RecyclerScrollableController;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.rxhttp.rxapi.model.Page;


/**
 * Created by Trunks on 15.09.2017.
 * Базовый класс для запросов с пагинацией
 */
public abstract class PullableFragmentVM<F extends JugglerFragment, B extends ViewDataBinding, A extends BaseRecyclerAdapter> extends UIComponentFragmentViewModel<F, B> {
    public Page page;
    public RecyclerView recyclerView;
    public boolean isPaginationEnable;
    public A adapter;

    public PullableFragmentVM(F fragment, B binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(B binding) {
        UIhelper.setRecyclerList(recyclerView, getActivity());
        recyclerView.addOnScrollListener(getScrollableListener());
        recyclerView.setAdapter(adapter);
        page = new Page();
        isPaginationEnable = true;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        progressable = getProgressable();
        doRequest();
    }

    public RecyclerView.OnScrollListener getScrollableListener() {
        RecyclerScrollableController.OnLastItemVisibleListener onLastItemVisibleListener
                = () -> {
            if (isPaginationEnable) {
                isPaginationEnable = false;
                page.increment();
                doRequest();
            }
        };
        return new RecyclerScrollableController(onLastItemVisibleListener);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new PullableUIComponent(() -> {
                    progressable = getPullableProgressable();
                    page.reset();
                    clearList();
                    doRequest();
                }))
                .with(new ProgressableUIComponent())
                .build();
    }

    public void clearList() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void progressPull() {
        adapter.notifyDataSetChanged();
        progressable.end();
        isPaginationEnable = true;
    }

    public abstract void doRequest();
}
