package ru.mos.polls.profile.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.profile.model.Achievements;
import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.profile.vm.AchievementVM;
import ru.mos.polls.profile.vm.OnAchievementClickListener;

/**
 * Created by Trunks on 23.06.2017.
 */

public class AchievementAdapter extends BaseRecyclerAdapter<AchievementVM> {
    OnAchievementClickListener listener;
    public static final int TYPE = 1;
    public AchievementAdapter(List<Achievements> list, OnAchievementClickListener listener) {
        this.listener = listener;
    }


    public void add(List<Achievements> achievements) {
        List<AchievementVM> content = new ArrayList<>();
        for (Achievements achievement : achievements) {
            AchievementVM achievementVM = new AchievementVM(achievement);
            achievementVM.setListener(listener);
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
