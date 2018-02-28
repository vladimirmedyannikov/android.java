package ru.mos.polls.profile.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentProfileQuestBinding;
import ru.mos.polls.profile.vm.ProfileQuestFragmentVM;

public class ProfileQuestFragment extends NavigateFragment<ProfileQuestFragmentVM, FragmentProfileQuestBinding> {
    @Override
    protected ProfileQuestFragmentVM onCreateViewModel(FragmentProfileQuestBinding binding) {
        return new ProfileQuestFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_profile_quest;
    }
}
