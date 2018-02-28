package ru.mos.polls.profile.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentNewProfileBinding;
import ru.mos.polls.profile.vm.ProfileFragmentVM;

public class ProfileFragment extends BindingFragment<ProfileFragmentVM, FragmentNewProfileBinding> {

    public static final int PAGE_START_PROFILE = 0;
    public static final int PAGE_ACHIEVEMENTS_PROFILE = 1;
    public static final int PAGE_INFO_PROFILE = 2;
    private static final String ARG_PAGE = "ru.mos.polls.newprofile.ui.fragment.arg_page";

    public static ProfileFragment newInstance(int page) {
        ProfileFragment f = new ProfileFragment();
        Bundle arg = new Bundle(1);
        arg.putInt (ARG_PAGE, page);
        f.setArguments(arg);
        return f;
    }

    public ProfileFragment() {
    }

    public int getStartPage() {
        return getArguments().getInt(ARG_PAGE);
    }

    @Override
    protected ProfileFragmentVM onCreateViewModel(FragmentNewProfileBinding binding) {
        return new ProfileFragmentVM(this, getBinding());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.mainmenu_profile);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_new_profile;
    }
}
