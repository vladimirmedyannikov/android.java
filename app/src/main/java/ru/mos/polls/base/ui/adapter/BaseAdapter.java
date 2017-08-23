package ru.mos.polls.base.ui.adapter;

import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import java.util.List;

import ru.mos.polls.BR;
import ru.mos.polls.base.ui.BindingHolder;


/**
 * Created by Trunks on 19.06.2017.
 */

public abstract class BaseAdapter<VM extends BaseObservable, VH extends BindingHolder, B extends ViewDataBinding, F> extends RecyclerView.Adapter<VH> {
    protected VM viewModel;
    protected VH viewHolder;
    protected B binding;
    protected List<F> list;


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getLayoutResources(), parent, false);
        VH vh = getVH(binding);
        return vh;
    }

    public abstract VH getVH(B binding);

    public abstract VM getVM(F obj, B binding);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        F obj = list.get(position);
        binding = (B) holder.getBinding();
        holder.getBinding().setVariable(getVariable(), getVM(obj, binding));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public int getVariable() {
        return BR.viewModel;
    }

    public abstract int getLayoutResources();
}
