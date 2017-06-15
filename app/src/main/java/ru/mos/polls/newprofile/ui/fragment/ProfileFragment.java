package ru.mos.polls.newprofile.ui.fragment;


import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewProfileBinding;
import ru.mos.polls.newprofile.base.ui.BindingFragment;
import ru.mos.polls.newprofile.vm.ProfileFragmentVM;

/**
 * Created by Trunks on 06.06.2017.
 */

public class ProfileFragment extends BindingFragment<ProfileFragmentVM, LayoutNewProfileBinding> {

    public static ProfileFragment newInstance() {
        ProfileFragment f = new ProfileFragment();
        return f;
    }

    public ProfileFragment() {
    }

    @Override
    protected ProfileFragmentVM onCreateViewModel(LayoutNewProfileBinding binding) {
        return new ProfileFragmentVM(this, getBinding());
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_new_profile;
    }
}
