package ru.mos.polls.newprofile.vm;

import ru.mos.polls.databinding.LayoutAchievementTabProfileBinding;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.ui.fragment.AchievementTabFragment;

/**
 * Created by Trunks on 16.06.2017.
 */

public class AchievementTabFragmentVM extends BaseTabFragmentVM<AchievementTabFragment, LayoutAchievementTabProfileBinding> {
    public AchievementTabFragmentVM(AchievementTabFragment fragment, LayoutAchievementTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutAchievementTabProfileBinding binding) {
        recyclerView = binding.agUserProfileList;
        super.initialize(binding);
    }
}
