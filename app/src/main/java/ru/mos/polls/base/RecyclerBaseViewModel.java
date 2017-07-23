package ru.mos.polls.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ru.mos.polls.newprofile.base.ui.BindingHolder;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.07.17 12:45.
 */

public abstract class RecyclerBaseViewModel<M, VDB extends ViewDataBinding> extends BaseViewModel<M, VDB>{

    public RecyclerBaseViewModel(M model, VDB viewDataBinding) {
        super(model, viewDataBinding);
    }

    public RecyclerBaseViewModel(M model) {
        super(model);
    }

    public abstract int getViewType();

    protected BindingHolder getBindingHolder(ViewGroup parent, boolean attachToParent) {
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                getLayoutId(),
                parent,
                attachToParent);
        return new BindingHolder(viewDataBinding);
    }

}
