package ru.mos.polls.changepassword.ui;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.changepassword.vm.ChangePasswordFragmentVM;
import ru.mos.polls.databinding.FragmentChangepasswordBinding;


public class ChangePasswordFragment extends MenuBindingFragment<ChangePasswordFragmentVM, FragmentChangepasswordBinding> {


    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment f = new ChangePasswordFragment();
        return f;
    }

    @Override
    protected ChangePasswordFragmentVM onCreateViewModel(FragmentChangepasswordBinding binding) {
        return new ChangePasswordFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_changepassword;
    }

    @Override
    public int getMenuResource() {
        return R.menu.confirm;
    }
}
