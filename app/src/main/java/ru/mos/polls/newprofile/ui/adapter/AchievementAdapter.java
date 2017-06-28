package ru.mos.polls.newprofile.ui.adapter;

import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.databinding.ItemAchievementBinding;
import ru.mos.polls.newprofile.base.ui.BindingHolder;
import ru.mos.polls.newprofile.base.ui.adapter.BaseAdapter;
import ru.mos.polls.newprofile.model.Achievement;
import ru.mos.polls.newprofile.vm.AchievementVM;

/**
 * Created by Trunks on 23.06.2017.
 */

public class AchievementAdapter extends BaseAdapter<AchievementVM, BindingHolder<ItemAchievementBinding>, ItemAchievementBinding, Achievement> {

    public AchievementAdapter(List<Achievement> list) {
        this.list = list;
    }

    @Override
    public BindingHolder<ItemAchievementBinding> getVH(ItemAchievementBinding binding) {
        return new BindingHolder<>(binding);
    }

    @Override
    public AchievementVM getVM(Achievement obj, ItemAchievementBinding binding) {
        return new AchievementVM(obj, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.item_achievement;
    }
}
