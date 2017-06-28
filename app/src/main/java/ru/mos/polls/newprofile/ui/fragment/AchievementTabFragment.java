package ru.mos.polls.newprofile.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutAchievementTabProfileBinding;
import ru.mos.polls.newprofile.base.ui.BindingFragment;
import ru.mos.polls.newprofile.vm.AchievementTabFragmentVM;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class AchievementTabFragment extends BindingFragment<AchievementTabFragmentVM, LayoutAchievementTabProfileBinding> {
    @Override
    protected AchievementTabFragmentVM onCreateViewModel(LayoutAchievementTabProfileBinding binding) {
        return new AchievementTabFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_achievement_tab_profile;
    }
}
