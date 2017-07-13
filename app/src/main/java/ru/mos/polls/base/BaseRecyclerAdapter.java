package ru.mos.polls.base;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import ru.mos.polls.BR;
import ru.mos.polls.newprofile.base.ui.BindingHolder;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.07.17 10:22.
 */

public class BaseRecyclerAdapter<RVM extends RecyclerBaseViewModel> extends RecyclerView.Adapter<BindingHolder> {
    protected List<RVM> list;

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getBindingHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        RVM viewModel = list.get(position);
        holder.getBinding().setVariable(BR.viewModel, viewModel.model);
        holder.getBinding().executePendingBindings();
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

    protected BindingHolder getBindingHolder(ViewGroup viewGroup, int viewType) {
        return new DefaultViewHolderProvider(list).getBindingHolder(viewGroup, viewType);
    }

    public interface BindingViewHolderProvider {
        BindingHolder getBindingHolder(ViewGroup parent, int viewType);
    }

    class DefaultViewHolderProvider implements BindingViewHolderProvider{
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
