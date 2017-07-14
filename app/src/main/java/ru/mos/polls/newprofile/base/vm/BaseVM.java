package ru.mos.polls.newprofile.base.vm;

import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;

/**
 * Created by wlTrunks on 14.07.2017.
 */

public abstract class BaseVM<M, VDB extends ViewDataBinding> extends BaseObservable{
    protected M model;
    protected VDB viewDataBinding;

    public BaseVM(M model, VDB viewDataBinding) {
        this.model = model;
        this.viewDataBinding = viewDataBinding;
    }
}
