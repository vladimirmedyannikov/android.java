package ru.mos.polls.profile.ui.fragment;

import android.os.Bundle;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentAchievementBinding;
import ru.mos.polls.profile.vm.AchievementFragmentVM;

public class AchievementFragment extends NavigateFragment<AchievementFragmentVM, FragmentAchievementBinding>{
    public static final String EXTRA_ACHIEVEMENT_ID = "extra_achievement";
    public static final String EXTRA_IS_OWN = "extra_is_own";

    public static AchievementFragment instance(String id, boolean isOwn) {
        AchievementFragment res = new AchievementFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ACHIEVEMENT_ID, id);
        bundle.putBoolean(EXTRA_IS_OWN, isOwn);
        res.setArguments(bundle);
        return res;
    }

    @Override
    protected AchievementFragmentVM onCreateViewModel(FragmentAchievementBinding binding) {
        return new AchievementFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_achievement;
    }

    public String getExtraAchievementId() {
        if (getArguments() != null) {
            return getArguments().getString(EXTRA_ACHIEVEMENT_ID);
        }
        return "";
    }

    public boolean getExtraIsOwn() {
        if (getArguments() != null) {
            return getArguments().getBoolean(EXTRA_IS_OWN);
        }
        return true;
    }
}