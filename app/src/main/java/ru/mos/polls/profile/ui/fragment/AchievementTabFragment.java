package ru.mos.polls.profile.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentAchievementTabProfileBinding;
import ru.mos.polls.profile.vm.AchievementTabFragmentVM;

public class AchievementTabFragment extends NavigateFragment<AchievementTabFragmentVM, FragmentAchievementTabProfileBinding> {
    public static final String ARG_FRIEND_ID = "arg_friend_id";

    public static Fragment newInstance(int friendId) {
        AchievementTabFragment result = new AchievementTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRIEND_ID, friendId);
        result.setArguments(args);
        return result;
    }

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
