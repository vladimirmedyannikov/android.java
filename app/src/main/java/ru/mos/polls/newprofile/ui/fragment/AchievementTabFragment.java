package ru.mos.polls.newprofile.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentAchievementTabProfileBinding;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.newprofile.vm.AchievementTabFragmentVM;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class AchievementTabFragment extends BindingFragment<AchievementTabFragmentVM, FragmentAchievementTabProfileBinding> {
    @Override
    protected AchievementTabFragmentVM onCreateViewModel(FragmentAchievementTabProfileBinding binding) {
        return new AchievementTabFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_achievement_tab_profile;
    }
}
