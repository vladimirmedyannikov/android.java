package ru.mos.polls.profile.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.profile.model.Statistics;
import ru.mos.polls.profile.vm.UserStatisticsVM;

public class UserStatisticsAdapter extends BaseRecyclerAdapter<UserStatisticsVM> {
    public UserStatisticsAdapter(List<Statistics> list) {
        add(list);
    }

    public void add(List<Statistics> list) {
        List<UserStatisticsVM> content = new ArrayList<>();
        for (Statistics item : list) {
            UserStatisticsVM userStatisticsVM = new UserStatisticsVM(item);
            content.add(userStatisticsVM);
        }
        addData(content);
    }
}
