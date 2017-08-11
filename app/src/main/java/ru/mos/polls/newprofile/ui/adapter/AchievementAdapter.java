package ru.mos.polls.newprofile.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.profile.Achievements;
import ru.mos.polls.R;
import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemAchievementBinding;
import ru.mos.polls.newprofile.base.ui.BindingHolder;
import ru.mos.polls.newprofile.base.ui.adapter.BaseAdapter;
import ru.mos.polls.newprofile.vm.AchievementVM;
import ru.mos.polls.newprofile.vm.OnAchievementClickListener;

/**
 * Created by Trunks on 23.06.2017.
 */

public class AchievementAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {
    OnAchievementClickListener listener;
    public static final int TYPE = 1;
    public AchievementAdapter(List<Achievements> list, OnAchievementClickListener listener) {
        this.listener = listener;
    }


    public void add(List<Achievements> achievements) {
        List<RecyclerBaseViewModel> content = new ArrayList<>();
        for (Achievements achievement : achievements) {
            AchievementVM achievementVM = new AchievementVM(achievement);
            achievementVM.setListener(listener);
//            achievementVM.getViewDataBinding().setListener(listener);
            content.add(achievementVM);
        }
        addData(content);
    }


    public void add(Achievements achievement) {
        AchievementVM achievementVM = new AchievementVM(achievement);
        achievementVM.getViewDataBinding().setListener(listener);
        add(achievementVM);
    }
}
