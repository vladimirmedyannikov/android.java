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
    /**
     * Если не указан объект в качестве ViewModel в разметке в теге  data
     */
    public static final int EMPTY_VARIABLE_ID = -1;

    public RecyclerBaseViewModel(M model, VDB viewDataBinding) {
        super(model, viewDataBinding);
    }

    public RecyclerBaseViewModel(M model) {
        super(model);
    }

    public int getVariableId() {
        return EMPTY_VARIABLE_ID;
    }

    protected BindingHolder getBindingHolder(ViewGroup parent, boolean attachToParent) {
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                getLayoutId(),
                parent,
                attachToParent);
        return new BindingHolder(viewDataBinding);
    }

    /**
     * Отрисовка элемента списка в "ручном режиме",
     * то есть тут можно выполнить те действия,
     * которые не получается выполнить в методе по каким-либо причинам {@link ViewDataBinding#setVariable(int, Object)}
     * при inflate элемента списка, см. {@link BaseRecyclerAdapter#onBindViewHolder(BindingHolder, int)}
     *
     * @param viewDataBinding объект {@link ViewDataBinding}
     */
    public void onBind(VDB viewDataBinding) {
    }

    public abstract int getViewType();

}
