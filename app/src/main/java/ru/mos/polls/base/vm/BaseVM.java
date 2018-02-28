package ru.mos.polls.base.vm;

import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;

public abstract class BaseVM<M, VDB extends ViewDataBinding> extends BaseObservable{
    protected M model;
    protected VDB viewDataBinding;

    public BaseVM(M model, VDB viewDataBinding) {
        this.model = model;
        this.viewDataBinding = viewDataBinding;
    }
}
