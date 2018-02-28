package ru.mos.polls.profile.vm;


import ru.mos.polls.base.vm.BaseVM;
import ru.mos.polls.databinding.UserStatsItemBinding;
import ru.mos.polls.profile.model.Statistics;

public class UserStatisticsVM extends BaseVM<Statistics,UserStatsItemBinding> {

    public UserStatisticsVM(Statistics userStatistics, UserStatsItemBinding binding) {
        super(userStatistics,binding);
    }

    public String getValue() {
        return model.getValue();
    }

    public String getTitle() {
        return model.getTitle();
    }

}
