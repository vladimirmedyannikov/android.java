package ru.mos.polls.newprofile.vm;

import android.databinding.BaseObservable;

import ru.mos.polls.databinding.UserStatsItemBinding;
import ru.mos.polls.newprofile.model.UserStatistics;

/**
 * Created by wlTrunks on 15.06.2017.
 */

public class UserStatisticsVM extends BaseObservable {

    private UserStatistics userStatistics;
    private UserStatsItemBinding binding;

    public UserStatisticsVM(UserStatistics userStatistics, UserStatsItemBinding binding) {
        this.userStatistics = userStatistics;
        this.binding = binding;
    }

    public String getValue() {
        return userStatistics.getValue();
    }

    public String getTitle() {
        return userStatistics.getTitle();
    }
}
