package ru.mos.polls.base;

import android.databinding.ViewDataBinding;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.07.17 10:12.
 */

public abstract class BaseViewModel<M, VDB extends ViewDataBinding> {
    protected M model;
    protected VDB viewDataBinding;

    public BaseViewModel(M model, VDB viewDataBinding) {
        this(model);
        this.viewDataBinding = viewDataBinding;
    }

    public BaseViewModel(M model) {
        this.model = model;
    }

    protected abstract int getLayoutId();

}
