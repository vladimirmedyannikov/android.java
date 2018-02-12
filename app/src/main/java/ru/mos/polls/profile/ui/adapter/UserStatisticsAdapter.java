package ru.mos.polls.profile.ui.adapter;

import java.util.List;

import ru.mos.polls.profile.model.Statistics;
import ru.mos.polls.R;
import ru.mos.polls.databinding.UserStatsItemBinding;
import ru.mos.polls.base.ui.adapter.BaseAdapter;
import ru.mos.polls.profile.vm.UserStatisticsVM;

/**
 * Created by Trunks on 19.06.2017.
 */

public class UserStatisticsAdapter extends BaseAdapter<UserStatisticsVM, UserStatisticsHolder, UserStatsItemBinding, Statistics> {
    public UserStatisticsAdapter(List<Statistics> list) {
        this.list = list;
    }

    @Override
    public UserStatisticsHolder getVH(UserStatsItemBinding binding) {
        return new UserStatisticsHolder(binding);
    }

    @Override
    public UserStatisticsVM getVM(Statistics obj, UserStatsItemBinding binding) {
        return new UserStatisticsVM(obj, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.user_stats_item;
    }

}
