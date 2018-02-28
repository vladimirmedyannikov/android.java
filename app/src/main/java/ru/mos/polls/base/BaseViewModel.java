package ru.mos.polls.base;

import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;


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

    public abstract int getLayoutId();

    public void setViewDataBinding(VDB viewDataBinding) {
        this.viewDataBinding = viewDataBinding;
    }
}
