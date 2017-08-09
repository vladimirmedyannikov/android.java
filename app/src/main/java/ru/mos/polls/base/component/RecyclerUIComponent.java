package ru.mos.polls.base.component;

import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;

public class RecyclerUIComponent<Adapter extends RecyclerView.Adapter> extends UIComponent {
    @BindView(R.id.list)
    protected RecyclerView recyclerView;

    @BindView(android.R.id.empty)
    protected View emptyView;

    private Adapter adapter;

    public RecyclerUIComponent(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onViewCreated(View layout) {
        super.onViewCreated(layout);
        ButterKnife.bind(this, layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    public void refreshUI() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setEmptyText(@StringRes int text) {
        setEmptyText(getContext().getString(text));
    }

    public void setEmptyText(String text) {
        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(text);
        } else throw new IllegalStateException("EmptyView must be an instance of TextView");
    }
}
