package ru.mos.polls.base;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BR;
import ru.mos.polls.base.ui.BindingHolder;

/**
 * Базовый адаптер для отображения списка {@link RecyclerBaseViewModel}
 *
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.07.17 10:22.
 */

public class BaseRecyclerAdapter<RVM extends RecyclerBaseViewModel> extends RecyclerView.Adapter<BindingHolder> {
    protected List<RVM> list = new ArrayList<>();

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getBindingHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        RVM viewModel = list.get(position);
        holder.getBinding().setVariable(viewModel.getVariableId(), viewModel.model);
        holder.getBinding().executePendingBindings();
        viewModel.onBind(holder.getBinding());
    }

    @Override
    public int getItemViewType(int position) {
        return list == null ? super.getItemViewType(position) : list.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public int getVariable() {
        return BR.viewModel;
    }

    public boolean hasData() {
        return getItemCount() > 0;
    }

    public void setData(List<RVM> list) {
        clear();
        addData(list);
    }

    public void addData(List<RVM> list) {
        this.list.addAll(list);
    }

    public void add(RVM item) {
        list.add(item);
    }

    public boolean remove(RVM item) {
        return list.remove(item);
    }

    public void clear() {
        list.clear();
    }

    protected BindingHolder getBindingHolder(ViewGroup viewGroup, int viewType) {
        return new DefaultViewHolderProvider(list).getBindingHolder(viewGroup, viewType);
    }

    /**
     * Спецификация для предоставления {@link android.support.v7.widget.RecyclerView.ViewHolder}
     */
    public interface BindingViewHolderProvider {
        BindingHolder getBindingHolder(ViewGroup parent, int viewType);
    }

    /**
     * Реализация {@link BindingViewHolderProvider} по умолчанию,
     * поддреживает различные типы разметок, определяемых {@link RecyclerBaseViewModel}
     */
     public class DefaultViewHolderProvider implements BindingViewHolderProvider{
        private List<RVM> list;

        public DefaultViewHolderProvider(List<RVM> list) {
            this.list = list;
        }

        public BindingHolder getBindingHolder(ViewGroup parent, int viewType) {
            BindingHolder result = null;
            for (RecyclerBaseViewModel vm : list) {
                if (vm.getViewType() == viewType) {
                    result = vm.getBindingHolder(parent, false);
                    break;
                }
            }
            return result;
        }
    }
}
