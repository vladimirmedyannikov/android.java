package ru.mos.polls.newprofile.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutUserTabProfileBinding;
import ru.mos.polls.newprofile.base.ui.BindingFragment;
import ru.mos.polls.newprofile.vm.UserTabFragmentVM;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class UserTabFragment extends BindingFragment<UserTabFragmentVM, LayoutUserTabProfileBinding> {

    public static UserTabFragment newInstance() {
        UserTabFragment f = new UserTabFragment();
        return f;
    }


    public UserTabFragment() {
    }

    @Override
    protected UserTabFragmentVM onCreateViewModel(LayoutUserTabProfileBinding binding) {
        return new UserTabFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_user_tab_profile;
    }
}
