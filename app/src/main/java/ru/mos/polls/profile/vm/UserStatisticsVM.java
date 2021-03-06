package ru.mos.polls.profile.vm;


import ru.mos.elk.profile.Statistics;
import ru.mos.polls.databinding.UserStatsItemBinding;
import ru.mos.polls.base.vm.BaseVM;

/**
 * Created by wlTrunks on 15.06.2017.
 */

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
