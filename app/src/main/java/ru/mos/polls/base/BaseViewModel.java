package ru.mos.polls.base;

import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.07.17 10:12.
 */

public abstract class BaseViewModel<M, VDB extends ViewDataBinding> extends BaseObservable {
    protected M model;
    protected VDB viewDataBinding;

    public BaseViewModel(M model, VDB viewDataBinding) {
        this(model);
        this.viewDataBinding = viewDataBinding;
    }

    public VDB getViewDataBinding() {
        return viewDataBinding;
    }

    public BaseViewModel(M model) {
        this.model = model;
    }

    public M getModel() {
        return model;
    }

    protected abstract int getLayoutId();

    public void setViewDataBinding(VDB viewDataBinding) {
        this.viewDataBinding = viewDataBinding;
    }
}
