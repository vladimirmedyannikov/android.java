package ru.mos.polls.newprofile.ui.vm;

import ru.mos.polls.databinding.LayoutUserTabProfileBinding;
import ru.mos.polls.newprofile.base.FragmentViewModel;
import ru.mos.polls.newprofile.ui.fragment.UserTabFragment;

/**
 * Created by Trunks on 08.06.2017.
 */

public class UserTabFragmentVM extends FragmentViewModel<UserTabFragment, LayoutUserTabProfileBinding> {
    public UserTabFragmentVM(UserTabFragment fragment, LayoutUserTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutUserTabProfileBinding binding) {

    }
}
