package ru.mos.polls.base.component;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.view.View;

import butterknife.ButterKnife;

public abstract class UIComponent implements ComponentHolder {
    private Context context;
    private UIComponentHolder uiComponentHolder;

    public final void onViewCreated(Context context, View layout) {
        this.context = context;
        uiComponentHolder = createComponentHolder();
        uiComponentHolder.onViewCreated(context, layout);
        onViewCreated(layout);
    }

    @CallSuper
    public void onViewCreated(View layout) {
        ButterKnife.bind(this, layout);
    }

    @CallSuper
    public void onDestroyView() {
        uiComponentHolder.onDestroyView();
    }

    protected final Context getContext() {
        return context;
    }

    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().build();
    }

    @Override
    public <T extends UIComponent> T getComponent(Class<T> componentClass) {
        return uiComponentHolder.getComponent(componentClass);
    }

    public final UIComponentHolder getUiComponentHolder() {
        return uiComponentHolder;
    }
}
