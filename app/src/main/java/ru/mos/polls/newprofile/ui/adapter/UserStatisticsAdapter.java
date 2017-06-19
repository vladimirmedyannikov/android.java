package ru.mos.polls.newprofile.ui.adapter;

import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.databinding.UserStatsItemBinding;
import ru.mos.polls.newprofile.base.ui.adapter.BaseAdapter;
import ru.mos.polls.newprofile.model.UserStatistics;
import ru.mos.polls.newprofile.vm.UserStatisticsVM;

/**
 * Created by Trunks on 19.06.2017.
 */

public class UserStatisticsAdapter extends BaseAdapter<UserStatisticsVM, UserStatisticsHolder, UserStatsItemBinding, UserStatistics> {
    public UserStatisticsAdapter(List<UserStatistics> list) {
        this.list = list;
    }

    @Override
    public UserStatisticsHolder getVH(UserStatsItemBinding binding) {
        return new UserStatisticsHolder(binding);
    }

    @Override
    public UserStatisticsVM getVM(UserStatistics obj) {
        return new UserStatisticsVM(obj);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.user_stats_item;
    }

}
