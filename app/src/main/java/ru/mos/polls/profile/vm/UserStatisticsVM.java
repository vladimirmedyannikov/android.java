package ru.mos.polls.profile.vm;


import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.UserStatsItemBinding;
import ru.mos.polls.profile.model.Statistics;

public class UserStatisticsVM extends RecyclerBaseViewModel<Statistics, UserStatsItemBinding> {

    public UserStatisticsVM(Statistics userStatistics) {
        super(userStatistics);
    }

    @Override
    public void onBind(UserStatsItemBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.setViewModel(this);
        viewDataBinding.executePendingBindings();
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.user_stats_item;
    }

    public String getValue() {
        return model.getValue();
    }

    public String getTitle() {
        return model.getTitle();
    }

}
