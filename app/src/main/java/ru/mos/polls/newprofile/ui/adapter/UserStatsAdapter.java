package ru.mos.polls.newprofile.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.databinding.UserStatsItemBinding;
import ru.mos.polls.newprofile.model.UserStatistics;
import ru.mos.polls.newprofile.base.ui.BindingHolder;
import ru.mos.polls.newprofile.vm.UserStatisticsVM;


/**
 * Created by wlTrunks on 15.06.2017.
 */

public class UserStatsAdapter extends RecyclerView.Adapter<UserStatsAdapter.UserStatsHolder> {

    private List<UserStatistics> list;

    public UserStatsAdapter(List<UserStatistics> list) {
        this.list = list;
    }

    @Override
    public UserStatsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BindingHolder holder = BindingHolder.newInstance(R.layout.user_stats_item, LayoutInflater.from(parent.getContext()), parent, false);
        return new UserStatsHolder((UserStatsItemBinding) holder.getBinding());
    }

    @Override
    public void onBindViewHolder(UserStatsHolder holder, int position) {
        UserStatsItemBinding usi = holder.getBinding();
        usi.setViewModel(new UserStatisticsVM(list.get(position)));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class UserStatsHolder extends BindingHolder<UserStatsItemBinding> {

        UserStatsHolder(UserStatsItemBinding binding) {
            super(binding);
        }
    }
}
