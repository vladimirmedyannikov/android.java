package ru.mos.polls.base.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by wlTrunks on 15.06.2017.
 */

public class BindingHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private B binding;

    public static <B extends ViewDataBinding> BindingHolder newInstance(@LayoutRes int layoutId, LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        final B vb = DataBindingUtil.inflate(inflater, layoutId, parent, attachToParent);
        return new BindingHolder(vb);
    }

    public BindingHolder(B binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void setBinding(B binding){
        this.binding = binding;
    }

    public B getBinding() {
        return binding;
    }
}